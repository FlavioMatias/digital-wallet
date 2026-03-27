package api.digital_wallet.modules.finance.infra.dto.request;

import api.digital_wallet.modules.finance.application.cmd.TransferCmd;
import api.digital_wallet.shared.value.Money;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @NotNull UUID originId,
        @NotNull UUID destinationId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency
) {
    public TransferCmd toCommand() {
        return new TransferCmd(
                originId,
                new Money(amount),
                destinationId
        );
    }
}