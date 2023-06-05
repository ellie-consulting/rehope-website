package live.rehope.site.endpoint.user.preferences;

import live.rehope.site.endpoint.user.preferences.model.UserPreferences;
import live.rehope.site.model.ApiController;
import io.avaje.http.api.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;

import java.util.Optional;

@Controller("/api/user/{userId}/preferences")
public class PreferencesController extends ApiController {

    private final PreferencesService service;

    @Inject
    public PreferencesController(PreferencesService service) {
        this.service = service;
    }

    @Get
    public void get(Context context, int userId, @QueryParam("id") String preferenceId) {
        assertSelfOrStaff(context, userId);

        if (preferenceId != null) {
            boolean preferenceState = service.getPreferenceState(userId, preferenceId);
            context.json(preferenceState);
            return;
        }

        Optional<UserPreferences> userPreferences = service.getUserPreferences(userId);
        if (userPreferences.isPresent()) {
            context.json(userPreferences.get());
        } else {
            context.status(HttpStatus.NOT_FOUND);
        }
    }

    @Post
    public void set(Context context, int userId, String preferenceId, boolean value) {
        assertSelfOrStaff(context, userId);

        service.updatePreference(userId, preferenceId, value);
    }

}
