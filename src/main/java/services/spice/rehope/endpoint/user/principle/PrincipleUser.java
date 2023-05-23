package services.spice.rehope.endpoint.user.principle;

import org.jetbrains.annotations.Nullable;
import services.spice.rehope.endpoint.user.auth.AuthProviderSource;

import java.sql.Timestamp;

/**
 * Represents a user signed up to the service with their primitive data.
 */
public class PrincipleUser {

    private int id;
    private String username;
    private String email;
    private UserRole role;
    private AuthProviderSource authProviderSource;
    private String providerId; // twitter id / google id / discord id. - maybe a field for all for socials
    private Timestamp accountCreated;
    private Timestamp lastLogin;

    public PrincipleUser(int id, String username, String email,
                         UserRole role,
                         AuthProviderSource authProviderSource, String providerId,
                         Timestamp accountCreated, Timestamp lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.authProviderSource = authProviderSource;
        this.providerId = providerId;
        this.accountCreated = accountCreated;
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getUsername() { // will be set to random at user conception
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public Timestamp getAccountCreated() {
        return accountCreated;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public AuthProviderSource getAuthProvider() {
        return authProviderSource;
    }

    public String getProviderId() {
        return providerId;
    }

}
