package live.rehope.site.endpoint.user.preferences;

import io.avaje.inject.RequiresBean;
import io.javalin.http.BadRequestResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.user.preferences.model.Preference;
import live.rehope.site.endpoint.user.preferences.model.UserPreferences;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import live.rehope.site.datasource.PostgreDatasource;
import live.rehope.site.endpoint.user.principle.PrincipleUserRepository;
import live.rehope.site.model.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
@RequiresBean(PrincipleUserRepository.class)
public class PreferencesRepository extends Repository<UserPreferences> {
    private static final String TABLE = "user_preferences";
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferencesRepository.class);

    @Inject
    public PreferencesRepository(PostgreDatasource datasource) {
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
     * Get a user's preferences.
     *
     * @param userId User.
     * @return Their preferences, if present.
     */
    @NotNull
    public Optional<UserPreferences> getUserPreferences(int userId) {
        return getByField("user_id", userId);
    }

    /**
     * Get the state of a single preference.
     *
     * @param userId User id.
     * @param preference Preference
     * @return The state.
     */
    public boolean getPreferenceState(int userId, @NotNull Preference preference) {
        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT ? FROM " + TABLE + " WHERE user_id = ? LIMIT 1");
            statement.setString(1, preference.getSqlKey());
            statement.setInt(2, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(preference.getSqlKey());
            }

        } catch (SQLException e) {
            getLogger().error("failed to get preference state of {} for {}", preference.getFieldName(), userId);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update a user preference.
     *
     * @param userId User to set for.
     * @param preference Preference
     * @param state Preference state.
     */
    public void updatePreference(int userId, @NotNull Preference preference, boolean state) {
        String key = preference.getSqlKey();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE + " " +
                    " (user_id, " + key + ") VALUES (?, ?)" +
                    "ON CONFLICT (user_id) DO UPDATE SET " + key + " = EXCLUDED." + key);
            statement.setInt(1, userId);
            statement.setBoolean(2, state);

            if (statement.executeUpdate() == 0) {
                throw new BadRequestResponse(preference + " was already set to " + state);
            }
        } catch (SQLException e) {
            getLogger().error("failed to set preference state of {} for {} -> {}", userId, preference, state);
            e.printStackTrace();
        }
    }

    @Override
    protected UserPreferences mapResultSetToType(@NotNull ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("user_id");
        boolean mailingList = resultSet.getBoolean("mailing_list");
        boolean privateProfile = resultSet.getBoolean("private_profile");
        boolean animatedBackground = resultSet.getBoolean("animated_background");
        boolean animatedInterfaces = resultSet.getBoolean("animated_interfaces");
        boolean siteMusic = resultSet.getBoolean("site_music");

        return new UserPreferences(userId, mailingList, privateProfile, animatedBackground, animatedInterfaces, siteMusic);
    }

    @Override
    protected void createTableIfNotExists() {
        try (Connection connection = datasource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    "id SERIAL," +
                    "user_id INTEGER NOT NULL UNIQUE REFERENCES " + PrincipleUserRepository.TABLE + "(id) ON DELETE CASCADE," +
                    "mailing_list BOOLEAN default true," +
                    "private_profile BOOLEAN default false," +
                    "animated_background BOOLEAN default true," +
                    "animated_interfaces BOOLEAN default true," +
                    "site_music BOOLEAN default true" +
                    ");").execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user settings table", e);
        }
    }
}
