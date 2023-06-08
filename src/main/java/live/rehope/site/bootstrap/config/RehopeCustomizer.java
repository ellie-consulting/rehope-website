package live.rehope.site.bootstrap.config;

import com.google.gson.Gson;
import io.avaje.config.Config;
import io.javalin.json.JavalinGson;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import io.javalin.config.JavalinConfig;
import io.javalin.config.PluginConfig;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.jetty.server.session.SessionHandler;
import live.rehope.site.util.ContextUtils;

import java.util.Optional;

@Singleton
public class RehopeCustomizer implements ServerCustomizer {

    private final Gson gson;
    private final SessionHandler sessionHandler;
    private static final boolean DEV = Config.get("env").equals("DEV")
            && Config.get("host.domain").contains("127.0.0.1");

    @Inject
    public RehopeCustomizer(Gson gson, SessionHandler handler) {
        this.gson = gson;
        this.sessionHandler = handler;
    }

    @Override
    public void accept(JavalinConfig config) {
//        // vue config
//        config.staticFiles.enableWebjars();
//        config.vue.vueAppName = "app";

        config.jetty.sessionHandler(() -> sessionHandler);
        config.accessManager((handler, context, permittedRoles) -> {
            // todo rate limit

            if (DEV) {
                handler.handle(context);
                return;
            }

            // no auth needed
            if (permittedRoles.isEmpty()) {
                handler.handle(context);
                return;
            }

            Optional<UserRole> reqRole = ContextUtils.role(context);

            // permitted roles are specified and no request, ignore.
            if (reqRole.isEmpty()) {
                context.status(HttpStatus.UNAUTHORIZED).json("Must be logged in to view this page.");
                return;
            }

            // Check roles
            UserRole userRole = reqRole.get();

            // exact match
            if (permittedRoles.contains(userRole)) {
                handler.handle(context);
                return;
            }

            // need to check if they have a greater or equal role than required.
            if (permittedRoles.stream().anyMatch(testRole -> userRole.isEqualOrGreaterThan((UserRole) testRole))) {
                handler.handle(context);
            } else {
                context.status(HttpStatus.UNAUTHORIZED).json("You cannot view this page.");
            }
        });

        PluginConfig pluginConfig = config.plugins;
        pluginConfig.enableDevLogging();

        config.jsonMapper(new JavalinGson(gson));
    }

}
