package services.spice.rehope.model;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.Nullable;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.util.ContextUtils;

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
        return optionalUserRole(context).orElseThrow(() -> new UnauthorizedResponse("Invalid session"));
    }

    protected final void assertSelfOrStaff(Context context, int queryId) {
        int userId = userId(context);
        UserRole userRole = userRole(context);

        if (userId != queryId && !userRole.isStaff()) {
            throw new UnauthorizedResponse("You can only perform this action on your own account.");
        }
    }

    protected final void unauthorized(@Nullable String error) {
        throw new UnauthorizedResponse(error != null ? error : "");
    }

}
