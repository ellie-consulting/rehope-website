package services.spice.rehope.user;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.user.principle.PrincipleUser;
import services.spice.rehope.user.principle.PrincipleUserRepository;
import services.spice.rehope.user.social.UserSocialMedia;
import services.spice.rehope.user.social.UserSocialsRepository;

import java.util.Optional;

/**
 * Interface to access users.
 */
@Singleton
public class UserService {

    private final PrincipleUserRepository principleUserRepository;
    private final UserSocialsRepository userSocialsRepository;

    @Inject
    public UserService(PrincipleUserRepository principleUserRepository, UserSocialsRepository userSocialsRepository) {
        this.principleUserRepository = principleUserRepository;
        this.userSocialsRepository = userSocialsRepository;
    }

    public Optional<PrincipleUser> getUserByEmail(@NotNull String email) {
        return principleUserRepository.getUserByEmail(email);
    }

    public Optional<PrincipleUser> getUserByUsername(@NotNull String username) {
        return principleUserRepository.getUserByEmail(username);
    }

    public Optional<PrincipleUser> getUserByProviderId(@NotNull String providerId) {
        return principleUserRepository.getUserByProviderId(providerId);
    }

}
