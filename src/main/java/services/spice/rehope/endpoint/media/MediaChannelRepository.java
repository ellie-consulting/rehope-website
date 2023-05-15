package services.spice.rehope.endpoint.media;

import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.endpoint.media.model.MediaCreator;
import services.spice.rehope.endpoint.user.principle.PrincipleUserRepository;
import services.spice.rehope.model.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Repository for storing media channels connections.
 */
@Singleton
public class MediaChannelRepository extends Repository<MediaCreator> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaChannelRepository.class);
    private static final String TABLE = "media_channels";

    public MediaChannelRepository(PostgreDatasource datasource) {
        super(datasource);
    }

    /**
     * Get connection data from a user id.
     *
     * @param userId User id to get.
     * @return Their data.
     */
    public Optional<MediaCreator> getDataFromUserId(int userId) {
        return getByField("user_id", userId);
    }

    /**
     * Get connection data from a channel id.
     *
     * @param channelId Channel id to get by.
     * @return Their data.
     */
    public Optional<MediaCreator> getDataFromChannelId(@NotNull String channelId) {
        return getByField("channel_id", channelId);
    }

    /**
     * Add a new youtube connection to a user id.
     *
     * @param userId User id.
     * @param channelId Channel id.
     * @return If added successfully.
     */
    public boolean addChannelId(int userId, @NotNull String channelId) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE + " (user_id, channel_id) " +
                    "VALUES (?, ?)");
            statement.setInt(1, userId);
            statement.setString(2, channelId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            getLogger().error("failed to add channel connection for {} -> {}", userId, channelId);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Remove a YouTube connection to a user id.
     *
     * @param userId User id to remove connection of.
     * @return If successfully removed.
     */
    public boolean removeChannelId(int userId) {
        return deleteData("user_id", userId);
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
    protected MediaCreator mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("user_id");
        String channelId = resultSet.getString("channel_id");
        return new MediaCreator(userId, channelId);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INTEGER NOT NULL UNIQUE REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "channel_id VARCHAR(24) NOT NULL" +
                    ");").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user settings table", e);
        }
    }
}
