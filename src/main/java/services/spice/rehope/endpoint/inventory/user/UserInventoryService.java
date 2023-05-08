package services.spice.rehope.endpoint.inventory.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import services.spice.rehope.endpoint.inventory.element.model.InventoryElement;
import services.spice.rehope.endpoint.inventory.element.model.UnlockObjective;
import services.spice.rehope.endpoint.inventory.element.InventoryElementService;
import services.spice.rehope.endpoint.inventory.user.UserInventoryElement;
import services.spice.rehope.endpoint.inventory.user.UserInventoryRepository;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserInventoryService {
    private final UserInventoryRepository inventoryRepository;
    private final InventoryElementService elementService;

    @Inject
    public UserInventoryService(UserInventoryRepository repository, InventoryElementService elementService) {
        this.inventoryRepository = repository;
        this.elementService = elementService;
    }

    @NotNull
    public List<UserInventoryElement> getInventory(int userId) {
        return inventoryRepository.getUserInventory(userId, null);
    }

    @NotNull
    public List<UserInventoryElement> getInventoryInContext(int userId, int streamerId) {
        return inventoryRepository.getUserInventory(userId, streamerId);
    }

    public boolean addToInventory(int userId, int elementId) {
        return inventoryRepository.addElementToInventory(userId, elementId, null, null, null);
    }

    public boolean addToInventoryFromCode(int userId, int elementId, String unlockCode) {
        return inventoryRepository.addElementToInventory(userId, elementId, unlockCode, null, null);
    }

    public boolean addToInventoryFromEvent(int userId, int elementId, int userContext, float unlockValue) {
        return inventoryRepository.addElementToInventory(userId, elementId, null, userContext, unlockValue);
    }

    public boolean deleteItemFromInventory(int userId, int elementId, @Nullable Integer context) {
        return inventoryRepository.removeElementFromInventory(userId, elementId, context);
    }

    public boolean hasUnlockedElementWithCode(int userId, @NotNull String unlockCode) {
        return inventoryRepository.hasUsedCode(userId, unlockCode);
    }

    /**
     * Handle unlocking of inventory elements for an event.
     * </br>
     * Will see if there is any eligible unlocks from this event,
     * then it will see what the user has in the context of the streamer,
     * then it will unlock anything the user doesn't have.
     *
     * @param userContext User id.
     * @param streamerContext Streamer user id.
     * @param objective Event objective.
     * @param unlockValue Value.
     */
    public void handleUnlockForEvent(int userContext, int streamerContext, UnlockObjective objective, float unlockValue) {
        List<InventoryElement> eligibleElements = elementService.getUnlockableElements(objective, unlockValue);
        if (eligibleElements.isEmpty()) {
            return;
        }

        // now to
        List<UserInventoryElement> userInventory = getInventoryInContext(userContext, streamerContext);
        List<InventoryElement> toUnlock = new ArrayList<>();

        // find what user doesn't have.
        for (InventoryElement eligibleElement : eligibleElements) {
            boolean found = false;
            for (UserInventoryElement userInventoryElement : userInventory) {
                if (userInventoryElement.equalsElement(eligibleElement)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                toUnlock.add(eligibleElement);
            }
        }

        if (toUnlock.isEmpty()) {
            return;
        }

        // unlock everything

        List<UserInventoryElement> userUnlockElements = toUnlock.stream()
                .map(element -> element.toUserInventoryElement(userContext, null, streamerContext, unlockValue))
                .toList();

        inventoryRepository.addElementsToInventory(userUnlockElements);
    }

}
