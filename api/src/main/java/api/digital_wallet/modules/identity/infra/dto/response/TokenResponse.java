package api.digital_wallet.modules.identity.infra.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}