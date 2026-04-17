package api.digital_wallet.modules.finance.application.usecase;

import api.digital_wallet.modules.finance.application.cmd.TransferCmd;
import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import api.digital_wallet.shared.value.Money;

import java.util.UUID;

public interface TransferUseCase {
    Transaction execute(TransferCmd cmd);
}