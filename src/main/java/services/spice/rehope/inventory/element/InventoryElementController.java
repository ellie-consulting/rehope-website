package services.spice.rehope.inventory.element;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import services.spice.rehope.inventory.element.model.InventoryElement;

import java.util.List;

@Controller("/api/inventory/elements")
public class InventoryElementController {
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
    @RolesAllowed("ADMIN")
    public boolean add(InventoryElement element) {
        return elementService.addItem(element);
    }

//    @Post("/remove")
//    @RolesAllowed("ADMIN")
//    public boolean remove(InventoryElement element) {
//        return elementService.
//    }

    @Post("/update")
    @RolesAllowed("ADMIN")
    public InventoryElement update(InventoryElement element) {
        elementService.updateElement(element);

        return element;
    }

}
