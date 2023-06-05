package live.rehope.site.factory;

import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.session.*;
import live.rehope.site.model.LoadingFactory;

@Factory
public class SessionFactory extends LoadingFactory {

    @Bean
    SessionHandler sqlSessionHandler(HikariDataSource dataSource) {
        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(jdbcDataStoreFactory(dataSource).getSessionDataStore(sessionHandler));
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSameSite(HttpCookie.SameSite.LAX); // todo https://stackoverflow.com/questions/42216700/how-can-i-redirect-after-oauth2-with-samesite-strict-and-still-get-my-cookies
        sessionHandler.setSecureRequestOnly(true);
        return sessionHandler;
    }

    private JDBCSessionDataStoreFactory jdbcDataStoreFactory(HikariDataSource dataSource) {
        DatabaseAdaptor databaseAdaptor = new DatabaseAdaptor();
        databaseAdaptor.setDriverInfo(dataSource.getDriverClassName(), dataSource.getJdbcUrl());
        databaseAdaptor.setDatasource(dataSource);

        JDBCSessionDataStoreFactory jdbcSessionDataStoreFactory = new JDBCSessionDataStoreFactory();
        jdbcSessionDataStoreFactory.setDatabaseAdaptor(databaseAdaptor);
        return jdbcSessionDataStoreFactory;
    }

}
