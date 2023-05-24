package services.spice.rehope.endpoint.user.auth;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import jakarta.inject.Inject;
import services.spice.rehope.model.ApiController;

@Controller("/api/auth")
public class AuthController extends ApiController {
    private final AuthService service;

    @Inject
    public AuthController(AuthService service) {
        this.service = service;
    }

    @Get("/login/google")
    public void googleLogin(Context context) {
        if (optionalUserId(context).isPresent()) {
            throw new BadRequestResponse("Already logged in");
        }

        service.handleLogin(AuthProviderSource.GOOGLE, context);
    }

    @Get("/login/twitter")
    public void twitterLogin(Context context) {
        if (optionalUserId(context).isPresent()) {
            throw new BadRequestResponse("Already logged in");
        }

        service.handleLogin(AuthProviderSource.TWITTER, context);
    }

    @Get("/oauth/google/callback")
    public void googleCallback(Context context) {
        service.handleCallback(AuthProviderSource.GOOGLE, context);
    }

    @Get("/oauth/twitter/callback")
    public void twitterCallback(Context context) {
        service.handleCallback(AuthProviderSource.TWITTER, context);
    }

    @Get("/logout")
    public void logout(Context context) {
        service.handleLogout(context);
    }

}
