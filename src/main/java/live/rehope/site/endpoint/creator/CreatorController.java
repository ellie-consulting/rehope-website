package live.rehope.site.endpoint.creator;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.javalin.http.NotFoundResponse;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.creator.model.MediaCreator;
import live.rehope.site.endpoint.user.principle.model.UserRole;

import java.util.List;

@Controller("/api")
@EndpointRoles(UserRole.ADMIN)
public class CreatorController {

    private final CreatorService service;

    public CreatorController(CreatorService service) {
        this.service = service;
    }

    @Get("/creators")
    public List<MediaCreator> getCreators() {
        return service.getCreators();
    }

    @Get("/creator/{userId}")
    public MediaCreator getCreator(int userId) {
        return service.getCreator(userId).orElseThrow(NotFoundResponse::new);
    }

    @Post("/creator/{userId}/{channelId}")
    public void linkCreator(int userId, String channelId) {
        service.linkChannel(userId, channelId);
    }

    @Delete("/creator/{userId}")
    public void unlink(int userId) {
        service.unlinkChannel(userId);
    }

}
