package services.spice.rehope.user.principle;

import services.spice.rehope.user.auth.AuthProvider;

import java.sql.Time;

/**
 * Represents a user signed up to the service with their primitive data.
 */
public class PrincipleUser {

    private int id;
    private String username;
    private String email;
    private AuthProvider authProvider;
    private String providerId; // twitter id / google id / discord id. - maybe a field for all for socials
    private Time accountCreated;
    private Time lastLogin;

    public PrincipleUser(int id, String username, String email,
                         AuthProvider authProvider, String providerId,
                         Time accountCreated, Time lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authProvider = authProvider;
        this.providerId = providerId;
        this.accountCreated = accountCreated;
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public String getUsername() { // will be set to random at user conception
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Time getAccountCreated() {
        return accountCreated;
    }

    public Time getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Time lastLogin) {
        this.lastLogin = lastLogin;
    }

    public AuthProvider getAuthProvider() {
        return authProvider;
    }

    public String getProviderId() {
        return providerId;
    }

}
