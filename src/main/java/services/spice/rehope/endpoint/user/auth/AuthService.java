package services.spice.rehope.endpoint.user.auth;

import io.javalin.http.Context;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.javalin.CallbackHandler;
import org.pac4j.javalin.JavalinWebContext;
import org.pac4j.javalin.SecurityHandler;
import org.pac4j.jee.context.session.JEESessionStore;
import org.pac4j.oauth.client.Google2Client;
import services.spice.rehope.endpoint.user.principle.UserRole;
import services.spice.rehope.endpoint.user.UserService;
import services.spice.rehope.endpoint.user.principle.PrincipleUser;
import services.spice.rehope.util.ContextUtils;
import services.spice.rehope.util.RandomNameGenerator;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Optional;

@Singleton
public class AuthService {

    private final UserService userService;

    private final Config googleConfig;
    private final CallbackHandler googleCallbackHandler;

    @Inject
    public AuthService(UserService userService, Google2Client google2Client) {
        this.userService = userService;

        this.googleConfig = new Config(google2Client);
        this.googleCallbackHandler = new CallbackHandler(googleConfig, "/", true);
    }

    // todo moduar?
    public void loginGoogle(Context context) {
        new SecurityHandler(googleConfig, "Google2Client").handle(context);
        // not really much else to do here - wait until the call back.
    }

    public void callbackGoogle(Context context) {
        googleCallbackHandler.handle(context);

        ProfileManager manager = new ProfileManager(new JavalinWebContext(context), JEESessionStore.INSTANCE);
        Optional<CommonProfile> optProfile = manager.getProfile(CommonProfile.class);

        if (optProfile.isEmpty()) {
            System.out.println("oop");
            return;
        }

        CommonProfile profile = optProfile.get();
        String email = profile.getEmail();

        PrincipleUser user = userService.getUserByEmail(email).orElse(null);
        if (user != null) {
            // TODO login
        } else {
            user = createUserFromProfile(profile, AuthProvider.GOOGLE);
            userService.createUser(user);

            // todo then redirect to select username
        }

        ContextUtils.setupSessionAttributes(user, context);
    }

    private PrincipleUser createUserFromProfile(CommonProfile profile, AuthProvider authProvider) {
        return new PrincipleUser(0, RandomNameGenerator.getRandomName(), profile.getEmail(), UserRole.USER,
                authProvider, profile.getId(), Time.valueOf(LocalTime.now()), Time.valueOf(LocalTime.now()));
    }

}
