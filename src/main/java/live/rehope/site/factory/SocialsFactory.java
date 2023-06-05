package live.rehope.site.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Named;
import live.rehope.site.endpoint.user.auth.DiscordClient;
import live.rehope.site.model.LoadingFactory;

import java.io.IOException;
import java.util.Properties;

@Factory
public class SocialsFactory extends LoadingFactory {

    @Bean
    @Named("social")
    DiscordClient discordApi() {
        try {
            Properties properties = loadCredentialPair("discord.properties");
            DiscordClient discordClient = new DiscordClient(getKey(properties), getSecret(properties), DiscordClient.SOCIAL_SCOPE);
            discordClient.setSaveProfileInSession(false);
            return discordClient;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
