package services.spice.rehope.endpoint.user.auth;

import io.javalin.http.Context;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.javalin.JavalinWebContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.spice.rehope.endpoint.user.principle.PrincipleUserService;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.endpoint.user.principle.PrincipleUser;
import services.spice.rehope.util.ContextUtils;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Handles account creation and log-in/out.
 */
@Singleton
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final PrincipleUserService userService;
    private final AuthProviders authProviders;

    @Inject
    public AuthService(PrincipleUserService userService, AuthProviders authProviders) {
        this.userService = userService;
        this.authProviders = authProviders;
    }

    /**
     * Handle a login for an auth source.
     *
     * @param context Request context.
     */
    public void handleLogin(@NotNull AuthProviderSource source, @NotNull Context context) {
        LOGGER.info("handleLogin({})", source);
        authProviders.handleLogin(source, context);
    }

    /**
     * Handle a callback from an auth provider..
     * </br>
     * If no profile is found from their selection,
     * we will send them back to login page.
     * </br>
     * If the user's email exists, we will log them in,
     * otherwise we will create their profile.
     * </br>
     * Finally, we will update their session attributes.
     *
     * @param context Request context.
     */
    public void handleCallback(@NotNull AuthProviderSource source, @NotNull Context context) {
        LOGGER.info("handleCallback({})", source);
        authProviders.handleCallback(context);

        ProfileManager manager = new ProfileManager(new JavalinWebContext(context), JEESessionStore.INSTANCE);
        Optional<CommonProfile> optProfile = manager.getProfile(CommonProfile.class);

        if (optProfile.isEmpty()) {
            LOGGER.info("No profile in context, returning to login");
            handleLogin(source, context); // todo could this cause loop?
            return;
        }

        CommonProfile profile = optProfile.get();
        String email = profile.getEmail();

        PrincipleUser user = userService.getUserByEmail(email).orElse(null);
        if (user != null) {
            LOGGER.info("Logging in user {}" , user.getId());
            userService.handleLogin(user.getId());
            // todo do we need to anything else??
        } else {
            LOGGER.info("Creating new profile");

            user = createUserFromProfile(profile, AuthProviderSource.GOOGLE);
            userService.createUser(user);
        }

        if (user.getUsername() == null) {
            // todo redirect to select username
        }

        ContextUtils.setupSessionAttributes(user, context);
        LOGGER.info("{} logged in successfully", user.getId());
    }

    /**
     * Handle a local logout.
     * </br>
     * We will destroy their session,
     * and redirect to the home page.
     *
     * @param context Request context.
     */
    public void handleLogout(@NotNull Context context) {
        LOGGER.info("Logging a user out");
        authProviders.handleLogout(context);
    }

    /**
     * Convert a Pac4j session profile to our own.
     * </br>
     * This is called when the user is being created.
     *
     * @param profile Profile to login.
     * @param authProviderSource Auth provider.
     * @return Converted
     */
    private PrincipleUser createUserFromProfile(CommonProfile profile, AuthProviderSource authProviderSource) {
        return new PrincipleUser(0, null, profile.getEmail(), UserRole.USER,
                authProviderSource, profile.getId(), Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now()));
    }

}
