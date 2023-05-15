package services.spice.rehope.model;

import org.eclipse.jetty.util.IO;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Factories.
 */
public abstract class LoadingFactory {
    private static final Path PATH_CREDENTIALS = Path.of("oauth-credentials");
    private static final Path PATH_KEYS = Path.of("api-keys");

    /**
     * Load a credentials pair.
     *
     * @param file File name in the credentials' directory.
     * @return Loaded properties
     * @throws IOException If there was an error loading.
     */
    @NotNull
    protected final Properties loadCredentialPair(@NotNull String file) throws IOException {
        return loadFile(PATH_CREDENTIALS.toFile(), file);
    }

    /**
     * Load an api key.
     *
     * @param file File name in the key directory.
     * @return Loaded properties
     * @throws IOException If there was an error loading.
     */
    @NotNull
    protected final Properties loadKey(@NotNull String file) throws IOException {
        return loadFile(PATH_KEYS.toFile(), file);
    }

    /**
     * Get client-id key from properties.
     *
     * @param properties Properties to load from.
     * @return Client id.
     */
    protected String getKey(@NotNull Properties properties) {
        return properties.getProperty("client-id");
    }

    /**
     * Get client-secret key from properties.
     *
     * @param properties Properties to load from.
     * @return Client secret.
     */
    protected String getSecret(@NotNull Properties properties) {
        return properties.getProperty("client-secret");
    }

    /**
     * Get api key from properties.
     *
     * @param file File to load from.
     * @return Api key.
     */
    protected String getApiKey(@NotNull String file) throws IOException {
        return loadKey(file).getProperty("key");
    }

    private Properties loadFile(File file, String child) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(new File(file, child));
        props.load(fis);
        return props;
    }

}
