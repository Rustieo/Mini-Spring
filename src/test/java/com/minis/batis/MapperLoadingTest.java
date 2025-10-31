package com.minis.batis;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapperLoadingTest {
    @Test
    public void shouldLoadUserMapperStatement() {
        DefaultSqlSessionFactory factory = new DefaultSqlSessionFactory();
        factory.setMapperLocations("mapper");
        // only load mappers; no DB interaction needed
        factory.init();

        MapperNode node = factory.getMapperNode("com.minis.test.entity.User.getUserInfo");
        assertNotNull("Expected mapper node to be loaded", node);
        String sql = node.getSql();
        assertNotNull(sql);
        assertTrue("SQL should include users table", sql.toLowerCase().contains("from users"));
        assertEquals("com.minis.test.entity.User", node.getNamespace());
        assertEquals("getUserInfo", node.getId());
    }
}

