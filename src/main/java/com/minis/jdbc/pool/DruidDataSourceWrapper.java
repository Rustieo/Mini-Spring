package com.minis.jdbc.pool;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Adapter to make DruidDataSource compatible with the simple BeanWrapper
 * which requires properties to be declared on the concrete class itself.
 */
public class DruidDataSourceWrapper implements DataSource {
    // expose fields so BeanWrapperImpl can resolve property types via getDeclaredField
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private Integer initialSize ;

    private final DruidDataSource delegate = new DruidDataSource();

    public DruidDataSourceWrapper() {
    }

    // setters used by IoC
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        delegate.setDriverClassName(driverClassName);
        // keep behavior similar to PooledDataSource to pre-load driver
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException ignored) { }
    }

    public void setUrl(String url) {
        this.url = url;
        delegate.setUrl(url);
    }

    public void setUsername(String username) {
        this.username = username;
        delegate.setUsername(username);
    }

    public void setPassword(String password) {
        this.password = password;
        delegate.setPassword(password);
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
        delegate.setInitialSize(initialSize);
    }

    // overload to support String value from simple XML binder
    public void setInitialSize(String initialSize) {
        if (initialSize != null && !initialSize.isEmpty()) {
            try {
                setInitialSize(Integer.parseInt(initialSize));
            } catch (NumberFormatException e) {
                // fallback: ignore and keep default
            }
        }
    }

    // getters (not strictly needed by current IoC, but provided for completeness)
    public String getDriverClassName() { return driverClassName; }
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getInitialSize() { return initialSize; }

    // DataSource delegation
    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return delegate.getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger("DruidDataSourceWrapper");
    }
}
