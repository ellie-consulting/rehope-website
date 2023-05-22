package services.spice.rehope.bootstrap;

import io.avaje.inject.BeanScope;
import io.javalin.Javalin;
import io.javalin.vue.VueComponent;
import services.spice.rehope.bootstrap.config.ServerCustomizer;

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

        javalin.get("/", new VueComponent("home"));
        javalin.get("/login", new VueComponent("login"));
        javalin.get("/register", new VueComponent("register"));

        javalin.get("/admin/users", new VueComponent("view-accounts"));

        javalin.start(host, port);
    }

}
