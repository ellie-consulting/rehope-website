package live.rehope.site.endpoint.inventory.element.model;

import org.jetbrains.annotations.Nullable;
import live.rehope.site.endpoint.inventory.user.UserInventoryElement;

/**
 * Represents an element that can be placed in an inventory.
 *
 * @param id Database id.
 * @param type Type.
 * @param unlockObjective Unlock objective.
 * @param unlockValue Value for the unlock objective.
 * @param name Pretty name.
 * @param description Description.
 * @param iconUri Uri to icon.
 */
public record InventoryElement(int id, ElementType type,
                               @Nullable UnlockObjective unlockObjective, float unlockValue,
                               String name, String description, String iconUri) {

    public boolean canBeUnlocked() {
        return unlockObjective != null && unlockValue >= 0;
    }

    public UserInventoryElement toUserInventoryElement(int userId, @Nullable String unlockCode,
                                                       @Nullable Integer unlockContextUser, @Nullable Float unlockContextValue) {
        return new UserInventoryElement(0, userId, id, null, unlockCode, unlockContextUser, unlockContextValue);
    }

}
