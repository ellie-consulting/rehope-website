package services.spice.rehope.endpoint.media;

import io.avaje.inject.RequiresBean;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.endpoint.media.model.Media;
import services.spice.rehope.endpoint.media.model.MediaType;
import services.spice.rehope.endpoint.user.principle.PrincipleUserRepository;
import services.spice.rehope.model.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Singleton
@RequiresBean(PrincipleUserRepository.class)
public class FeaturedMediaRepository extends Repository<Media> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);
    private static final String TABLE = "featured_media";

    private List<Media> featuredContent;

    // todo live stream can be featured too.

    public FeaturedMediaRepository(PostgreDatasource datasource) {
        super(datasource);
    }

    @Override
    @NotNull
    public List<Media> getAll() {
        // todo move to redis in future
        if (featuredContent == null) {
            featuredContent = super.getAll();
        }

        return featuredContent;
    }

    /**
     * Add a featured media.
     * </br>
     * The url must be unique to the table.
     *
     * @param media Media to add.
     * @return If it was successfully added.
     */
    public boolean addMedia(@NotNull Media media) {
        featuredContent.add(media);

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE + " (user_id, channel_id, video_url) " +
                    "VALUES (?, ?, ?)");
            statement.setInt(1, media.userId());
            statement.setString(2, media.channelId());
            statement.setString(3, media.url());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            getLogger().error("failed to add media " + media, e);
        }

        return false;
    }

    /**
     * Remove a featured media by its url.
     *
     * @param url Url to remove.
     * @return If it was removed.
     */
    public boolean removeMedia(@NotNull String url) {
        featuredContent.removeIf(media -> media.url().equalsIgnoreCase(url));

        return deleteData("video_url", url);
    }

    @Override
    public String getTable() {
        return TABLE;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected Media mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("user_id");
        String channelId = resultSet.getString("channel_id");
        String videoUrl = resultSet.getString("video_url");
        return new Media(userId, channelId, videoUrl, MediaType.VIDEO);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INTEGER NOT NULL REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "channel_id VARCHAR(24) NOT NULL," +
                    "video_url VARCHAR(64) UNIQUE NOT NULL" +
                    ");").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create featured media table", e);
        }
    }
}
