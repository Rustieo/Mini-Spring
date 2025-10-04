package com.minis.test.service;

import com.minis.batis.SqlSession;
import com.minis.batis.SqlSessionFactory;
import com.minis.jdbc.core.PreparedStatementCallback;
import com.minis.test.entity.User;

import java.sql.ResultSet;


public class UserService {
    private SqlSessionFactory sqlSessionFactory;

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public User getUserById(int id) {
        SqlSession sqlSession = this.sqlSessionFactory.openSession();
        Object obj = sqlSession.selectOne(
                "com.minis.test.entity.User.getUserInfo", // 对应 User_Mapper.xml 的 namespace.id
                new Object[]{id},
                (PreparedStatementCallback) pstmt -> {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            User u = new User();
                            u.setId(rs.getInt("id"));
                            u.setName(rs.getString("name"));
                            java.sql.Date d = rs.getDate("birthday");
                            if (d != null) {
                                u.setBirthday(new java.util.Date(d.getTime()));
                            }
                            return u;
                        }
                        return null;
                    }
                }
        );
        return (User) obj;
    }
}
