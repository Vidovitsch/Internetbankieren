package Models;

import Shared_Centrale.ICentrale;
import Shared_Centrale.ITransactie;
import Shared_Centrale.IBankTrans;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Centrale extends UnicastRemoteObject implements ICentrale {
    private ArrayList<ITransactie> transactions;
    
    public Centrale() throws RemoteException {
        transactions = new ArrayList();
    }

    @Override
    public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws RemoteException {
        bank.removeSaldo(IBAN1, value);
        bank.addSaldo(IBAN2, value);
        
        //Add a new transaction to the centrale
        Transactie transactie = new Transactie(IBAN1, IBAN2, getCurrentDateTime(), value);
        if (!description.isEmpty()) transactie.setDescription(description);
        transactions.add(transactie);
    }

    @Override
    public ArrayList<ITransactie> getTransactions(String IBAN) throws RemoteException {
        ArrayList<ITransactie> transList = new ArrayList();
        for (ITransactie trans : transactions) {
            if (trans.getIBANTo().equals(IBAN) || trans.getIBANFrom().equals(IBAN)) {
                transList.add(trans);
            }
        }
        return transList;
    }

    private String getCurrentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime.toString());
        return dateTime.toString();
    }
}
