package com.minis.test.service;

import com.minis.batis.SqlSession;
import com.minis.batis.SqlSessionFactory;
import com.minis.jdbc.core.BeanPropertyRowMapper;
import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.RowMapper;
import com.minis.test.entity.PageResult;
import com.minis.test.entity.User;
import com.minis.test.mapper.UserMapper;

import java.util.List;

public class UserService {
    private SqlSessionFactory sqlSessionFactory;
    private JdbcTemplate jdbcTemplate;

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUserById(int id) {
        SqlSession sqlSession = this.sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        return mapper.getUserInfo(id);
    }

    public PageResult<User> pageUsers(Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int offset = (p - 1) * s;

        // SQL ids in mapper
        String listSqlId = "com.minis.test.mapper.UserMapper.listPage";
        String countSqlId = "com.minis.test.mapper.UserMapper.countAll";

        String listSql = this.sqlSessionFactory.getMapperNode(listSqlId).getSql();
        String countSql = this.sqlSessionFactory.getMapperNode(countSqlId).getSql();

        List<User> list = this.jdbcTemplate.query(listSql, new Object[]{s, offset}, new BeanPropertyRowMapper<>(User.class));
        Integer total = this.jdbcTemplate.queryForObject(countSql, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(java.sql.ResultSet rs, int rowNum) {
                try { return rs.getInt(1); } catch (Exception e) { throw new RuntimeException(e); }
            }
        });
        long t = (total == null) ? 0L : total.longValue();
        return new PageResult<>(p, s, t, list);
    }
}
