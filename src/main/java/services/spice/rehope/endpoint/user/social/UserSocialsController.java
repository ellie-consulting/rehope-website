package services.spice.rehope.endpoint.user.social;

import io.avaje.http.api.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;
import services.spice.rehope.model.ApiController;

@Controller("/api/user/:userId/socials")
public class UserSocialsController extends ApiController {

    private final UserSocialsService service;

    @Inject
    public UserSocialsController(UserSocialsService service) {
        this.service = service;
    }

    @Get
    public void get(Context context, int userId, @QueryParam("platform") UserSocialPlatform platform) {
        if (platform != null) {
            UserSocial userSocial = service.getUserSocial(userId, platform).orElse(null);

            if (userSocial != null) {
                context.json(userSocial, UserSocial.class);
            } else {
                context.result("null");
            }

            return;
        }

        // todo respect privacy

        context.json(service.getUserSocials(userId).values(), UserSocial.class);
    }

    @Post
    public UserSocial connect(Context context, int userId, @QueryParam("platform") UserSocialPlatform platform) {
        if (!assertSelfOrStaff(context, userId)) {
            unauthorized(context, "You can only perform this action on your own account.");
            return null;
        }

        // todo need to connect

        return service.getUserSocial(userId, platform).orElse(null);
    }

    @Delete
    public boolean unlink(Context context, int userId, @QueryParam("platform") UserSocialPlatform platform) {
        if (platform == null) {
            context.status(HttpStatus.BAD_REQUEST);
            context.result("no platform");
            return false;
        }

        if (!assertSelfOrStaff(context, userId)) {
            return unauthorized(context, "You can only perform this action on your own account.");
        }

        return service.deleteUserSocial(userId, platform);
    }

}
