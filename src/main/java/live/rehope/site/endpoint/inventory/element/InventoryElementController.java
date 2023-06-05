package live.rehope.site.endpoint.inventory.element;

import io.avaje.http.api.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.inventory.element.model.InventoryElement;
import live.rehope.site.endpoint.user.principle.UserRole;
import live.rehope.site.model.ApiController;

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

    @Get("/{elementId}")
    public InventoryElement getElementById(int elementId) {
        return elementService.getElementById(elementId).orElseThrow(() -> new NotFoundResponse("no element by id " + elementId));
    }

    @Post
    public InventoryElement add(InventoryElement element) {
        if (element == null) {
            throw new BadRequestResponse("no element object");
        }

        return elementService.addItem(element);
    }

    @Delete("/{elementId}")
    public void remove(int elementId) {
        elementService.deleteItem(elementId);
    }

    @Patch("/{elementId}")
    public InventoryElement update(int elementId, InventoryElement element) {
        return elementService.updateElement(elementId, element);
    }

}
