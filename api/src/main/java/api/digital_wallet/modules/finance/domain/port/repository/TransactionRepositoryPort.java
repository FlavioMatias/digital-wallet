package api.digital_wallet.modules.finance.domain.port.repository;

import api.digital_wallet.modules.finance.domain.transaction.Transaction;

import java.util.List;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    void saveAll(List<Transaction> transactions);
}
