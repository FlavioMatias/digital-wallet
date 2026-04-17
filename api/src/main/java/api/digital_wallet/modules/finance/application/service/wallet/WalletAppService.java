package api.digital_wallet.modules.finance.application.service.wallet;

import api.digital_wallet.modules.finance.application.cmd.WalletCreateCmd;
import api.digital_wallet.modules.finance.application.usecase.WalletUserCase;
import api.digital_wallet.modules.finance.domain.port.repository.WalletRepositoryPort;
import api.digital_wallet.modules.finance.domain.wallet.Wallet;
import api.digital_wallet.modules.finance.domain.wallet.WalletFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}