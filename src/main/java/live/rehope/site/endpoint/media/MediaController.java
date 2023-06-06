package live.rehope.site.endpoint.media;

import io.avaje.http.api.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.media.model.VideoPublishEvent;
import live.rehope.site.endpoint.user.principle.model.UserRole;
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

    @Post("/feature/")
    @EndpointRoles(UserRole.ADMIN)
    public void addFeaturedMedia(Media media) {
        service.addFeaturedMedia(media);
    }

    @Delete("/feature/url/{url}")
    @EndpointRoles(UserRole.ADMIN)
    public void deleteFeaturedMedia(String url) {
        service.removeFeaturedMedia(url);
    }

    @Delete("/feature/user/{userId}")
    @EndpointRoles(UserRole.ADMIN)
    public void deleteFeaturedMedia(int userId) {
        service.removeFeaturedMediaByUserid(userId);
    }

    @Get("/streams")
    public Object getStreams(@QueryParam("userId") Integer userId) {
        if (userId != null) {
            return service.getLiveStreamOf(userId)
                    .orElseThrow(() -> new NotFoundResponse(userId + " has no streams"));
        }

        return service.getLiveStreams();
    }

    @Get("/videos")
    public Object getVideos(@QueryParam("userId") Integer userId) {
        if (userId != null) {
            return service.getVideosOf(userId);
        }

        return service.getVideos();
    }

    @Get("/refresh")
    @EndpointRoles(UserRole.ADMIN)
    public void refresh(@QueryParam("userId") Integer userId) {
        if (userId == null) {
            throw new BadRequestResponse("no support for global refresh");
        }

        // manually refresh their field
        service.refreshMediaOf(userId);
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
