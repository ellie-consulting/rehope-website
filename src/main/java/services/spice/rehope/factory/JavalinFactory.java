package services.spice.rehope.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.javalin.Javalin;

/**
 * Sets up the Javalin environment.
 */
@Factory
public class JavalinFactory {

    @Bean
    Javalin javalin() {
        return Javalin.create();
    }

}