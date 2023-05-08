package services.spice.rehope.endpoint.user.principle;

import io.javalin.security.RouteRole;

/**
 * Represents a list of roles on the site.
 */
public enum UserRole implements RouteRole {
    USER, CREATOR, ADMIN;

    public boolean isStaff() {
        return this == ADMIN;
    }

    public boolean isEqualOrGreaterThan(UserRole role) {
        return this.ordinal() >= role.ordinal();
    }
}
