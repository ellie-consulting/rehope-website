package services.spice.rehope.endpoint.user.preferences;

import io.avaje.http.api.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;
import services.spice.rehope.model.ApiController;

import java.util.Optional;

@Controller("/api/user/:userId/preferences")
public class PreferencesController extends ApiController {

    private final PreferencesService service;

    @Inject
    public PreferencesController(PreferencesService service) {
        this.service = service;
    }

    @Get
    public void get(Context context, int userId, @QueryParam("id") String preferenceId) {
        if (!assertSelfOrStaff(context, userId)) {
            unauthorized(context, "You can only see your own preferences.");
            return;
        }

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
    public boolean set(Context context, int userId, String preferenceId, boolean value) {
        if (!assertSelfOrStaff(context, userId)) {
            return unauthorized(context, "You can only set your own preferences.");
        }

        return service.updatePreference(userId, preferenceId, value);
    }

}
