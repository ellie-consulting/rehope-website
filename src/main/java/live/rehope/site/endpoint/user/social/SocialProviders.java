package live.rehope.site.endpoint.user.social;

import io.javalin.http.Context;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.user.auth.DiscordClient;
import live.rehope.site.endpoint.user.social.model.UserSocialPlatform;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.javalin.CallbackHandler;
import org.pac4j.javalin.SecurityHandler;

/**
 * Manages oauth social connections.
 */
@Singleton
public class SocialProviders {

    private final Config config;
    private final CallbackHandler callbackHandler;

    public SocialProviders(@Named("social") DiscordClient discordClient) {
        this.config = new Config("http://127.0.0.1:8080/api/social/oauth/callback", discordClient);
        config.getClients().setCallbackUrlResolver(new NoParameterCallbackUrlResolver());
        DefaultSecurityLogic securityLogic = new DefaultSecurityLogic();
        securityLogic.setLoadProfilesFromSession(false);
        config.setSecurityLogic(securityLogic);

        this.callbackHandler = new CallbackHandler(config, "/connected", false);
    }

    public void handleSocialConnect(@NotNull UserSocialPlatform platform, @NotNull Context context) {
        new SecurityHandler(config, platform.getClientId()).handle(context);
    }

    public void handleSocialCallback(@NotNull Context context) {
        callbackHandler.handle(context);
    }

}
