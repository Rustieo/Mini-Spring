package com.minis.batis;

import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.PreparedStatementCallback;
import com.minis.jdbc.core.RowMapper;

public interface SqlSession {
	void setJdbcTemplate(JdbcTemplate jdbcTemplate);
	void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);
	Object selectOne(String sqlid, Object[] args, PreparedStatementCallback pstmtcallback);
	// New: selectOne with RowMapper
	<T> T selectOne(String sqlId, Object[] args, RowMapper<T> rowMapper);
	// New: dynamic proxy mapper
	<T> T getMapper(Class<T> mapperInterface);
}
