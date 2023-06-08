package live.rehope.site.bootstrap;

import io.avaje.inject.BeanScope;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.staticfiles.Location;
import live.rehope.site.bootstrap.config.ServerCustomizer;

/**
 * Starts the http server.
 */
public final class ServerBootstrap {

    public static void run() {
        String host = System.getProperty("SERVER_HOST", "127.0.0.1");
        int port = Integer.parseInt(System.getProperty("SERVER_PORT", "8080"));

        BeanScope beanScope = BeanScope.builder().build();
        Javalin javalin = beanScope.get(Javalin.class);

        // Setup
        beanScope.getOptional(ServerCustomizer.class)
                .ifPresent(serverCustomizer -> serverCustomizer.accept(javalin.cfg));

        javalin.exception(IllegalArgumentException.class, (exception, ctx) -> {
            throw new BadRequestResponse("Provided constant is invalid: " + exception.getMessage());
        });

        // Register public
        javalin.updateConfig(javalinConfig -> {
            javalinConfig.staticFiles.add("/public", Location.CLASSPATH);
        });

        javalin.get("/", ctx -> ctx.render("public/index.html")); // injection point for React

        javalin.start(host, port);
    }

}
