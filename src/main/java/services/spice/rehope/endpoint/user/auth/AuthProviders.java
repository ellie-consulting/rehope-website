package services.spice.rehope.endpoint.user.auth;

import io.javalin.http.Context;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.config.Config;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.javalin.CallbackHandler;
import org.pac4j.javalin.LogoutHandler;
import org.pac4j.javalin.SecurityHandler;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;

/**
 * Manages auth providers which can be used for logging in and out.
 */
@Singleton
public class AuthProviders {
    private final Config config;
    private final CallbackHandler callbackHandler;
    private final LogoutHandler logoutHandler;

    public AuthProviders(Google2Client google2Client, TwitterClient twitterClient) {
        this.config = new Config(google2Client, twitterClient);
        config.getClients().setCallbackUrlResolver(new NoParameterCallbackUrlResolver());
        this.callbackHandler = new CallbackHandler(config, "/", true);
        this.logoutHandler = new LogoutHandler(config, "/?");
        logoutHandler.destroySession = true;
    }

    public void handleLogin(@NotNull AuthProviderSource source, @NotNull Context context) {
        new SecurityHandler(config, source.getClientId()).handle(context);
    }

    /**
     * Handle a callback.
     * </br>
     * It will then redirect the user back to the login page.
     *
     * @param context Request context.
     */
    public void handleCallback(@NotNull Context context) {
        callbackHandler.handle(context);
    }

    public void handleLogout(Context context) {
        logoutHandler.handle(context);
    }

}
