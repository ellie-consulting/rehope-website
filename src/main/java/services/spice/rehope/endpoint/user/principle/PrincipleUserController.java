package services.spice.rehope.endpoint.user.principle;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import services.spice.rehope.endpoint.EndpointRoles;
import services.spice.rehope.model.ApiController;

import java.util.List;

@Controller("/api/user")
public class PrincipleUserController extends ApiController {

    private final PrincipleUserService service;

    public PrincipleUserController(PrincipleUserService service) {
        this.service = service;
    }

    @Get
//    @EndpointRoles(UserRole.ADMIN)
    public List<PrincipleUser> getAll() {
        return service.getUsers();
    }

}
