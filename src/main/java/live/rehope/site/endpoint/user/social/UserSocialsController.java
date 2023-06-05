package live.rehope.site.endpoint.user.social;

import live.rehope.site.model.ApiController;
import io.avaje.http.api.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;

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
            UserSocial userSocial = service.getUserSocial(userId, platform).orElseThrow(NotFoundResponse::new);

            context.json(userSocial, UserSocial.class);
            return;
        }

        // todo respect privacy

        context.json(service.getUserSocials(userId).values(), UserSocial.class);
    }

    @Post
    public void connect(Context context, int userId, @QueryParam("platform") UserSocialPlatform platform) {
        assertSelfOrStaff(context, userId);

        // todo need to connect via oauth

//        return service.getUserSocial(userId, platform).orElseThrow(() -> );
    }

    @Delete
    public void unlink(Context context, int userId, @QueryParam("platform") UserSocialPlatform platform) {
        if (platform == null) {
            throw new BadRequestResponse("No platform specified");
        }

        assertSelfOrStaff(context, userId);

        service.deleteUserSocial(userId, platform);
    }

}
