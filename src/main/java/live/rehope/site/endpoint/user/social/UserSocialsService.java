package live.rehope.site.endpoint.user.social;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.user.social.model.UserSocial;
import live.rehope.site.endpoint.user.social.model.UserSocialPlatform;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * Handles socials for users.
 */
@Singleton
public class UserSocialsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSocialsService.class);

    private final UserSocialsRepository repository;
    private final SocialProviders socialProviders;

    @Inject
    public UserSocialsService(UserSocialsRepository repository, SocialProviders socialProviders) {
        this.repository = repository;
        this.socialProviders = socialProviders;
    }

    /**
     * Get all social medias for a user id
     * indexed by the type.
     *
     * @param userId User id.
     * @return Their socials.
     */
    @NotNull
    public Map<UserSocialPlatform, UserSocial> getUserSocials(int userId) {
        return repository.getUserSocials(userId);
    }

    /**
     * Get the social media information of a type.
     *
     * @param userId User id.
     * @param type Type.
     * @return Their data.
     */
    public Optional<UserSocial> getUserSocial(int userId, @NotNull UserSocialPlatform type) {
        LOGGER.info("getUserSocial({}, {})", userId, type);
        return repository.getUserSocial(userId, type);
    }

    /**
     * Manually insert a new social media for a user.
     *
     * @param socialMedia Social media to insert.
     */
    public void addUserSocial(@NotNull UserSocial socialMedia) {
        LOGGER.info("addUserSocial({})", socialMedia);
        repository.addUserSocial(socialMedia);
    }

    /**
     * Connect to a social via oauth.
     * </br>
     * A user can only link a one social type once.
     *
     * @param context Context.
     * @param userId User id.
     * @param platform Platform to connect.
     * @return Connected social.
     */
    public UserSocial connectSocial(Context context, int userId, @NotNull UserSocialPlatform platform) {
        LOGGER.info("connectSocial({}, {})", userId, platform);

        if (getUserSocial(userId, platform).isPresent()) {
            LOGGER.info("social already linked!");
            throw new BadRequestResponse("Already linked " + platform);
        }

        Map<String, UserProfile> profiles = context.sessionAttribute(Pac4jConstants.USER_PROFILES);
        if (profiles == null || profiles.isEmpty()) {
            LOGGER.info("profiles empty, sending to auth");
            socialProviders.handleSocialConnect(platform, context);
            return null;
        }

        Optional<UserProfile> optProfile = ProfileHelper.flatIntoOneProfile(profiles.values());
        if (optProfile.isEmpty()) {
            LOGGER.info("user profile is empty, sending to auth");
            return null;
        }

        UserProfile profile = optProfile.get();

        UserSocial socialMedia = new UserSocial(0, userId, platform, profile.getId());
        addUserSocial(socialMedia);
        return socialMedia;
    }

    public void handleCallback(Context context) {
        LOGGER.info("handleCallback");
        socialProviders.handleSocialCallback(context);
    }

    /**
     * Delete a particular social media of a user.
     *
     * @param userId User to delete.
     * @param type Type to delete.
     */
    public void deleteUserSocial(int userId, @NotNull UserSocialPlatform type) {
        LOGGER.info("deleteUserSocial({}, {})", userId, type);
        repository.deleteUserSocial(userId, type);
    }

}
