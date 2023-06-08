package live.rehope.site.endpoint.media.view;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Post;
import io.javalin.http.Context;

@Controller("/api/media/{videoId}/watching")
public class MediaViewController {

    @Post
    public void startWatching(Context context) {

    }

    @Delete
    public void stopWatching(Context context) {

    }

}
