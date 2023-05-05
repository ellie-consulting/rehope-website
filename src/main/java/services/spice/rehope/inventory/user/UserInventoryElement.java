package services.spice.rehope.inventory.user;

import java.sql.Time;

/**
 * Represents an element in a user's inventory.
 *
 * @param id UserInventoryElement id.
 * @param userId User who owns this.
 * @param elementId Foreign key to the element.
 * @param unlockTime When this was unlocked.
 * @param unlockCode The code used to unlock it.
 * @param relatedUserId User id this is related to. (a streamer they donated to?)
 * @param value Value (hours / money?) TODO better name
 */
public record UserInventoryElement(int id, int userId, int elementId, Time unlockTime, String unlockCode,
                                   int relatedUserId, float value) {
}
