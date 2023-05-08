package services.spice.rehope.endpoint.user.auth;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
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
        if (userId(context) != null) {
            context.json("You are already logged in.");
            return;
        }

        service.loginGoogle(context);
    }

    @Get("/oauth/google/callback")
    public void googleCallback(Context context) {
        service.callbackGoogle(context);
    }

    @Post("/logout")
    public void logout(Context context) {

    }

}
