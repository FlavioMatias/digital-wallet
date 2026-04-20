package api.digital_wallet.modules.finance.domain.service;

import api.digital_wallet.modules.finance.domain.port.service.TransferServicePort;
import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import org.springframework.stereotype.Service;

@Service
public class TransferService implements TransferServicePort {

    @Override
    public void execute(Wallet origin, Wallet destination, TransactionPair transactions) {
        if (transactions.isAuthorized()) {
            origin.reserveBalance(transactions.debit().getAmount());

            origin.confirmPaymentFromBlocked(
                    transactions.debit().getAmount()
            );

            destination.deposit(
                    transactions.credit().getAmount()
            );

            transactions.complet();

            return;
        }

        throw new FinanceDomainException("It is not possible to complete transfers with unauthorized transactions.",
                FinanceErrorCode.UNAUTHORIZED_TRANSFER);
    }
}
