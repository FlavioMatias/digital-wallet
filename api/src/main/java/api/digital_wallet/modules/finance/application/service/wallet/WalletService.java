package api.digital_wallet.modules.finance.application.service.wallet;

import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.modules.finance.infra.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public Wallet getByOwnerId(UUID ownerId){
        return walletRepository.findByOwnerIdWithLock(ownerId).orElseThrow(
                () -> new FinanceDomainException("Wallet not found", FinanceErrorCode.WALLET_NOT_FOUND)
        );
    }

    public Wallet save(Wallet wallet){
        return walletRepository.save(wallet);
    }
}
