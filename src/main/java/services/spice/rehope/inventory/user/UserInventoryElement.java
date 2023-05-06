package services.spice.rehope.inventory.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import services.spice.rehope.inventory.element.model.InventoryElement;

import java.sql.Time;

/**
 * Represents an element in a user's inventory.
 *
 * @param id UserInventoryElement id.
 * @param userId User who owns this.
 * @param elementId Foreign key to the element.
 * @param unlockTime When this was unlocked.
 * @param unlockCode The code used to unlock it.
 * @param unlockContextUser Unlocked in the context of this user.
 * @param unlockContextValue Unlock value.
 */
public record UserInventoryElement(int id, int userId, int elementId, Time unlockTime,
                                   @Nullable String unlockCode,
                                   @Nullable Integer unlockContextUser, @Nullable Float unlockContextValue) {


    public boolean equalsElement(@NotNull InventoryElement element) {
        return element.id() == elementId;
    }


}
