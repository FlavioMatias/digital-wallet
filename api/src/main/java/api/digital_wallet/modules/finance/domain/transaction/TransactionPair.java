package api.digital_wallet.modules.finance.domain.transaction;

public record TransactionPair(
        Transaction debit,  // quem envia
        Transaction credit  // quem recebe
) {
    public TransactionPair {
        if (debit == null || credit == null) {
            throw new IllegalArgumentException("Debit and Credit transactions cannot be null");
        }
    }
}