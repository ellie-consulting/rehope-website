package live.rehope.site.endpoint.dev;

import io.avaje.http.api.Controller;
import io.avaje.http.api.Post;
import io.javalin.http.Context;
import live.rehope.site.endpoint.EndpointRoles;
import live.rehope.site.endpoint.user.principle.PrincipleUser;
import live.rehope.site.endpoint.user.principle.UserRole;
import live.rehope.site.util.ContextUtils;

@Controller("/api/dev")
@EndpointRoles(UserRole.ADMIN)
public class DevController {

    // TODO turn off

    @Post("/context")
    public void setContext(Context ctx, PrincipleUser user) {
        ContextUtils.setupSessionAttributes(user, ctx);
    }


}
