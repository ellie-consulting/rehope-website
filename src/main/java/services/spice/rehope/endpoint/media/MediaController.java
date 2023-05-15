package services.spice.rehope.endpoint.media;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.avaje.http.api.QueryParam;
import services.spice.rehope.endpoint.EndpointRoles;
import services.spice.rehope.endpoint.media.model.Media;
import services.spice.rehope.endpoint.user.principle.UserRole;

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


}
