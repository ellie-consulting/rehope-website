package live.rehope.site.endpoint.media.model;

import java.time.LocalDateTime;

public record VideoPublishEvent(String id, String title, String channelId, LocalDateTime publishedAt, String channelTitle) {
//          "id": "video_id",
//          "title": "Video Title",
//          "published_at": "2023-05-25T10:30:00Z",
//          "channel_id": "channel_id",
//          "channel_title": "Channel Title"
}