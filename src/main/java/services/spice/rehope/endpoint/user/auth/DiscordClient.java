package services.spice.rehope.endpoint.user.auth;

import com.github.scribejava.apis.DiscordApi;
import org.pac4j.core.logout.GoogleLogoutActionBuilder;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.google2.Google2ProfileDefinition;

public class DiscordClient extends OAuth20Client {
    private static final String SCOPE = "identify";

    public DiscordClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        // call https://discordapp.com/api/users/@me after authe

        // todo https://discord.com/developers/docs/topics/oauth2#authorization-code-grant
        configuration.setApi(DiscordApi.instance());
        configuration.setProfileDefinition(new Google2ProfileDefinition());
        configuration.setScope(SCOPE);
        configuration.setWithState(true); // not required but recommended
        configuration.setHasBeenCancelledFactory(ctx -> {
            final var error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            // user has denied permissions
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });
        defaultLogoutActionBuilder(new GoogleLogoutActionBuilder());

        super.internalInit(forceReinit);
    }

}
