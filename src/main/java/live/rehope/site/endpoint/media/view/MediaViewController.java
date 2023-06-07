package live.rehope.site.endpoint.media.view;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Post;
import io.javalin.http.Context;

@Controller("/api/media/watching")
public class MediaViewController {

    @Post("/{videoId}")
    public void startWatching(Context context, String videoId) {

    }

    @Delete("/{videoId}")
    public void stopWatching(Context context, String videoId) {

    }

}
