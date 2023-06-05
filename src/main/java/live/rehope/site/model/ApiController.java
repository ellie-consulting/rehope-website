package live.rehope.site.model;

import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.Nullable;
import live.rehope.site.endpoint.user.principle.UserRole;
import live.rehope.site.util.ContextUtils;

import java.util.Optional;

public abstract class ApiController {

    protected final Optional<Integer> optionalUserId(Context context) {
        return ContextUtils.userId(context);
    }

    protected final int userId(Context context) {
        return optionalUserId(context).orElseThrow(() -> new UnauthorizedResponse("Invalid session"));
    }

    protected final Optional<UserRole> optionalUserRole(Context context) {
        return ContextUtils.role(context);
    }

    protected final UserRole userRole(Context context) {
        return optionalUserRole(context).orElse(UserRole.USER);
    }

    protected final void assertSelfOrStaff(Context context, int queryId) {
        int userId = userId(context);
        UserRole userRole = userRole(context);

        if (userId != queryId && !userRole.isStaff()) {
            throw new UnauthorizedResponse("You can only perform this action on your own account.");
        }
    }

    protected final boolean canViewSensitiveData(Context context, int queryId) {
        int userId = userId(context);
        UserRole userRole = userRole(context);

        return userId == queryId || userRole.isStaff();
    }

    protected final void unauthorized(@Nullable String error) {
        throw new UnauthorizedResponse(error != null ? error : "");
    }

}
