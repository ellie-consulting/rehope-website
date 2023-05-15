package services.spice.rehope.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import services.spice.rehope.endpoint.media.youtube.YouTubeClient;
import services.spice.rehope.model.LoadingFactory;

import java.io.IOException;

/**
 * Factory for accessing media platforms.
 */
@Factory
public class MediaClientFactory extends LoadingFactory {

    @Bean
    YouTubeClient youTubeClient() {
        try {
            YouTubeClient client = new YouTubeClient();
            client.setKey(getApiKey("youtube-key.properties"));

            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
