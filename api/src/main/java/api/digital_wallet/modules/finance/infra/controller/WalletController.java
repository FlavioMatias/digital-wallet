package api.digital_wallet.modules.finance.infra.controller;

import api.digital_wallet.modules.finance.application.usecase.WalletUserCase;
import api.digital_wallet.modules.finance.infra.dto.response.WalletResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletUserCase walletUserCase;

    public WalletController(WalletUserCase walletUserCase) {
        this.walletUserCase = walletUserCase;
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<List<WalletResponseDTO>> listByOwner(@PathVariable UUID ownerId) {

        List<WalletResponseDTO> wallets = walletUserCase.findAllByOwnerId(ownerId)
                .stream()
                .map(WalletResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(wallets);
    }
}