package services.spice.rehope.endpoint.inventory.element;

import io.avaje.http.api.*;
import jakarta.inject.Inject;
import services.spice.rehope.endpoint.EndpointRoles;
import services.spice.rehope.endpoint.inventory.element.model.InventoryElement;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.model.ApiController;

import java.util.List;

@Controller("/api/inventory/elements")
@EndpointRoles(UserRole.ADMIN)
public class InventoryElementController extends ApiController {
    private final InventoryElementService elementService;

    @Inject
    public InventoryElementController(InventoryElementService elementService) {
        this.elementService = elementService;
    }

    @Get
    public List<InventoryElement> getAll() {
        return elementService.getAll();
    }

    @Post
    public boolean add(InventoryElement element) {
        return elementService.addItem(element);
    }

    @Delete
    public boolean remove(int elementId) {
        return elementService.deleteItem(elementId);
    }

    @Patch
    public InventoryElement update(InventoryElement element) {
        if (elementService.updateElement(element)) {
            return element;
        }

        return null;
    }

}
