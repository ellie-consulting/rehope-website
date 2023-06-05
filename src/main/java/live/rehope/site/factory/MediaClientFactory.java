package live.rehope.site.factory;

import live.rehope.site.model.LoadingFactory;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import live.rehope.site.endpoint.media.youtube.YouTubeClient;

import java.io.IOException;
import java.util.Properties;

/**
 * Factory for accessing media platforms.
 */
@Factory
public class MediaClientFactory extends LoadingFactory {

    @Bean
    YouTubeClient youTubeClient() {
        try {
            YouTubeClient client = new YouTubeClient();
            Properties properties = loadKey("youtube-key.properties");

            client.setKey(properties.getProperty("key"));
            client.setSubscriberSecret(properties.getProperty("pubsub-secret"));

            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
