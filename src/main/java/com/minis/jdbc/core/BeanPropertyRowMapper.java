package com.minis.jdbc.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class BeanPropertyRowMapper<T> implements RowMapper<T> {
    private final Class<T> mappedClass;

    public BeanPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        try {
            T bean = mappedClass.getDeclaredConstructor().newInstance();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String column = meta.getColumnLabel(i);
                if (column == null || column.isEmpty()) {
                    column = meta.getColumnName(i);
                }
                Object value = rs.getObject(i);
                if (value == null) continue;

                // try setter first
                String prop = toCamel(column);
                String setterName = "set" + Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
                Method setter = findSetter(setterName, bean.getClass(), value.getClass());
                if (setter != null) {
                    setter.setAccessible(true);
                    setter.invoke(bean, convertValue(value, setter.getParameterTypes()[0]));
                    continue;
                }
                // fallback: set field directly
                Field f = findField(bean.getClass(), prop);
                if (f != null) {
                    f.setAccessible(true);
                    f.set(bean, convertValue(value, f.getType()));
                }
            }
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method findSetter(String name, Class<?> clz, Class<?> valClz) {
        try {
            for (Method m : clz.getMethods()) {
                if (m.getName().equals(name) && m.getParameterCount() == 1) {
                    return m;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static Field findField(Class<?> clz, String name) {
        Class<?> c = clz;
        while (c != null && c != Object.class) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {}
            c = c.getSuperclass();
        }
        return null;
    }

    private static String toCamel(String name) {
        // simple convert: underscores to camelCase
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (char ch : name.toCharArray()) {
            if (ch == '_' || ch == ' ') { up = true; continue; }
            if (up) { sb.append(Character.toUpperCase(ch)); up = false; }
            else { sb.append(Character.toLowerCase(ch)); }
        }
        return sb.toString();
    }

    private static Object convertValue(Object val, Class<?> targetType) {
        if (val == null) return null;
        if (targetType.isAssignableFrom(val.getClass())) return val;
        try {
            if ((targetType == java.util.Date.class) && val instanceof java.sql.Date) {
                return new java.util.Date(((java.sql.Date) val).getTime());
            }
            if ((targetType == java.util.Date.class) && val instanceof java.sql.Timestamp) {
                return new java.util.Date(((java.sql.Timestamp) val).getTime());
            }
            if (targetType == int.class || targetType == Integer.class) {
                if (val instanceof Number) return ((Number) val).intValue();
                return Integer.parseInt(String.valueOf(val));
            }
            if (targetType == long.class || targetType == Long.class) {
                if (val instanceof Number) return ((Number) val).longValue();
                return Long.parseLong(String.valueOf(val));
            }
            if (targetType == String.class) {
                return String.valueOf(val);
            }
        } catch (Exception ignored) {}
        return val;
    }
}

