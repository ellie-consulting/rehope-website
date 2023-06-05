package live.rehope.site.endpoint.user.principle;

import io.javalin.security.RouteRole;
import org.jetbrains.annotations.NotNull;

/**
 * Defines roles for users on the site
 * and also used for restricting pages.
 */
public enum UserRole implements RouteRole {
    USER, CREATOR, ADMIN;

    public boolean isStaff() {
        return this == ADMIN;
    }

    public boolean isEqualOrGreaterThan(@NotNull UserRole role) {
        return this.ordinal() >= role.ordinal();
    }
}
