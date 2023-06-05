package live.rehope.site.endpoint.inventory.element;

import live.rehope.site.endpoint.inventory.element.model.InventoryElement;
import live.rehope.site.endpoint.inventory.element.model.UnlockObjective;
import io.javalin.http.InternalServerErrorResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Singleton
public class InventoryElementService {

    private final ElementRepository elementRepository;

    @Inject
    public InventoryElementService(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    /**
     * @return All registered inventory elements.
     */
    @NotNull
    public List<InventoryElement> getAll() {
        return elementRepository.getAll();
    }

    @NotNull
    public Optional<InventoryElement> getElementById(int id) {
        return elementRepository.getById(id);
    }

    /**
     * Register an item definition.
     *
     * @param element Element to register.
     * @return registered element
     */
    public InventoryElement addItem(@NotNull InventoryElement element) {
        int id = elementRepository.addItem(element);
        return elementRepository.getById(id).orElseThrow(InternalServerErrorResponse::new);
    }

    /**
     * Delete an item definition by its id.
     *
     * @param id Id to delete.
     */
    public void deleteItem(int id) {
        elementRepository.deleteElementById(id);
    }

    /**
     * Update an element.
     *
     * @param element Element to update.
     */
    public InventoryElement updateElement(int id, @NotNull InventoryElement element) {
        elementRepository.updateElement(id, element);
        return elementRepository.getById(element.id()).orElseThrow(InternalServerErrorResponse::new);
    }

    /**
     * Get all unlockable elements from this objective and value.
     *
     * @param unlockObjective In which object to achieve.
     * @param unlockValue The value obtained.
     * @return Elements that can be unlocked with this criteria.
     */
    @NotNull
    public List<InventoryElement> getUnlockableElements(@NotNull UnlockObjective unlockObjective, float unlockValue) {
        return elementRepository.getUnlockableElements(unlockObjective, unlockValue);
    }

}
