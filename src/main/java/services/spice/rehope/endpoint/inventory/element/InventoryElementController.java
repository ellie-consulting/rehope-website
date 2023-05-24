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
    public void add(InventoryElement element) {
        elementService.addItem(element);
    }

    @Delete
    public void remove(int elementId) {
        elementService.deleteItem(elementId);
    }

    @Patch
    public void update(InventoryElement element) {
        elementService.updateElement(element);
    }

}
