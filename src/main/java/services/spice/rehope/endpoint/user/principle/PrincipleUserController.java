package services.spice.rehope.endpoint.user.principle;

import io.avaje.http.api.Controller;
import io.avaje.http.api.FormParam;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import services.spice.rehope.endpoint.EndpointRoles;
import services.spice.rehope.endpoint.user.principle.exception.InvalidUsernameException;
import services.spice.rehope.model.ApiController;
import services.spice.rehope.util.ContextUtils;

import java.util.List;

@Controller("/api/user")
public class PrincipleUserController extends ApiController {

    private final PrincipleUserService service;

    public PrincipleUserController(PrincipleUserService service) {
        this.service = service;
    }

    @Get
    @EndpointRoles(UserRole.ADMIN)
    public List<PrincipleUser> getAll() {
        return service.getUsers();
    }

    @Post("/setupUsername")
    public void setupUsername(Context context, @FormParam String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException(InvalidUsernameException.Reason.EMPTY);
        }

        if (ContextUtils.username(context).isPresent()) {
            throw new BadRequestResponse("You've already set your username.");
        }

        service.updateUsername(userId(context), username);
        ContextUtils.updateUsername(context, username);
    }

}
