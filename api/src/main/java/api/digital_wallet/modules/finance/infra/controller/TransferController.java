package api.digital_wallet.modules.finance.infra.controller;

import api.digital_wallet.modules.finance.application.usecase.TransferUseCase;
import api.digital_wallet.modules.finance.infra.dto.request.TransferRequest;
import api.digital_wallet.modules.finance.infra.dto.response.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transfer")
@RequiredArgsConstructor
@Tag(name = "Transfers", description = "Endpoints para movimentações financeiras")
public class TransferController {

    private final TransferUseCase transferUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Executa uma transferência entre carteiras")
    public TransferResponse execute(@RequestBody @Valid TransferRequest request) {

        var command = request.toCommand();
        var transaction = transferUseCase.execute(command);

        return TransferResponse.from(transaction);
    }
}