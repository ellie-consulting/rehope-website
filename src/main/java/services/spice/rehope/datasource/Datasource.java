package services.spice.rehope.datasource;

import org.jetbrains.annotations.NotNull;

public interface Datasource<Connection> {

    void close();

    boolean isConnected();

    @NotNull
    Connection getConnection();

}
