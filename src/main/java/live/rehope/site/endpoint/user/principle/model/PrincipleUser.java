package live.rehope.site.endpoint.user.principle.model;

import live.rehope.site.endpoint.user.auth.AuthProviderSource;

import java.sql.Timestamp;

/**
 * Represents a user signed up to the service with their primitive data.
 */
public record PrincipleUser(int id, String username, String email, UserRole role,
                            AuthProviderSource authSource, String providerId,
                            Timestamp accountedCreated, Timestamp lastLogin) {

    public PrincipleUser sensitiveCopy() {
        return new PrincipleUser(id, username, null, null, null, null, accountedCreated, lastLogin);
    }

    public PrincipleUser copyWithId(int id) {
        return new PrincipleUser(id, username, email, role, authSource, providerId, accountedCreated, lastLogin);
    }
}
