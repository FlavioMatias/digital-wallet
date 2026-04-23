package api.digital_wallet.modules.finance.application.usecase;

import api.digital_wallet.modules.finance.application.cmd.WalletCreateCmd;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;

import java.util.List;
import java.util.UUID;

public interface WalletUserCase {
    Void create(WalletCreateCmd cmd);
    List<Wallet> findAllByOwnerId(UUID ownerId);

}
