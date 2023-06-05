package live.rehope.site.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import live.rehope.site.endpoint.user.auth.DiscordClient;
import live.rehope.site.model.LoadingFactory;

import java.io.IOException;
import java.util.Properties;

@Factory
public class OAuthFactory extends LoadingFactory {

    @Bean
    Google2Client google2Client() {
        try {
            Properties properties = loadCredentialPair("google.properties");

            Google2Client googleClient = new Google2Client(getKey(properties), getSecret(properties));
            googleClient.setCallbackUrl("http://127.0.0.1:8080/api/auth/oauth/google/callback");
            return googleClient;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    TwitterClient twitterClient() {
        try {
            Properties properties = loadCredentialPair("twitter.properties");

            TwitterClient twitterClient = new TwitterClient(getKey(properties), getSecret(properties));
            twitterClient.setCallbackUrl("http://127.0.0.1:8080/api/auth/oauth/twitter/callback");
            return twitterClient;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    DiscordClient discordApi() {
        DiscordClient discordClient = new DiscordClient("1105437794605748244", "CF1IrGtoRMjnfixmwgWTPNyukaw-7QMF");
        discordClient.setCallbackUrl("http://127.0.0.1:8080/api/auth/oauth/discord/callback");
        return discordClient;
    }
}
