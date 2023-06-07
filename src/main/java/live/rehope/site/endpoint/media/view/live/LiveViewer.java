package live.rehope.site.endpoint.media.view.live;

import java.sql.Timestamp;

/**
 * Represents a user watching a video now (to the best of our knowledge).
 *
 * @param userId User id.
 * @param videoId Video id.
 * @param startedAt When they started watching.
 */
public record LiveViewer(int userId, String videoId, Timestamp startedAt) {
}
