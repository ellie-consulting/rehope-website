package live.rehope.site.endpoint.inventory.user;

import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import live.rehope.site.model.ApiController;
import io.avaje.http.api.*;
import io.javalin.http.Context;
import jakarta.inject.Inject;

import java.util.List;

@Controller("/api/user/:userId/inventory")
public class UserInventoryController extends ApiController {

    private final UserInventoryService inventoryService;

    @Inject
    public UserInventoryController(UserInventoryService service) {
        this.inventoryService = service;
    }

    @Get
    @EndpointRoles(UserRole.USER)
    public List<UserInventoryElement> getInventory(Context context, int userId) {
        assertSelfOrStaff(context, userId);

        return inventoryService.getInventory(userId);
    }

    @Get("/context/:streamerId")
    @EndpointRoles(UserRole.USER)
    public List<UserInventoryElement> getInventory(Context context, int userId, int streamerId) {
        assertSelfOrStaff(context, userId);

        return inventoryService.getInventoryInContext(userId, streamerId);
    }

    @Post
    @EndpointRoles(UserRole.ADMIN)
    public void addItem(int userId, int elementId) {
        inventoryService.addToInventory(userId, elementId);
    }

    @Delete
    @EndpointRoles(UserRole.ADMIN)
    public void removeItem(int userId, int elementId, @QueryParam Integer userContext) {
        inventoryService.deleteItemFromInventory(userId, elementId, userContext);
    }

}
