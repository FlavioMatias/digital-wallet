package api.digital_wallet.modules.identity.infra.controller;

import api.digital_wallet.modules.identity.application.auth.AuthService;
import api.digital_wallet.modules.identity.application.register.RegisterService;
import api.digital_wallet.modules.identity.infra.dto.request.LoginDTO;
import api.digital_wallet.modules.identity.infra.dto.request.RegisterDTO;
import api.digital_wallet.modules.identity.infra.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints para login, registro e renovação de tokens")
public class AuthController {

    private final AuthService authService;
    private final RegisterService registerService;

    public AuthController(AuthService authService, RegisterService registerService) {
        this.authService = authService;
        this.registerService = registerService;
    }

    @PostMapping("/register")
    @Operation(summary = "Cria um novo usuário e dispara criação de carteira via evento")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        registerService.register(
                data.email(), data.password(),
                data.firstName(), data.lastName(),
                data.document(), data.profileType()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza o login e retorna os tokens de acesso e refresh")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginDTO data) {
        var response = authService.login(data.email(), data.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova o Access Token utilizando um Refresh Token válido")
    public ResponseEntity<TokenResponse> refresh(@RequestParam String refreshToken) {
        var response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }
}