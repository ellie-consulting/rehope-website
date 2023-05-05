package services.spice.rehope.security;

import org.pac4j.core.authorization.authorizer.ProfileAuthorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;

public class CustomAuthorizer extends ProfileAuthorizer {

    @Override
    public boolean isAuthorized(WebContext context, SessionStore sessionStore, List<UserProfile> profiles) {
        return isAnyAuthorized(context, sessionStore, profiles);
    }

    @Override
    protected boolean isProfileAuthorized(WebContext context, SessionStore sessionStore, UserProfile profile) {
        if (profile == null || profile.getUsername() == null) {
            return false;
        }

        return profile.getUsername().toLowerCase().startsWith("jle");
    }
}