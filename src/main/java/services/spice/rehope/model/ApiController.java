package services.spice.rehope.model;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.Nullable;
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

    protected final boolean unauthorized(Context context, @Nullable String error) {
        context.status(HttpStatus.UNAUTHORIZED);
        if (error != null) {
            context.result(error);
        }

        return false;
    }

}
