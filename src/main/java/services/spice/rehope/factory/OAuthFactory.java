package services.spice.rehope.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.oauth.client.Google2Client;

@Factory
public class OAuthFactory {

    @Bean
    Google2Client google2Client() {
        // todo config load
        Google2Client googleClient = new Google2Client("932759867981-598hf7nui6q6o64dubnmnjlfipku7sau.apps.googleusercontent.com", "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");
        googleClient.setCallbackUrl("http://127.0.0.1:8080/api/auth/oauth/google/callback");
        googleClient.setCallbackUrlResolver(new NoParameterCallbackUrlResolver());
        return googleClient;
    }

}
