package services.spice.rehope.route;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.security.RouteRole;
import io.javalin.vue.VueComponent;
import jakarta.inject.Singleton;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.javalin.CallbackHandler;
import org.pac4j.javalin.JavalinWebContext;
import org.pac4j.javalin.SecurityHandler;
import org.pac4j.jee.context.session.JEESessionStore;
import org.pac4j.oauth.client.Google2Client;

import java.util.Optional;

@Singleton
public class RegisterRoutes implements ServerRoute {

    @Override
    public void registerRoutes(Javalin javalin) {

        // Create the Pac4j configuration with the Google client
        Google2Client googleClient = new Google2Client("932759867981-598hf7nui6q6o64dubnmnjlfipku7sau.apps.googleusercontent.com", "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");
        googleClient.setCallbackUrl("http://127.0.0.1:8080/callback"); // /auth/oauth/google/callback
        googleClient.setCallbackUrlResolver(new NoParameterCallbackUrlResolver());
        Config config = new Config(googleClient);

        CallbackHandler callback = new CallbackHandler(config, null, true);

        // Configure the login and callback endpoints
        javalin.get("/login", new VueComponent("login"));

//        javalin.before("/api/googleLogin", new SecurityHandler(config, "Google2Client"));
        javalin.get("/api/googleLogin", new SecurityHandler(config, "Google2Client"));
        javalin.get("/register", ctx -> {
            new VueComponent("hello-world").handle(ctx);
        });
        javalin.get("/auth/oauth/google/callback", callback);
        javalin.post("/auth/oauth/google/callback", callback);

        // Create the home page route
        javalin.get("/", new VueComponent("index"));
    }

    private VueComponent register(Context context) {
        return new VueComponent("hello-world");
    }

}
