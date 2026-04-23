package api.digital_wallet.modules.finance.infra.dto.response;

import api.digital_wallet.modules.finance.domain.wallet.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponseDTO(
        UUID id,
        UUID ownerId,
        BigDecimal availableBalance,
        BigDecimal blockedBalance,
        BigDecimal totalBalance,
        String currency,
        String status
) {
    public static WalletResponseDTO fromEntity(Wallet wallet) {
        return new WalletResponseDTO(
                wallet.getId(),
                wallet.getOwnerId(),
                wallet.getAvailableBalance().getAmount(),
                wallet.getBlockedBalance().getAmount(),
                wallet.getAvailableBalance().add(wallet.getBlockedBalance()).getAmount(),
                wallet.getCurrency().name(),
                wallet.getStatus().name()
        );
    }
}
