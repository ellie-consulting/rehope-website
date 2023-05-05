package services.spice.rehope.route;

import io.javalin.Javalin;

/**
 * A route on the web server.
 */
public interface ServerRoute {
    void registerRoutes(Javalin javalin);
}
