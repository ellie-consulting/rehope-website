package services.spice.rehope.model;

import io.javalin.http.Context;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.util.ContextUtils;

public abstract class ApiController {

    protected final Integer userId(Context context) {
        return ContextUtils.userId(context).orElse(null);
    }

    protected final UserRole userRole(Context context) {
        return ContextUtils.role(context).orElse(null);
    }

    protected final boolean assertSelfOrStaff(Context context, int queryId) {
        Integer userId = userId(context);
        UserRole userRole = userRole(context);

        return userId != null && userRole != null && (userId == queryId || userRole.isStaff());
    }

}
