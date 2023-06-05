package live.rehope.site.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Named;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import live.rehope.site.endpoint.user.auth.DiscordClient;
import live.rehope.site.model.LoadingFactory;

import java.io.IOException;
import java.util.Properties;

@Factory
public class OAuthFactory extends LoadingFactory {

    @Bean
    @Named("oauth")
    Google2Client google2Client() {
        try {
            Properties properties = loadCredentialPair("google.properties");

            return new Google2Client(getKey(properties), getSecret(properties));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Named("oauth")
    TwitterClient twitterClient() {
        try {
            Properties properties = loadCredentialPair("twitter.properties");
            return new TwitterClient(getKey(properties), getSecret(properties));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Named("oauth")
    DiscordClient discordApi() {
        try {
            Properties properties = loadCredentialPair("discord.properties");
            return new DiscordClient(getKey(properties), getSecret(properties), DiscordClient.AUTH_SCOPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
