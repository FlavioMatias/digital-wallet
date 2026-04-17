package api.digital_wallet.modules.finance.domain.port.service;

import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;

public interface TransferServicePort {
    void execute(Wallet origin, Wallet destination, TransactionPair transactions);
}
