package services.spice.rehope.endpoint.inventory.unlock;

import io.javalin.http.NotFoundResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.endpoint.inventory.user.UserInventoryService;

import java.util.List;

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

    public void createCode(@NotNull UnlockCode code) {
        codeRepository.insertCode(code);
    }

    public void deleteCode(@NotNull String code) {
        codeRepository.deleteCode(code);
    }

    public void setCodeState(@NotNull String code, boolean newState) {
        codeRepository.setCodeActive(code, newState);
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
