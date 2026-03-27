package api.digital_wallet.modules.finance.infra.repository;

import api.digital_wallet.modules.finance.domain.port.repository.TransactionRepositoryPort;
import api.digital_wallet.modules.finance.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>, TransactionRepositoryPort {
}
