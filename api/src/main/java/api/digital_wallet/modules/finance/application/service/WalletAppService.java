package api.digital_wallet.modules.finance.application.service;

import api.digital_wallet.modules.finance.application.cmd.WalletCreateCmd;
import api.digital_wallet.modules.finance.application.usecase.WalletUserCase;
import api.digital_wallet.modules.finance.domain.port.repository.WalletRepositoryPort;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.domain.wallet.WalletFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletAppService implements WalletUserCase {

    private final WalletFactory walletFactory;
    private final WalletRepositoryPort walletRepository;

    @Override
    @Transactional
    public Void create(WalletCreateCmd cmd) {
        Wallet wallet = walletFactory.create(cmd.ownerId(), cmd.currency());

        walletRepository.save(wallet);

        return null;
    }

    @Override
    public List<Wallet> findAllByOwnerId(UUID ownerId) {
        return walletRepository.findAllByOwnerId(ownerId);
    }
}