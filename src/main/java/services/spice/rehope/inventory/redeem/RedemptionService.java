package services.spice.rehope.inventory.redeem;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RedemptionService {
    private CodeRepository codeRepository;

    @Inject
    public RedemptionService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }


    public boolean redeemCode(@NotNull String code, int userId) {

    }

}
