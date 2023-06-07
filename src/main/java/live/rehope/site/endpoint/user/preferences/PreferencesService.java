package live.rehope.site.endpoint.user.preferences;

import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.user.preferences.model.Preference;
import live.rehope.site.endpoint.user.preferences.model.UserPreferences;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PreferencesService {

    private final PreferencesRepository repository;

    @Inject
    public PreferencesService(PreferencesRepository repository) {
        this.repository = repository;
    }

    /**
     * Get a user's preferences, or defaults.
     *
     * @param userId User.
     * @return Their preferences, if present.
     */
    @NotNull
    public UserPreferences getUserPreferences(int userId) {
        return repository.getUserPreferences(userId).orElse(UserPreferences.defaults(userId));
    }

    /**
     * Get the state of a single preference.
     *
     * @param userId User id.
     * @param preferenceId Setting id.
     * @return The state.
     */
    public boolean getPreferenceState(int userId, @NotNull String preferenceId) {
        Preference preference = Preference.byFieldName(preferenceId);
        if (preference == null) {
            throw new NotFoundResponse(preferenceId + " does not exist");
        }

        return repository.getPreferenceState(userId, preference);
    }

    /**
     * Update a user preference.
     *
     * @param userId User to set for.
     * @param preferenceId Preference id to update.
     * @param state Preference state.
     */
    public void updatePreference(int userId, @NotNull String preferenceId, boolean state) {
        Preference preference = Preference.byFieldName(preferenceId);
        if (preference == null) {
            throw new NotFoundResponse(preferenceId + " does not exist");
        }

        repository.updatePreference(userId, preference, state);
    }

}
