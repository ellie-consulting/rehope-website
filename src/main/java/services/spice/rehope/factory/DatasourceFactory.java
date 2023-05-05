package services.spice.rehope.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import services.spice.rehope.datasource.PostgreDatasource;

@Factory
public class DatasourceFactory {

    @Bean
    static HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig("datasource.properties");
        config.setLeakDetectionThreshold(60 * 1000);
        return new HikariDataSource(config);
    }

    @Bean
    PostgreDatasource postgreDatasource(HikariDataSource dataSource) {
        return new PostgreDatasource(dataSource);
    }

}
