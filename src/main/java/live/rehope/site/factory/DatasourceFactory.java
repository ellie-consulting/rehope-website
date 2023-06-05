package live.rehope.site.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.model.LoadingFactory;

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
