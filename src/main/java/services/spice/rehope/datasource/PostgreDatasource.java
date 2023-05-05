package services.spice.rehope.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreDatasource implements Datasource<Connection> {
    private HikariDataSource dataSource;

    public PostgreDatasource(@NotNull HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void close() {
        System.out.println("close");
        if (!isConnected()) {
            return;
        }

        dataSource.close();
        dataSource = null;
    }

    @Override
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    @Override
    @NotNull
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
