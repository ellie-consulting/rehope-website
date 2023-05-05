package services.spice.rehope.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

/**
 * Factory to provide a Gson instance.
 */
@Factory
public class GsonFactory {

  @Bean
  Gson gson() {
    return new GsonBuilder().create();
  }

}