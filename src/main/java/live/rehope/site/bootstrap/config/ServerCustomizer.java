package live.rehope.site.bootstrap.config;

import io.javalin.config.JavalinConfig;

import java.util.function.Consumer;

@FunctionalInterface
public interface ServerCustomizer extends Consumer<JavalinConfig> {
}
