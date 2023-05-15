package services.spice.rehope.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.TwitterClient;
import services.spice.rehope.model.LoadingFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
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

//    @Bean
//    DiscordClient discordApi() {
//        return new ServiceBuilder("1105437794605748244")
//                .apiSecret("")
//                .defaultScope("identify") // replace with desired scope
//                .callback("http://127.0.0.1:8080/api/auth/oauth/discord/callback")
//                .userAgent("ScribeJava")
//                .build(DiscordApi.instance());
//    }

}
