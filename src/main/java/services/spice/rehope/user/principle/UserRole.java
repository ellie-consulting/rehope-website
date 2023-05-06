package services.spice.rehope.user.principle;

import io.javalin.security.RouteRole;

/**
 * Represents a list of roles on the site.
 */
public enum UserRole implements RouteRole {
    USER, CREATOR, ADMIN;

    public boolean isStaff() {
        return this == ADMIN;
    }
}
