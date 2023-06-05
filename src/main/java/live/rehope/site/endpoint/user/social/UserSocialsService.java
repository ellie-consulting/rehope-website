package live.rehope.site.endpoint.user.social;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Handles socials for users.
 */
@Singleton
public class UserSocialsService {

    private final UserSocialsRepository repository;

    @Inject
    public UserSocialsService(UserSocialsRepository repository) {
        this.repository = repository;
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
        return repository.getUserSocial(userId, type);
    }

    /**
     * Insert a new social media for a user.
     *
     * @param socialMedia Social media to insert.
     */
    public void addUserSocial(@NotNull UserSocial socialMedia) {
        repository.addUserSocial(socialMedia);
    }

    /**
     * Delete a particular social media of a user.
     *
     * @param userId User to delete.
     * @param type Type to delete.
     */
    public void deleteUserSocial(int userId, @NotNull UserSocialPlatform type) {
        repository.deleteUserSocial(userId, type);
    }

}
