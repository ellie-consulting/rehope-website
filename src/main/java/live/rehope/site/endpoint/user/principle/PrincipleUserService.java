package live.rehope.site.endpoint.user.principle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import live.rehope.site.endpoint.user.principle.model.PrincipleUser;
import live.rehope.site.endpoint.user.principle.model.UserRole;
import org.jetbrains.annotations.NotNull;
import live.rehope.site.endpoint.user.principle.exception.InvalidUsernameException;
import live.rehope.site.endpoint.user.social.UserSocialsService;

import java.util.List;
import java.util.Optional;

/**
 * Service for main information of users.
 */
@Singleton
public class PrincipleUserService {
    private static final int MAX_USERNAME_LENGTH = 16;

    private final PrincipleUserRepository principleUserRepository;
    private final UserSocialsService socialsService;

    @Inject
    public PrincipleUserService(PrincipleUserRepository principleUserRepository, UserSocialsService socialsService) {
        this.principleUserRepository = principleUserRepository;
        this.socialsService = socialsService;
    }

    @NotNull
    public List<PrincipleUser> getUsers() {
        return principleUserRepository.getAll();
    }

    public PrincipleUser createUser(@NotNull PrincipleUser user) {
        return principleUserRepository.createUser(user);

        // link up social
//        socialsService.addUserSocial(new UserSocial(0, user.getId(), ));
    }

    public void handleLogin(int userId) {
         principleUserRepository.updateLastLogin(userId);
    }

    /**
     * Update the username of a user.
     * </br>
     * This may be called by admin, or
     * as the user setting up their first username.
     *
     * @param userId User id.
     * @param newUsername Their new username.
     */
    public void updateUsername(int userId, @NotNull String newUsername) {
        newUsername = newUsername.trim();

        // todo profanity check

        if (newUsername.length() > MAX_USERNAME_LENGTH) {
            throw new InvalidUsernameException(InvalidUsernameException.Reason.TOO_LONG);
        }

        principleUserRepository.setUsername(userId, newUsername.trim());
    }

    @NotNull
    public List<PrincipleUser> getUsersByRole(@NotNull UserRole role) {
        return principleUserRepository.getUsersByRole(role);
    }

    public Optional<PrincipleUser> getUserById(int id) {
        return principleUserRepository.getById(id);
    }

    public Optional<PrincipleUser> getUserByEmail(@NotNull String email) {
        return principleUserRepository.getUserByEmail(email);
    }

    public Optional<PrincipleUser> getUserByUsername(@NotNull String username) {
        return principleUserRepository.getUserByUsername(username);
    }

    public Optional<PrincipleUser> getUserByProviderId(@NotNull String providerId) {
        return principleUserRepository.getUserByProviderId(providerId);
    }

}
