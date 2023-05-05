package services.spice.rehope.inventory.redeem;

/**
 * Represents a code which can be redeemed
 * and unlocked for an inventory element.
 *
 * @param id Code id.
 * @param code String code that user types in.
 * @param redeemLimit Limit for this code to be redeemed, -1 for unlimited.
 * @param uses How many times this code has been used.
 * @param active If this code can be used.
 * @param unlockElementId The element this will unlock upon usage.
 */
public record Code(int id, String code, int redeemLimit, int uses, boolean active,
                   int unlockElementId) { // TODO allow multiple

    /**
     * @return If this code has a limit to how many times it can be redeemed.
     */
    public boolean hasRedeemLimit() {
        return redeemLimit > 0;
    }

    /**
     * @return If this code can be further redeemed.
     */
    public boolean canBeRedeemed() {
        return active && (!hasRedeemLimit() || redeemLimit < uses);
    }

}
