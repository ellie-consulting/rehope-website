package services.spice.rehope.factory;

import io.avaje.http.api.WebRoutes;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.javalin.Javalin;

import java.util.List;

/**
 * Sets up the Javalin environment.
 */
@Factory
public class JavalinFactory {

    @Bean
    Javalin javalin(List<WebRoutes> webRoutes) {
        Javalin javalin = Javalin.create();

        javalin.routes(() -> webRoutes.forEach(WebRoutes::registerRoutes));

        return javalin;
    }

}