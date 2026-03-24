package api.digital_wallet.modules.finance.exception;

import api.digital_wallet.shared.exceptions.BusinessException;

public class FinanceDomainException extends BusinessException {

    public FinanceDomainException(String message, FinanceErrorCode code) {
        super(message, code.getValue(), null);
    }
    public FinanceDomainException(String message) {
        super(message, FinanceErrorCode.DOMAIN_ERROR.getValue(), null);
    }
}