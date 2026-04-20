package api.digital_wallet.modules.finance.infra.repository;

import api.digital_wallet.modules.finance.domain.port.repository.WalletRepositoryPort;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID>, WalletRepositoryPort {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.ownerId = :ownerId")
    Optional<Wallet> findByOwnerIdWithLock(UUID ownerId);


}