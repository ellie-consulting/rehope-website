package live.rehope.site.endpoint.user.auth;

import org.jetbrains.annotations.NotNull;

/**
 * Auth provider used to create an account.
 */
public enum AuthProviderSource {
    GOOGLE("Google2Client"),
    TWITTER("TwitterClient"),
    DISCORD("DiscordClient");

    private final String clientId;

    AuthProviderSource(String clientId) {
        this.clientId = clientId;
    }

    @NotNull
    public String getClientId() {
        return clientId;
    }
}
