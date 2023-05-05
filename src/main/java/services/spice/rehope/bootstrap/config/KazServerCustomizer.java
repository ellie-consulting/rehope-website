package services.spice.rehope.bootstrap.config;

import com.google.gson.Gson;
import io.javalin.config.JavalinConfig;
import io.javalin.config.PluginConfig;
import io.javalin.json.JsonMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.jetty.server.session.SessionHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@Singleton
public class KazServerCustomizer implements ServerCustomizer {

    private final Gson gson;
    private final SessionHandler sessionHandler;

    @Inject
    public KazServerCustomizer(Gson gson, SessionHandler handler) {
        this.gson = gson;
        this.sessionHandler = handler;
    }

    @Override
    public void accept(JavalinConfig config) {
        // vue config
        config.staticFiles.enableWebjars();
        config.vue.vueAppName = "app";

        config.jetty.sessionHandler(() -> sessionHandler);

        PluginConfig pluginConfig = config.plugins;
        pluginConfig.enableDevLogging();

        config.jsonMapper(new JsonMapper() {
            @NotNull
            @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }

            @NotNull
            @Override
            public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }
        });
    }

}
