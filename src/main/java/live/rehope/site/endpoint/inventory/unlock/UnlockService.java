package live.rehope.site.endpoint.inventory.unlock;

import live.rehope.site.endpoint.inventory.user.UserInventoryService;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Handles code redemption and unlock code related things.
 */
@Singleton
public class UnlockService {
    private final UnlockCodeRepository codeRepository;

    private final UserInventoryService userInventoryService;

    @Inject
    public UnlockService(UnlockCodeRepository codeRepository, UserInventoryService inventoryService) {
        this.codeRepository = codeRepository;
        this.userInventoryService = inventoryService;
    }

    public List<UnlockCode> getAllCodes() {
        return codeRepository.getAll();
    }

    public Optional<UnlockCode> getCodeById(int id) {
        return codeRepository.getById(id);
    }

    public UnlockCode createCode(@NotNull UnlockCode code) {
        int id = codeRepository.insertCode(code);
        return codeRepository.getById(id).orElseThrow(InternalServerErrorResponse::new);
    }

    public void deleteCode(int id) {
        codeRepository.deleteCode(id);
    }

    public void setCodeState(int codeId, boolean newState) {
        codeRepository.setCodeActive(codeId, newState);
    }

    /**
     * Redeem a code for a user.
     * </br>
     * If they have already used the code, return false.
     * Upon redemption, it will add the item to their inventory.
     *
     * @param code Code to redeem.
     * @param userId User id.
     */
    public void redeemCode(@NotNull String code, int userId) {
        // first: check if this user has not used the code already.
        if (userInventoryService.hasUnlockedElementWithCode(userId, code)) {
            throw new NotFoundResponse("code contents already unlocked");
        }

        // second: try to redeem the code
        UnlockCode usedCode = codeRepository.tryRedeem(code);

        userInventoryService.addToInventoryFromCode(userId, usedCode.unlockElementId(), usedCode.code());
    }

}
