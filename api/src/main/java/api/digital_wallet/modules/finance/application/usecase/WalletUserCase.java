package api.digital_wallet.modules.finance.application.usecase;

import api.digital_wallet.modules.finance.application.cmd.WalletCreateCmd;

public interface WalletUserCase {
    Void create(WalletCreateCmd cmd);
}
