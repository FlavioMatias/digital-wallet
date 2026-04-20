package api.digital_wallet.modules.finance.domain.port.repository;

import api.digital_wallet.modules.finance.domain.wallet.Wallet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepositoryPort {

    Optional<Wallet> findByOwnerId(UUID ownerId);

    Optional<Wallet> findByOwnerIdWithLock(UUID ownerId);

    Wallet save(Wallet wallet);

}
