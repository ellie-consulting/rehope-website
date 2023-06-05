package live.rehope.site.endpoint.user.auth;

import com.github.scribejava.apis.DiscordApi;
import com.github.scribejava.core.model.Token;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oauth.client.OAuth20Client;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.HashMap;
import java.util.Map;

public class DiscordClient extends OAuth20Client {
    public static final String SOCIAL_SCOPE = "identify"; // todo email scope for reg
    public static final String AUTH_SCOPE = "identify,email";

    private final String scope;

    public DiscordClient(final String key, final String secret, final String scope) {
        setKey(key);
        setSecret(secret);

        this.scope = scope;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        super.internalInit(forceReinit);

        // todo https://discord.com/developers/docs/topics/oauth2#authorization-code-grant
        configuration.setApi(DiscordApi.instance());
        configuration.setProfileDefinition(new OAuthProfileDefinition() {
            @Override
            public String getProfileUrl(Token accessToken, OAuthConfiguration configuration) {
                return "https://discord.com/api/users/@me";
            }

            @Override
            public CommonProfile extractUserProfile(String body) {
                // hello {"id": "123188806349357062", "username": "Ellie", "global_name": "Ellie",
                // "avatar": "cee72101816de09a0ff02190ba6fc842", "discriminator": "0006", "public_flags": 644,
                // "flags": 644, "banner": null, "banner_color": "#d1a3d3", "accent_color": 13738963, "locale": "no",
                // "mfa_enabled": true, "premium_type": 1, "avatar_decoration": null}
                JsonObject element = JsonParser.parseString(body).getAsJsonObject();

                Map<String, Object> attributes = new HashMap<>();
                attributes.put(Pac4jConstants.USERNAME, element.getAsJsonPrimitive("username").getAsString());

                OAuth20Profile profile = new OAuth20Profile();
                profile.setId(element.getAsJsonPrimitive("id").getAsString());
                profile.addAttributes(attributes);
                return profile;
            }
        });

        configuration.setScope(scope);
        configuration.setWithState(true); // not required but recommended
        configuration.setHasBeenCancelledFactory(ctx -> {
            final var error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);

            // user has denied permissions
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });


    }

}
