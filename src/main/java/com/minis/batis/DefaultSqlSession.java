package com.minis.batis;

import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.PreparedStatementCallback;
import com.minis.jdbc.core.RowMapper;
import com.minis.jdbc.core.BeanPropertyRowMapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class DefaultSqlSession implements SqlSession{
	JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public JdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	SqlSessionFactory sqlSessionFactory;
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}
	
	@Override
	public Object selectOne(String sqlid, Object[] args, PreparedStatementCallback pstmtcallback) {
		String sql = this.sqlSessionFactory.getMapperNode(sqlid).getSql();
		return jdbcTemplate.query(sql, args, pstmtcallback);
	}

	@Override
	public <T> T selectOne(String sqlId, Object[] args, RowMapper<T> rowMapper) {
		String sql = this.sqlSessionFactory.getMapperNode(sqlId).getSql();
		List<T> list = jdbcTemplate.query(sql, args, rowMapper);
		return (list != null && !list.isEmpty()) ? list.get(0) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> mapperInterface) {
		ClassLoader cl = mapperInterface.getClassLoader();
		Class<?>[] ifaces = new Class<?>[]{mapperInterface};
		InvocationHandler h = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// Handle Object methods explicitly
				String name = method.getName();
				if (method.getDeclaringClass() == Object.class) {
					if ("toString".equals(name)) return mapperInterface.getName() + " proxy";
					if ("hashCode".equals(name)) return System.identityHashCode(proxy);
					if ("equals".equals(name)) return proxy == args[0];
				}
				String sqlId = mapperInterface.getName() + "." + method.getName();
				MapperNode node = sqlSessionFactory.getMapperNode(sqlId);
				if (node == null) {
					throw new IllegalStateException("MapperNode not found for: " + sqlId);
				}
				String sql = node.getSql();
				Class<?> rt = method.getReturnType();
				// Only support single-row select for now
				if (rt == Void.TYPE) return null;
				// resolve result type from xml if provided
				Class<?> mappedClass = rt;
				if (node.getResultType() != null && !node.getResultType().isEmpty()) {
					mappedClass = Class.forName(node.getResultType());
				}
				RowMapper<?> rm = new BeanPropertyRowMapper<>(mappedClass);
				List<?> list = jdbcTemplate.query(sql, (Object[]) args, (RowMapper<Object>) rm);
				return (list != null && !list.isEmpty()) ? list.get(0) : null;
			}
		};
		return (T) Proxy.newProxyInstance(cl, ifaces, h);
	}
}
