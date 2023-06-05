package live.rehope.site.endpoint.media;

import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.media.model.VideoPublishEvent;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.avaje.http.api.QueryParam;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import live.rehope.site.endpoint.media.model.Media;

import java.util.List;

@Controller("/api/media")
public class MediaController {

    private final MediaService service;

    public MediaController(MediaService service) {
        this.service = service;
    }

    @Get("/featured")
    public List<Media> getFeatured() {
        return service.getFeaturedMedia();
    }

    @Get("/stream")
    public Object getStreams(@QueryParam("userId") Integer userId) {
        if (userId != null) {
            return service.getLiveStreamOf(userId).orElse(null);
        }

        return service.getLiveStreams();
    }

    @Get("/video")
    public Object getVideos(@QueryParam("userId") Integer userId) {
        if (userId != null) {
            return service.getLiveStreamOf(userId).orElse(null);
        }

        return service.getLiveStreams();
    }

    @Get("/refresh")
    @EndpointRoles(UserRole.ADMIN)
    public void refresh(@QueryParam("userId") Integer userId) {

    }

    /* Subscriber */

    @Get("/notify")
    public String subscriberChallenge(@QueryParam("hub.challenge") String challenge) {
        return challenge;
    }

    @Post("/notify")
    public void notifyEvent(Context ctx, VideoPublishEvent event) {
        String secret = ctx.header("X-Hub-Signature");
        if (secret == null) {
            throw new UnauthorizedResponse();
        }

        if (!secret.equals(service.getMediaCache().getYouTubeClient().getSubscriberSecret())) {
            throw new UnauthorizedResponse();
        }

        service.handleVideoPublishEvent(event);
    }

}
