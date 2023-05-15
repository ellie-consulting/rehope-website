package services.spice.rehope.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.model.LoadingFactory;

@Factory
public class DatasourceFactory extends LoadingFactory {

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
