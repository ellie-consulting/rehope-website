package services.spice.rehope.endpoint.inventory.unlock;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;
import services.spice.rehope.endpoint.EndpointRoles;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.model.ApiController;

import java.util.List;

@Controller("/api/code")
public class UnlockController extends ApiController {

    private final UnlockService service;

    @Inject
    public UnlockController(UnlockService service) {
        this.service = service;
    }

    @Get
    @EndpointRoles(UserRole.ADMIN)
    public List<UnlockCode> getCodes() {
        return service.getAllCodes();
    }

    @Post
    @EndpointRoles(UserRole.ADMIN)
    public boolean addCode(UnlockCode code) {
        return service.createCode(code);
    }

    @Delete
    @EndpointRoles(UserRole.ADMIN)
    public boolean deleteCode(String code) {
        return service.deleteCode(code);
    }

    @Post("/redeem")
    @EndpointRoles(UserRole.ADMIN)
    public boolean redeemCode(Context context, String code) {
        if (code == null || code.isBlank()) {
            context.status(HttpStatus.BAD_REQUEST).json("No code specified");
            return false;
        }

        Integer selfUserId = userId(context);
        if (selfUserId == null) {
            return false;
        }

        return service.redeemCode(code, selfUserId);
    }

}
