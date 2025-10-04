package com.minis.jdbc.core;


import com.minis.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
//public abstract class JdbcTemplate
public class JdbcTemplate {

    @Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public JdbcTemplate() {
    }

    // 新增：便捷构造器
    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void assertDataSource() {
        if (this.dataSource == null) {
            throw new IllegalStateException("DataSource must not be null");
        }
    }

    public Object query(StatementCallback stmtcallback) {
        // 使用 try-with-resources 确保资源关闭
        try {
            assertDataSource();
            try (Connection con = dataSource.getConnection();
                 Statement stmt = con.createStatement()) {
                return stmtcallback.doInStatement(stmt);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 与原实现保持一致：异常时返回 null
        return null;
    }
    //把statement传到回调函数中,然后返回回调函数处理的结果

    public Object query(String sql, Object[] args, PreparedStatementCallback pstmtcallback) {
        try {
            assertDataSource();
            try (Connection con = dataSource.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
                argumentSetter.setValues(pstmt);
                return pstmtcallback.doInPreparedStatement(pstmt);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增：便捷重载（无参数）
    public Object query(String sql, PreparedStatementCallback pstmtcallback) {
        return query(sql, null, pstmtcallback);
    }

    //自己约定一个映射关系,方法按照这个映射关系返回对应的list.
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        RowMapperResultSetExtractor<T> resultExtractor = new RowMapperResultSetExtractor<>(rowMapper);
        try {
            assertDataSource();
            try (Connection con = dataSource.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
                argumentSetter.setValues(pstmt);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return resultExtractor.extractData(rs);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增：便捷重载（无参数）
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return query(sql, null, rowMapper);
    }

    // 新增：返回单对象（多行取第一行，零行返回 null）
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) {
        List<T> list = query(sql, args, rowMapper);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    // 新增：便捷重载（无参数）
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        return queryForObject(sql, null, rowMapper);
    }

    // 新增：更新语句
    public int update(String sql, Object[] args) {
        try {
            assertDataSource();
            try (Connection con = dataSource.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
                argumentSetter.setValues(pstmt);
                return pstmt.executeUpdate();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // 失败返回 -1 区分于 0 行更新
        return -1;
    }

    // 新增：便捷重载
    public int update(String sql) {
        // 明确调用到 Object[] 重载，避免歧义
        return update(sql, (Object[]) null);
    }

    // 新增：批量更新
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        if (batchArgs == null || batchArgs.isEmpty()) {
            return new int[0];
        }
        try {
            assertDataSource();
            try (Connection con = dataSource.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                for (Object[] args : batchArgs) {
                    ArgumentPreparedStatementSetter setter = new ArgumentPreparedStatementSetter(args);
                    setter.setValues(pstmt);
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[0];
    }


    // 新增：便捷重载（批量可变参）
    public int[] batchUpdate(String sql, Object[]... batchArgs) {
        List<Object[]> list = new ArrayList<>();
        if (batchArgs != null) {
            for (Object[] arr : batchArgs) {
                list.add(arr);
            }
        }
        return batchUpdate(sql, list);
    }
}