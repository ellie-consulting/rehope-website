package services.spice.rehope.inventory.user;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.inject.Inject;
import services.spice.rehope.user.principle.UserRole;

import java.util.List;

@Controller("/api/user/:userId/inventory")
public class UserInventoryController {

    private final UserInventoryService inventoryService;

    @Inject
    public UserInventoryController(UserInventoryService service) {
        this.inventoryService = service;
    }

    @Get
    public List<UserInventoryElement> getInventory(Context context, int userId) {
        // only allow non-staff to view their own inventory
        Integer selfUserId = context.sessionAttribute("user-id");
        UserRole role = context.sessionAttribute("role");

        if (selfUserId == null || role == null || (!role.isStaff() && selfUserId != userId)) {
            context.status(HttpStatus.FORBIDDEN);
            return List.of();
        }

        return inventoryService.getInventory(userId);
    }

}
