package api.digital_wallet.modules.finance.domain.port.transfer.response;

public record AuthorizationResponse(String status, AuthorizationData data) {}