package live.rehope.site.endpoint.media;

import io.avaje.inject.RequiresBean;
import io.javalin.http.BadRequestResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.datasource.exception.DatabaseError;
import live.rehope.site.endpoint.media.model.Media;
import live.rehope.site.endpoint.media.model.MediaType;
import live.rehope.site.endpoint.user.principle.PrincipleUserRepository;
import live.rehope.site.model.Repository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Singleton
@RequiresBean(PrincipleUserRepository.class)
public class FeaturedMediaRepository extends Repository<Media> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);
    private static final String TABLE = "featured_media";

    private List<Media> featuredContent;

    // todo live stream can be featured too.

    @Inject
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
     * The video id must be unique to the table.
     *
     * @param media Media to add.
     */
    public void addMedia(@NotNull Media media) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE
                    + " (user_id, channel_id, video_id, published_at) VALUES (?, ?, ?, ?) RETURNING id");
            statement.setInt(1, media.userId());
            statement.setString(2, media.channelId());
            statement.setString(3, media.videoId());
            statement.setTimestamp(4, new Timestamp(media.publishedAt() == 0 ? System.currentTimeMillis() : media.publishedAt()));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // add to cache
                getAll().add(getById(resultSet.getInt("id")).orElseThrow());
            } else {
                throw new BadRequestResponse("media already added");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("unique constraint")) {
                throw new BadRequestResponse("video id already in use");
            }

            getLogger().error("failed to add media " + media, e);
            throw new DatabaseError();
        }
    }

    /**
     * Remove a featured media by its id.
     *
     * @param featureId Feature id.
     */
    public void removeMediaById(int featureId) {
        featuredContent.removeIf(media -> media.featuredId() == featureId);

        deleteDataById(featureId);
    }

    /**
     * Remove a featured media by its video id.
     *
     * @param videoId Id to remove.
     */
    public void removeMediaByVideoId(@NotNull String videoId) {
        featuredContent.removeIf(media -> media.videoId().equalsIgnoreCase(videoId));

        deleteData("video_id", videoId);
    }

    public void removeMediaByUserId(int userId) {
        deleteData("user_id", userId);
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
        int id = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        String channelId = resultSet.getString("channel_id");
        String videoId = resultSet.getString("video_id");
        long publishedAt = resultSet.getTimestamp("published_at").getTime();

        return new Media(id, userId, channelId, videoId, MediaType.VIDEO, publishedAt);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INTEGER NOT NULL REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "channel_id VARCHAR(24) NOT NULL," +
                    "video_id VARCHAR(24) NOT NULL UNIQUE," +
                    "featured_since TIMESTAMP NOT NULL DEFAULT NOW()," +
                    "published_at TIMESTAMP NOT NULL" +
                    ");").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create featured media table", e);
        }
    }
}
