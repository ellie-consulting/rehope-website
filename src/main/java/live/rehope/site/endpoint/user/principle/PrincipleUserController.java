package live.rehope.site.endpoint.user.principle;

import io.avaje.http.api.Controller;
import io.avaje.http.api.FormParam;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.javalin.http.*;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.user.principle.exception.InvalidUsernameException;
import live.rehope.site.endpoint.user.principle.model.PrincipleUser;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import live.rehope.site.model.ApiController;
import live.rehope.site.util.ContextUtils;

import java.util.List;

@Controller("/api/")
public class PrincipleUserController extends ApiController {

    private final PrincipleUserService service;

    public PrincipleUserController(PrincipleUserService service) {
        this.service = service;
    }

    @Get("/user")
    public PrincipleUser getSelf(Context ctx) {
        return service.getUserById(userId(ctx)).orElseThrow(UnauthorizedResponse::new);
    }

    @Get("/user/id/{userId}")
    public PrincipleUser getById(Context ctx, int userId) {
        PrincipleUser user = service.getUserById(userId).orElseThrow(NotFoundResponse::new);
        if (canViewSensitiveData(ctx, userId)) {
            return user;
        }

        return user.sensitiveCopy();
    }

    @Get("/user/name/{username}")
    public PrincipleUser getByUsername(Context ctx, String username) {
        PrincipleUser user = service.getUserByUsername(username).orElseThrow(NotFoundResponse::new);
        if (canViewSensitiveData(ctx, user.id())) {
            return user;
        }

        return user.sensitiveCopy();
    }

    @Get("/user/email/{email}")
    @EndpointRoles(UserRole.ADMIN)
    public PrincipleUser getByEmail(String email) {
        return service.getUserByEmail(email).orElseThrow(NotFoundResponse::new);
    }

    @Get("/users")
    @EndpointRoles(UserRole.ADMIN)
    public List<PrincipleUser> getAll() {
        return service.getUsers();
    }

    @Post("/user/{userId}/setupUsername")
    public void setupUsername(Context context, int userId, @FormParam String username) {
        assertSelfOrStaff(context, userId);

        if (username == null || username.isBlank()) {
            throw new InvalidUsernameException(InvalidUsernameException.Reason.EMPTY);
        }

        if (!userRole(context).isStaff() && ContextUtils.username(context).isPresent()) {
            throw new BadRequestResponse("You've already set your username.");
        }

        service.updateUsername(userId, username);

        if (userId == userId(context)) {
            ContextUtils.updateUsername(context, username);
        } else {
            // how will be update another's context.
        }
    }

}
