package api.digital_wallet.modules.finance.application.cmd;

import api.digital_wallet.shared.value.Money;

import java.util.UUID;

public record TransferCmd(
        UUID from,
        Money amount,
        UUID to
) { }
