package services.spice.rehope.endpoint.user.social;

import io.avaje.inject.RequiresBean;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.datasource.PostgreDatasource;
import services.spice.rehope.model.Repository;
import services.spice.rehope.endpoint.user.principle.PrincipleUserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Stores socials of a user.
 */
@Singleton
@RequiresBean(PrincipleUserRepository.class)
public class UserSocialsRepository extends Repository<UserSocial> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSocialsRepository.class);
    private static final String TABLE = "user_social_media";

    @Inject
    public UserSocialsRepository(PostgreDatasource datasource) {
        super(datasource);
    }

    @Override
    public String getTable() {
        return TABLE;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Get all social medias for a user id
     * indexed by the type.
     *
     * @param userId User id.
     * @return Their socials.
     */
    @NotNull
    public Map<UserSocialPlatform, UserSocial> getUserSocials(int userId) {
        Map<UserSocialPlatform, UserSocial> res = new EnumMap<>(UserSocialPlatform.class);

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ? WHERE user_id = ?");
            statement.setString(1, TABLE);
            statement.setInt(2, userId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // try/catch individual
                UserSocial userSocial = mapResultSetToType(resultSet);

                res.put(userSocial.platform(), userSocial);
            }

        } catch (SQLException e) {
            LOGGER.error("failed to get user {} socials", userId);
            e.printStackTrace();
        }

        return res;

    }

    /**
     * Get the social media information of a type.
     *
     * @param userId User id.
     * @param type Type.
     * @return Their data.
     */
    public Optional<UserSocial> getUserSocial(int userId, @NotNull UserSocialPlatform type) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ? WHERE user_id = ? AND social_media = ?");
            statement.setString(1, TABLE);
            statement.setInt(2, userId);
            statement.setString(3, type.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToType(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error("failed to get user {} social by type {}", userId, type);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Insert a new social media for a user.
     *
     * @param socialMedia Social media to insert.
     * @return If it was successfully added.
     */
    public boolean addUserSocial(@NotNull UserSocial socialMedia) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE + " (user_id, social_media, social_media_id) VALUES (?, ?, ?)");
            statement.setInt(1, socialMedia.userId());
            statement.setString(2, socialMedia.platform().name());
            statement.setString(3, socialMedia.socialMediaId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            getLogger().error("failed to insert user social media {}", socialMedia);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a particular social media of a user.
     *
     * @param userId User to delete.
     * @param type Type to delete.
     * @return If any change.
     */
    public boolean deleteUserSocial(int userId, @NotNull UserSocialPlatform type) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM ? WHERE user_id = ? AND social_media = ?");
            statement.setString(1, getTable());
            statement.setInt(2, userId);
            statement.setString(3, type.name());
            int deleted = statement.executeUpdate();

            return deleted > 0;
        } catch (SQLException e) {
            getLogger().error("failed to delete user {} social {}", userId, type);
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected UserSocial mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        UserSocialPlatform socialMedia = UserSocialPlatform.valueOf(resultSet.getString("social_media"));
        String socialMediaId = resultSet.getString("social_media_id");
        return new UserSocial(id, userId, socialMedia, socialMediaId);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "social_media TEXT," +
                    "social_media_id TEXT," +
                    "CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "CONSTRAINT uk_user_social_media UNIQUE (user_id, social_media)" + // unique type per user
                    ");").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user socials table", e);

        }
    }
}
