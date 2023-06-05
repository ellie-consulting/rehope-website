package live.rehope.site.endpoint.inventory.unlock;

import io.avaje.http.api.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import live.rehope.site.model.ApiController;

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

    @Get("/{codeId}")
    @EndpointRoles(UserRole.ADMIN)
    public UnlockCode getCodeById(int codeId) {
        return service.getCodeById(codeId).orElseThrow(() -> new NotFoundResponse("no code by id " + codeId));
    }

    @Post
    @EndpointRoles(UserRole.ADMIN)
    public UnlockCode addCode(UnlockCode code) {
        return service.createCode(code);
    }

    @Delete("/{codeId}")
    @EndpointRoles(UserRole.ADMIN)
    public void deleteCode(int codeId) {
        service.deleteCode(codeId);
    }

    @Post("/redeem")
    @EndpointRoles(UserRole.USER)
    public void redeemCode(Context context, @QueryParam String code) {
        if (code == null || code.isBlank()) {
            throw new BadRequestResponse("No code specified");
        }

        int selfUserId = userId(context);
        service.redeemCode(code, selfUserId);
        // todo return the redeemed code?
    }

}
