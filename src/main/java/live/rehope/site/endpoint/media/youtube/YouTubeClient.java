package live.rehope.site.endpoint.media.youtube;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.avaje.config.Config;
import jakarta.inject.Singleton;
import live.rehope.site.util.DateUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import live.rehope.site.endpoint.media.model.Media;
import live.rehope.site.endpoint.media.model.MediaType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Client to query youtube things.
 */
@Singleton
public class YouTubeClient {
    private static final String SUB_CALLBACK = Config.get("host.domain") + "/api/media/notify";
    private static final String BASE_URL = "https://www.googleapis.com/youtube/v3";
    private static final String KEY_SUFFIX = "?key=%s";

    private final OkHttpClient client;
    private String key;
    private String subscriberSecret;

    public YouTubeClient() {
        this.client = new OkHttpClient();
    }

    /**
     * Subscribe to a channel's video feed.
     *
     * @param channelId Channel id.
     * @throws IOException If there is an exception subscribing.
     */
    public void subscribeToChannel(@NotNull String channelId) throws IOException {
        String topicUrl = "https://www.youtube.com/xml/feeds/videos.xml?channel_id=" + channelId;

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/x-www-form-urlencoded");
        String requestBody = "hub.mode=subscribe" +
                "&hub.callback=" + SUB_CALLBACK +
                "&hub.topic=" + topicUrl +
                "&hub.secret=" + subscriberSecret;

        RequestBody body = RequestBody.create(requestBody, mediaType);
        Request request = new Request.Builder()
                .url("https://pubsubhubbub.appspot.com/subscribe")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("Unsuccessful subscription to " + channelId + ", response: " + response.code());
        }
    }

    /**
     * Get a channel's content.
     * <b>COST: 100 QUOTA TOKENS!!!!!</b>
     * </br>
     * There seems to be no way in the YT API to get videos from a channel,
     * so we are doing this. We need to ensure we have up-to-date.
     * </br>
     * The query parameters ensure we only get videos/streams,
     * from the specified channel ordered chronologically.
     * </br>
     * We only construct a video url and store if it's a video or live stream.
     *
     * @param userId Owning user id.
     * @param channelId Channel id to search.
     * @param maxResults Max results (limited by 50)
     * @param publishedAfter Optional published after tag.
     * @return A list of media from this user.
     * @throws IOException If an error occurs making the request.
     */
    @NotNull
    public List<Media> getChannelContent(int userId, String channelId, int maxResults, @Nullable Long publishedAfter)
            throws IOException {
        //https://www.googleapis.com/youtube/v3/search?key=AIzaSyCMS-PgP2coQcJe6oHhOmBpH17h7lLplE0&channelId=UCm0bEce19ywHDfCGvSHVyYQ&maxResults=3&part=snippet&order=date&type=video
        String url = createApiUrl("/search");
        url += addQuery("channelId", channelId);
        url += addQuery("maxResults", String.valueOf(maxResults));
        url += addQuery("part", "snippet");
        url += addQuery("order", "date");
        url += addQuery("type", "video");
        if (publishedAfter != null) {
            url += addQuery("publishedAfter", DateUtils.formatMillisRfc(publishedAfter));
        }

        System.out.println("url " + url);

        // https://developers.google.com/youtube/v3/docs/search/list
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build();

        List<Media> res = new LinkedList<>();
        try (Response response = client.newCall(request).execute()) {
            JsonObject root = JsonParser.parseString(response.body().string()).getAsJsonObject();

            if (root.has("error")) {
                throw new RuntimeException("error response from youtube " + root.getAsJsonObject("error"));
            }

            JsonArray items = root.getAsJsonArray("items");
            for (JsonElement videoElement : items) {
                JsonObject videoObject = videoElement.getAsJsonObject();

                String videoId = videoObject.getAsJsonObject("id")
                        .getAsJsonPrimitive("videoId").getAsString();

                JsonObject videoDetails = videoObject.getAsJsonObject("snippet");
                boolean liveStream = videoDetails.getAsJsonPrimitive("liveBroadcastContent").getAsString()
                        .equals("live");
                String publishAtString = videoDetails.getAsJsonPrimitive("publishedAt").getAsString();
                long publishedAt = DateUtils.fromRfc(publishAtString);

                res.add(new Media(userId, channelId, videoId, liveStream ? MediaType.STREAM : MediaType.VIDEO, publishedAt));
            }
        }

        return res;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubscriberSecret() {
        return subscriberSecret;
    }

    public void setSubscriberSecret(String subscriberSecret) {
        this.subscriberSecret = subscriberSecret;
    }

    private String createApiUrl(String endpoint) {
        return BASE_URL + endpoint + String.format(KEY_SUFFIX, key);
    }

    private String addQuery(String key, String value) {
        return "&" + key + "=" + value;
    }


    /* Example response.
    {
    "kind": "youtube#searchListResponse",
    "etag": "Ra347q2DkOjNBqhh8KweF1MXGAw",
    "nextPageToken": "CAMQAA",
    "regionCode": "GB",
    "pageInfo": {
        "totalResults": 241,
        "resultsPerPage": 3
    },
    "items": [
        {
            "kind": "youtube#searchResult",
            "etag": "QBHR394veFCqAzSqzlAhgHG4sTU",
            "id": {
                "kind": "youtube#video",
                "videoId": "PyC_463Wuq8"
            },
            "snippet": {
                "publishedAt": "2023-05-11T23:00:11Z",
                "channelId": "UC_8j-wKLOUr3L9QNWKsCzkw",
                "title": "Post-Apocalyptic Adventures: Fallout 4 | Part 1",
                "description": "Welcome to the post-apocalyptic world of Fallout 4, where danger and adventure lurk around every corner. In this live gameplay ...",
                "thumbnails": {
                    "default": {
                        "url": "https://i.ytimg.com/vi/PyC_463Wuq8/default.jpg",
                        "width": 120,
                        "height": 90
                    },
                    "medium": {
                        "url": "https://i.ytimg.com/vi/PyC_463Wuq8/mqdefault.jpg",
                        "width": 320,
                        "height": 180
                    },
                    "high": {
                        "url": "https://i.ytimg.com/vi/PyC_463Wuq8/hqdefault.jpg",
                        "width": 480,
                        "height": 360
                    }
                },
                "channelTitle": " All Stream Game",
                "liveBroadcastContent": "none",
                "publishTime": "2023-05-11T23:00:11Z"
            }
        },
        {
        "kind": "youtube#searchListResponse",
        "etag": "gcMluFIA0eQuwYHg811mfX06N40",
        "nextPageToken": "CAMQAA",
        "regionCode": "GB",
        "pageInfo": {
            "totalResults": 246,
            "resultsPerPage": 3
        },
        "items": [
            {
                "kind": "youtube#searchResult",
                "etag": "oSYTveagtYUswWtJcJSoVSja6uQ",
                "id": {
                    "kind": "youtube#video",
                    "videoId": "EtHsWfIZ8ck"
                },
                "snippet": {
                    "publishedAt": "2023-02-21T16:26:58Z",
                    "channelId": "UC_8j-wKLOUr3L9QNWKsCzkw",
                    "title": "🔴 LIVE 24/7 from Team Fortress 2 Games | Masterclass Action",
                    "description": "GET VBucks (Fortnite): ▻ https://u.to/bR2iHw In this epic video game clip, get ready to jump into the fast-paced world of Team ...",
                    "thumbnails": {
                        "default": {
                            "url": "https://i.ytimg.com/vi/EtHsWfIZ8ck/default_live.jpg",
                            "width": 120,
                            "height": 90
                        },
                        "medium": {
                            "url": "https://i.ytimg.com/vi/EtHsWfIZ8ck/mqdefault_live.jpg",
                            "width": 320,
                            "height": 180
                        },
                        "high": {
                            "url": "https://i.ytimg.com/vi/EtHsWfIZ8ck/hqdefault_live.jpg",
                            "width": 480,
                            "height": 360
                        }
                    },
                    "channelTitle": " All Stream Game",
                    "liveBroadcastContent": "live",
                    "publishTime": "2023-02-21T16:26:58Z"
                }
            },
     */

}
