package live.rehope.site.endpoint.user.preferences;

import io.javalin.http.NotFoundResponse;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.user.preferences.model.UserPreferences;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import live.rehope.site.model.ApiController;
import io.avaje.http.api.*;
import io.javalin.http.Context;
import jakarta.inject.Inject;

@Controller("/api/user/{userId}/preferences")
@EndpointRoles(UserRole.USER)
public class PreferencesController extends ApiController {

    private final PreferencesService service;

    @Inject
    public PreferencesController(PreferencesService service) {
        this.service = service;
    }

    @Get
    public UserPreferences getPreferences(Context context, int userId) {
        assertSelfOrStaff(context, userId);

        return service.getUserPreferences(userId);
    }

    @Get("/{preference}")
    public boolean getPreference(Context context, int userId, String preference) {
        assertSelfOrStaff(context, userId);

        return service.getPreferenceState(userId, preference);
    }

    @Post("/{preference}/{value}")
    public void set(Context context, int userId, String preference, boolean value) {
        assertSelfOrStaff(context, userId);

        service.updatePreference(userId, preference, value);
    }

}
