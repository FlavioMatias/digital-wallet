package api.digital_wallet.modules.finance.application.service;

import api.digital_wallet.modules.finance.application.usecase.TransferUseCase;
import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.modules.finance.infra.repository.WalletRepository;
import api.digital_wallet.shared.value.Money;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TransferAppService implements TransferUseCase {

    private final WalletRepository walletRepository;

    @Transactional
    public Transaction execute(UUID from, Money amount, UUID to){
        Wallet originWallet;
        Wallet destinationWallet;

        originWallet = walletRepository.findByOwnerIdWithLock(from).orElseThrow(
                () -> new FinanceDomainException("Wallet of user not found", FinanceErrorCode.WALLET_NOT_FOUND)
        );
        destinationWallet = walletRepository.findByOwnerIdWithLock(to).orElseThrow(
                () -> new FinanceDomainException("Wallet destination not found", FinanceErrorCode.WALLET_NOT_FOUND)
        );

        originWallet.reserveBalance(amount);

        return new Transaction();
    };

}
