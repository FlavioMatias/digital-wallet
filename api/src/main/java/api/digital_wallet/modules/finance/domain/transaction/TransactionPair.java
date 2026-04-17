package api.digital_wallet.modules.finance.domain.transaction;

import api.digital_wallet.modules.finance.domain.transaction.enums.TransactionStatus;

import java.util.ArrayList;
import java.util.List;

public record TransactionPair(
        Transaction debit,  // quem envia
        Transaction credit  // quem recebe
) {
    public TransactionPair {
        if (debit == null || credit == null) {
            throw new IllegalArgumentException("Debit and Credit transactions cannot be null");
        }
    }

    public void fail(){
        debit.fail();
        credit.fail();
    }
    public void authorize(){
        debit.authorize();
        credit.authorize();
    }
    public void complet(){
        debit.complete();
        credit.complete();
    }
    public Boolean isAuthorized(){
        return debit.getStatus().equals(TransactionStatus.AUTHORIZED)
                && credit.getStatus().equals(TransactionStatus.AUTHORIZED);
    }

    public List<Transaction> toList(){
        List<Transaction> list = new  ArrayList<Transaction>();
        list.add(debit);
        list.add(credit);
        return list;
    }
}