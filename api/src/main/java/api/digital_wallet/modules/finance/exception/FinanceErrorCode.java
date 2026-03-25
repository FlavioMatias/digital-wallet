package api.digital_wallet.modules.finance.exception;

import lombok.Getter;

@Getter
public enum FinanceErrorCode {
    DOMAIN_ERROR("FIN-000"),
    INSUFFICIENT_FUNDS("FIN-001"),
    WALLET_BLOCKED("FIN-002"),
    CURRENCY_MISMATCH("FIN-003"),
    INVALID_STATE_TRANSITION("FIN-004"),
    WALLET_NOT_FOUND("FIN-005"),
    INTEGRATION_TIMEOUT("FIN-504");

    private final String value;

    FinanceErrorCode(String value) {
        this.value = value;
    }

}