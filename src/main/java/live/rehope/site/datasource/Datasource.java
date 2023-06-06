package live.rehope.site.datasource;

import io.avaje.inject.PreDestroy;
import org.jetbrains.annotations.NotNull;

public interface Datasource<Connection> {

    @PreDestroy
    void close();

    boolean isConnected();

    @NotNull
    Connection getConnection();

}
