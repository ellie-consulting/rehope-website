package live.rehope.site.endpoint.user.social;

import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import live.rehope.site.endpoint.user.social.model.UserSocial;
import live.rehope.site.endpoint.user.social.model.UserSocialPlatform;
import live.rehope.site.model.ApiController;
import io.avaje.http.api.*;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;

import java.util.Collection;

@Controller("/api/")
public class UserSocialsController extends ApiController {

    private final UserSocialsService service;

    @Inject
    public UserSocialsController(UserSocialsService service) {
        this.service = service;
    }

    @Get("/user/{userId}/socials")
    @EndpointRoles(UserRole.USER)
    public Collection<UserSocial> getSocials(int userId) {
        return service.getUserSocials(userId).values();
    }

    @Get("/user/{userId}/social/{platform}")
    @EndpointRoles(UserRole.USER)
    public UserSocial getSocial(int userId, UserSocialPlatform platform) {
        return service.getUserSocial(userId, platform).orElseThrow(() -> new NotFoundResponse("not linked"));
    }

    @Get("/user/{userId}/social/{platform}/connect")
    @EndpointRoles(UserRole.USER)
    public UserSocial connect(Context context, int userId, UserSocialPlatform platform) {
        assertSelfOrStaff(context, userId);

        return service.connectSocial(context, userId, platform);
    }

    @Get("/social/oauth/callback")
    @EndpointRoles(UserRole.USER)
    public void oauthCallback(Context context) {
        service.handleCallback(context);
    }

    @Delete("/user/{userId}/social/{platform}")
    @EndpointRoles(UserRole.USER)
    public void unlink(Context context, int userId, UserSocialPlatform platform) {
        assertSelfOrStaff(context, userId);

        service.deleteUserSocial(userId, platform);
    }

}
