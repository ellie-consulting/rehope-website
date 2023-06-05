package live.rehope.site.endpoint.user.social.model;

/**
 * A social media that can be linked to the account.
 */
public enum UserSocialPlatform {
    YOUTUBE("YouTubeClient"),
    TWITTER("TwitterClient"),
    DISCORD("DiscordClient");

    private final String clientId;

    UserSocialPlatform(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
