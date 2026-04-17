package api.digital_wallet.modules.finance.application.cmd;

import api.digital_wallet.modules.finance.domain.enums.CurrencyType;

import java.util.UUID;

public record WalletCreateCmd(
        CurrencyType currency,
        UUID ownerId
) { }
