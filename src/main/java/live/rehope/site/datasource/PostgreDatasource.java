package live.rehope.site.datasource;

import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.PreDestroy;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreDatasource implements Datasource<Connection> {
    private HikariDataSource dataSource;

    public PostgreDatasource(@NotNull HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @PreDestroy
    public void close() {
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
