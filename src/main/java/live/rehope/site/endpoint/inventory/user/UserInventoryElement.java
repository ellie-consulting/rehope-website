package live.rehope.site.endpoint.inventory.user;

import live.rehope.site.endpoint.inventory.element.model.InventoryElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;

/**
 * Represents an element in a user's inventory.
 *
 * @param id UserInventoryElement id.
 * @param userId User who owns this.
 * @param elementId Foreign key to the element.
 * @param creatorContext Element context, i.e an element for watching x minutes on [context] channel.
 * @param progressStart When progress was started.
 * @param progress Progress to unlock the element fully.
 * @param unlockTime When this was unlocked.
 * @param unlockCode The code used to unlock it.
 */
public record UserInventoryElement(int id, int userId, int elementId, @Nullable Integer creatorContext,
                                   @NotNull Timestamp progressStart, float progress,
                                   @Nullable Timestamp unlockTime, @Nullable String unlockCode) {

    public static UserInventoryElement inProgress(int userId, int elementId,
                                                  @Nullable Integer creatorContext, float progress) {
        return new UserInventoryElement(0, userId, elementId, creatorContext, new Timestamp(System.currentTimeMillis()), progress, null, null);
    }

    public static UserInventoryElement completed(int userId, int elementId,
                                                  @Nullable Integer creatorContext, float progress, @Nullable String unlockCode) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return new UserInventoryElement(0, userId, elementId, creatorContext, now, progress, now, unlockCode);
    }

    public boolean equalsElement(@NotNull InventoryElement element) {
        return element.id() == elementId;
    }

    public boolean willBeCompleted(@NotNull InventoryElement element, float extraProgress) {
        return progress + extraProgress >= element.unlockValue();
    }

    public boolean hasBeenUnlocked() {
        return unlockTime != null;
    }

    public UserInventoryElement addProgress(float progress) {
        return new UserInventoryElement(id, userId, elementId, creatorContext,
                progressStart, this.progress + progress, unlockTime, unlockCode);
    }

    public UserInventoryElement completed(float addedProgress) {
        return new UserInventoryElement(id, userId, elementId, creatorContext,
                progressStart, this.progress + progress, new Timestamp(System.currentTimeMillis()), unlockCode);
    }

}
