package services.spice.rehope.endpoint.inventory.user;

import io.avaje.http.api.*;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;
import services.spice.rehope.endpoint.EndpointRoles;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.model.ApiController;

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
        if (!assertSelfOrStaff(context, userId)) {
            context.status(HttpStatus.FORBIDDEN);
            return List.of();
        }

        return inventoryService.getInventory(userId);
    }

    @Get("/context/:streamerId")
    @EndpointRoles(UserRole.USER)
    public List<UserInventoryElement> getInventory(Context context, int userId, int streamerId) {
        if (!assertSelfOrStaff(context, userId)) {
            context.status(HttpStatus.FORBIDDEN);
            return List.of();
        }

        return inventoryService.getInventoryInContext(userId, streamerId);
    }

    @Post("/add")
    @EndpointRoles(UserRole.ADMIN)
    public boolean addItem(int userId, int elementId) {
        return inventoryService.addToInventory(userId, elementId);
    }

    @Delete("/remove")
    @EndpointRoles(UserRole.ADMIN)
    public void removeItem(int userId, int elementId, @QueryParam Integer userContext) {
        inventoryService.deleteItemFromInventory(userId, elementId, userContext);
    }

}
