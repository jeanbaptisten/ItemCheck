package me.jb.itemcheck.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String DATABASE_URL = "jdbc:mysql";

    private final String host, database, user, password;
    private Connection connection;

    public DbConnection(String host, String database, String user, String password) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public void open() throws SQLException {
        if (this.isOpened()) throw new SQLException("Connection already opened.");

        String url = String.format("%s://%s/%s?autoReconnect=true&allowMultiQueries=true", DATABASE_URL, host, database);

        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void close() throws SQLException {

        if (!this.isOpened()) throw new SQLException("Connection already closed.");

        this.connection.close();
        this.connection = null;
    }

    public boolean isOpened() throws SQLException {
        return this.connection != null && !this.connection.isClosed();
    }

    public Connection getConnection() {
        return this.connection;
    }
}
