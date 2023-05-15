package services.spice.rehope.endpoint.user.preferences;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Singleton
public class PreferencesService {

    private final PreferencesRepository repository;

    @Inject
    public PreferencesService(PreferencesRepository repository) {
        this.repository = repository;
    }

    /**
     * Get a user's preferences.
     *
     * @param userId User.
     * @return Their preferences, if present.
     */
    @NotNull
    public Optional<UserPreferences> getUserPreferences(int userId) {
        return repository.getUserPreferences(userId);
    }

    /**
     * Get the state of a single preference.
     *
     * @param userId User id.
     * @param settingId Setting id.
     * @return The state.
     */
    public boolean getPreferenceState(int userId, @NotNull String settingId) {
        return repository.getPreferenceState(userId, settingId);
    }

    /**
     * Update a user preference.
     *
     * @param userId User to set for.
     * @param settingId Setting id.
     * @param state Preference state.
     * @return If set correctly.
     */
    public boolean updatePreference(int userId, @NotNull String settingId, boolean state) {
        return repository.updatePreference(userId, settingId, state);
    }

}
