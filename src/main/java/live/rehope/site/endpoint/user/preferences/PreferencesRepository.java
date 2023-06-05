package live.rehope.site.endpoint.user.preferences;

import io.avaje.inject.RequiresBean;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
import java.util.Optional;
import java.util.Set;

@Singleton
@RequiresBean(PrincipleUserRepository.class)
public class PreferencesRepository extends Repository<UserPreferences> {
    private static final String TABLE = "user_preferences";
    private static final Logger LOGGER = LoggerFactory.getLogger(PreferencesRepository.class);

    /**
     * Preference keys.
     */
    private static final Set<String> PREFERENCE_KEYS = Set.of(
            "mailing_list", "private_profile",
            "animated_background", "animated_interfaces", "site_music"
    );

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
     * @param settingId Setting id.
     * @return The state.
     */
    public boolean getPreferenceState(int userId, @NotNull String settingId) {
        if (!PREFERENCE_KEYS.contains(settingId.toLowerCase())) {
            return false;
        }

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT ? FROM " + TABLE + " WHERE user_id = ? LIMIT 1");
            statement.setString(1, settingId);
            statement.setInt(2, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(settingId);
            }

        } catch (SQLException e) {
            getLogger().error("failed to get preference state of {} for {}", settingId, userId);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update a user preference.
     *
     * @param userId User to set for.
     * @param settingId Setting id.
     * @param state Preference state.
     */
    public void updatePreference(int userId, @NotNull String settingId, boolean state) {
        if (!PREFERENCE_KEYS.contains(settingId.toLowerCase())) {
            throw new NotFoundResponse(settingId + " is not a valid preference key.");
        }

        updateField("user_id", userId, settingId.toLowerCase(), state);
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
                    "id SERIAL PRIMARY KEY," +
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
