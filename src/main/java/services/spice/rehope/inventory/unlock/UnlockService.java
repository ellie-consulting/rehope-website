package services.spice.rehope.inventory.unlock;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import services.spice.rehope.inventory.user.UserInventoryService;

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

    /**
     * Redeem a code for a user.
     * </br>
     * If they have already used the code, return false.
     * Upon redemption, it will add the item to their inventory.
     *
     * @param code Code to redeem.
     * @param userId User id.
     * @return If the code was redeemed.
     */
    public boolean redeemCode(@NotNull String code, int userId) {
        // first: check if this user has not used the code already.
        if (userInventoryService.hasUnlockedElementWithCode(userId, code)) {
            return false;
        }

        // second: try to redeem the code
        UnlockCode usedCode = codeRepository.tryRedeem(code);
        if (usedCode == null) {
            return false;
        }

        userInventoryService.addToInventoryFromCode(userId, usedCode.unlockElementId(), usedCode.code());
        return true;
    }

}
