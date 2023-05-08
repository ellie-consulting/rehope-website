package services.spice.rehope.endpoint.inventory.element;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
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

    @Post("/add")
    public boolean add(InventoryElement element) {
        return elementService.addItem(element);
    }

    @Delete("/remove")
    public boolean remove(int elementId) {
        return elementService.deleteItem(elementId);
    }

    @Post("/update")
    public InventoryElement update(InventoryElement element) {
        if (elementService.updateElement(element)) {
            return element;
        }

        return null;
    }

}
