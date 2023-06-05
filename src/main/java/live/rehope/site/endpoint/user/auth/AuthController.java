package live.rehope.site.endpoint.user.auth;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import jakarta.inject.Inject;
import live.rehope.site.endpoint.user.principle.PrincipleUser;
import live.rehope.site.model.ApiController;

@Controller("/api/auth")
public class AuthController extends ApiController {
    private final AuthService service;

    @Inject
    public AuthController(AuthService service) {
        this.service = service;
    }

    @Get("/login/google")
    public PrincipleUser googleLogin(Context context) {
        if (optionalUserId(context).isPresent()) {
            throw new BadRequestResponse("Already logged in");
        }

        return service.handleLogin(AuthProviderSource.GOOGLE, context);
    }

    @Get("/login/twitter")
    public PrincipleUser twitterLogin(Context context) {
        if (optionalUserId(context).isPresent()) {
            throw new BadRequestResponse("Already logged in");
        }

        return service.handleLogin(AuthProviderSource.TWITTER, context);
    }

    @Get("/login/discord")
    public PrincipleUser discordLogin(Context context) {
        return service.handleLogin(AuthProviderSource.DISCORD, context);
    }

    @Get("/oauth/google/callback")
    public void googleCallback(Context context) {
        service.handleCallback(AuthProviderSource.GOOGLE, context);
    }

    @Get("/oauth/twitter/callback")
    public void twitterCallback(Context context) {
        service.handleCallback(AuthProviderSource.TWITTER, context);
    }

    @Get("/oauth/discord/callback")
    public void discordCallback(Context context) {
        service.handleCallback(AuthProviderSource.DISCORD, context);
    }

    @Get("/logout")
    public void logout(Context context) {
        service.handleLogout(context);
    }

}
