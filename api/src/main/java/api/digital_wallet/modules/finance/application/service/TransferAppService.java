package api.digital_wallet.modules.finance.application.service;

import api.digital_wallet.modules.finance.application.cmd.TransferCmd;

import api.digital_wallet.modules.finance.application.usecase.TransferUseCase;
import api.digital_wallet.modules.finance.domain.port.repository.TransactionRepositoryPort;
import api.digital_wallet.modules.finance.domain.port.repository.WalletRepositoryPort;
import api.digital_wallet.modules.finance.domain.port.service.ExchangeServicePort;
import api.digital_wallet.modules.finance.domain.port.service.TransferServicePort;
import api.digital_wallet.modules.finance.domain.port.transfer.TransferAuthorization;
import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import api.digital_wallet.modules.finance.domain.transaction.TransactionBuilder;
import api.digital_wallet.modules.finance.domain.transaction.TransactionPair;
import api.digital_wallet.modules.finance.domain.transaction.event.TransactionCreatedEvent;
import api.digital_wallet.modules.finance.exception.FinanceDomainException;
import api.digital_wallet.modules.finance.exception.FinanceErrorCode;
import api.digital_wallet.shared.outbox.OutboxServicePort;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransferAppService implements TransferUseCase {

    private final WalletRepositoryPort walletRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final ExchangeServicePort exchangeService;
    private final OutboxServicePort outboxService;
    private final TransferAuthorization transferAuthorization;
    private final TransferServicePort transferService;


    @Transactional
    public Transaction execute(TransferCmd cmd){

        var builder = new TransactionBuilder();

        var origin = walletRepository.findByOwnerIdWithLock(cmd.from()).orElseThrow(
                    () -> new FinanceDomainException("Wallet not found", FinanceErrorCode.WALLET_NOT_FOUND)
        );
        var destination = walletRepository.findByOwnerId(cmd.to()).orElseThrow(
                () -> new FinanceDomainException("Wallet destination not found", FinanceErrorCode.WALLET_NOT_FOUND)
        );

        var convertedAmount = exchangeService.convert(
                cmd.amount(),
                origin.getCurrency(),
                destination.getCurrency()
        );

        var transactions = builder
                .convertedAmount(convertedAmount)
                .from(origin)
                .to(destination)
                .amount(cmd.amount())
                .build();

        authorize(transactions);

        transferService.execute(origin, destination, transactions);

        walletRepository.save(origin);
        walletRepository.save(destination);
        transactionRepository.saveAll(transactions.toList());

        sendEvent(transactions);
        return transactions.debit();

    };

    private void authorize(TransactionPair transactions){
        if (!transferAuthorization.authorize()) {
            transactions.fail();
            transactionRepository.saveAll(transactions.toList());
            throw new FinanceDomainException("unauthorized transfer", FinanceErrorCode.UNAUTHORIZED_TRANSFER);
        }
        transactions.authorize();
    }

    private void sendEvent(TransactionPair transactions) {
        var event = TransactionCreatedEvent.from(transactions);
        outboxService.saveEvent(event, "Finance");

    }

}
