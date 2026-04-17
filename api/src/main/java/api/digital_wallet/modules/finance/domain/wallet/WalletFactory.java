package api.digital_wallet.modules.finance.domain.wallet;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;
import api.digital_wallet.modules.finance.domain.wallet.enums.WalletStatus;
import api.digital_wallet.shared.value.Money;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WalletFactory {

    public Wallet create(UUID ownerId, CurrencyType currency) {
        Wallet wallet = new Wallet();

        wallet.setOwnerId(ownerId);
        wallet.setCurrency(currency);

        wallet.setAvailableBalance(Money.of(100.00));
        wallet.setBlockedBalance(Money.zero());
        wallet.setStatus(WalletStatus.ACTIVE);

        return wallet;
    }
}