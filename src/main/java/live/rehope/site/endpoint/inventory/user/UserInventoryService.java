package live.rehope.site.endpoint.inventory.user;

import live.rehope.site.endpoint.inventory.element.model.UnlockObjective;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import live.rehope.site.endpoint.inventory.element.model.InventoryElement;
import live.rehope.site.endpoint.inventory.element.InventoryElementService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return inventoryRepository.getUserInventory(userId, null, null);
    }

    @NotNull
    public List<UserInventoryElement> getInventoryInContext(int userId, int streamerId) {
        return inventoryRepository.getUserInventory(userId, streamerId, null);
    }

    @NotNull
    public List<UserInventoryElement> getCompletedElements(int userId) {
        return inventoryRepository.getUserInventory(userId, null, false);
    }

    @NotNull
    public List<UserInventoryElement> getCompletedElementsInContext(int userId, int streamerId) {
        return inventoryRepository.getUserInventory(userId, streamerId, false);
    }

    @NotNull
    public List<UserInventoryElement> getInProgressElements(int userId) {
        return inventoryRepository.getUserInventory(userId, null, true);
    }

    @NotNull
    public List<UserInventoryElement> getInProgressElementsInContext(int userId, int streamerId) {
        return inventoryRepository.getUserInventory(userId, streamerId, true);
    }

    public void addToInventory(int userId, int elementId) {
        inventoryRepository.addElementToInventory(userId, elementId, null, null, null);
    }

    public void addToInventoryFromCode(int userId, int elementId, String unlockCode) {
        inventoryRepository.addElementToInventory(userId, elementId, unlockCode, null, null);
    }

    public void addToInventoryFromEvent(int userId, int elementId, int userContext, float unlockValue) {
        inventoryRepository.addElementToInventory(userId, elementId, null, userContext, unlockValue);
    }

    public void deleteItemFromInventory(int userId, int elementId, @Nullable Integer context) {
        inventoryRepository.removeElementFromInventory(userId, elementId, context);
    }

    public boolean hasUnlockedElementWithCode(int userId, @NotNull String unlockCode) {
        return inventoryRepository.hasUsedCode(userId, unlockCode);
    }

    /**
     * Handles progress made from doing an event.
     *
     * @return Any completed inventory elements.
     */
    public List<UserInventoryElement> handleInventoryProgress(int userId, int streamerContext, UnlockObjective objective, float progress) {
        List<InventoryElement> eligibleElements = elementService.getElementsByUnlockObjective(objective);
        if (eligibleElements.isEmpty()) {
            return List.of();
        }

        List<UserInventoryElement> progressable = getInProgressElementsInContext(userId, streamerContext);
        if (progressable.isEmpty()) {
            return List.of();
        }

        List<UserInventoryElement> updatedElements = new ArrayList<>();

        // find what user doesn't have.
        for (InventoryElement eligibleElement : eligibleElements) {
            boolean found = false;

            for (UserInventoryElement progressableElement : progressable) {
                if (!progressableElement.equalsElement(eligibleElement)) {
                    continue;
                }

                if (progressableElement.willBeCompleted(eligibleElement, progress)) {
                    updatedElements.add(
                            progressableElement.completed(progress)
                    );
                } else {
                    updatedElements.add(
                            progressableElement.addProgress(progress)
                    );
                }
                break;
            }

            if (!found) {
                boolean completed = progress >= eligibleElement.unlockValue();
                UserInventoryElement newElement = completed
                        ? UserInventoryElement.completed(userId, eligibleElement.id(), streamerContext, progress, null)
                        : UserInventoryElement.inProgress(userId, eligibleElement.id(), streamerContext, progress);

                updatedElements.add(newElement);
            }
        }

        // submit to database
        inventoryRepository.addElementsToInventory(updatedElements);

        return updatedElements.stream()
                .filter(UserInventoryElement::hasBeenUnlocked).toList();
    }

    /*
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
//    public void handleUnlockForEvent(int userContext, int streamerContext, UnlockObjective objective, float unlockValue) {
//        List<InventoryElement> eligibleElements = elementService.getUnlockableElements(objective, unlockValue);
//        if (eligibleElements.isEmpty()) {
//            return;
//        }
//
//        // now to
//        List<UserInventoryElement> userInventory = getInventoryInContext(userContext, streamerContext);
//        List<InventoryElement> toUnlock = new ArrayList<>();
//
//        // find what user doesn't have.
//        for (InventoryElement eligibleElement : eligibleElements) {
//            boolean found = false;
//            for (UserInventoryElement userInventoryElement : userInventory) {
//                if (userInventoryElement.equalsElement(eligibleElement)) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                toUnlock.add(eligibleElement);
//            }
//        }
//
//        if (toUnlock.isEmpty()) {
//            return;
//        }
//
//        // unlock everything
//
//        List<UserInventoryElement> userUnlockElements = toUnlock.stream()
//                .map(element -> element.toUserInventoryElement(userContext, null, streamerContext, unlockValue))
//                .toList();
//
//        inventoryRepository.addElementsToInventory(userUnlockElements);
//    }

}
