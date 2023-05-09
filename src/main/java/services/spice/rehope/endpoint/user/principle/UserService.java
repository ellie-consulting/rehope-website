package services.spice.rehope.endpoint.user.principle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.endpoint.user.social.UserSocialsRepository;

import java.util.List;
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

    @NotNull
    public List<PrincipleUser> getUsers() {
        return principleUserRepository.getAll();
    }

    public boolean createUser(PrincipleUser user) {
        return principleUserRepository.createUser(user);
    }

    public boolean handleLogin(int userId) {
        return principleUserRepository.updateLastLogin(userId);
    }

    public boolean updateUsername(int userId, String newUsername) {
        return principleUserRepository.setUsername(userId, newUsername);
    }

    @NotNull
    public List<PrincipleUser> getUsersByRole(@NotNull UserRole role) {
        return principleUserRepository.getUsersByRole(role);
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
