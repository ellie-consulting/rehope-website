package services.spice.rehope.endpoint.user.principle;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Service for main information of users.
 */
@Singleton
public class PrincipleUserService {

    private final PrincipleUserRepository principleUserRepository;

    @Inject
    public PrincipleUserService(PrincipleUserRepository principleUserRepository) {
        this.principleUserRepository = principleUserRepository;
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
