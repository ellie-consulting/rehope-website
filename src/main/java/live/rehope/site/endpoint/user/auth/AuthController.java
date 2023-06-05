package live.rehope.site.endpoint.user.auth;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import jakarta.inject.Inject;
import live.rehope.site.endpoint.user.principle.model.PrincipleUser;
import live.rehope.site.model.ApiController;

@Controller("/api/auth")
public class AuthController extends ApiController {
    private final AuthService service;

    @Inject
    public AuthController(AuthService service) {
        this.service = service;
    }

    @Get("/oauth/{provider}/login")
    public PrincipleUser login(Context context, String provider) {
        if (optionalUserId(context).isPresent()) {
            throw new BadRequestResponse("Already logged in");
        }

        AuthProviderSource source = AuthProviderSource.valueOf(provider.toUpperCase());
        return service.handleLogin(source, context);
    }

    @Get("/oauth/callback")
    public void callback(Context context) {
        service.handleCallback(context);
    }

    @Get("/logout")
    public void logout(Context context) {
        service.handleLogout(context);
    }

}
