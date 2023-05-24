package services.spice.rehope.endpoint.inventory.element;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.endpoint.inventory.element.model.InventoryElement;
import services.spice.rehope.endpoint.inventory.element.model.UnlockObjective;

import java.util.List;

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

    /**
     * Register an item definition.
     *
     * @param element Element to register.
     */
    public void addItem(@NotNull InventoryElement element) {
        elementRepository.addItem(element);
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
    public void updateElement(@NotNull InventoryElement element) {
        elementRepository.updateElement(element);
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
