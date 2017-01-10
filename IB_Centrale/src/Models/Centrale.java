package Models;

import Exceptions.LimitReachedException;
import Shared_Centrale.ICentrale;
import Shared_Centrale.IBankTrans;
import Shared_Data.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Centrale extends UnicastRemoteObject implements ICentrale {
    
    private ArrayList<Transactie> transactions;
    private IPersistencyMediator pMediator;
    
    public Centrale() throws RemoteException {
        transactions = new ArrayList();
    }

    public void setPersistencyMediator(IPersistencyMediator pMediator) throws RemoteException {
        this.pMediator = pMediator;
        setDatabaseData();
    }
    
    @Override
    public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws LimitReachedException, RemoteException {
        if (bank.removeSaldo(IBAN1, value)) {
            bank.addSaldo(IBAN2, value);
        } else {
            throw new LimitReachedException("The credit limit has been reached");
        }
        
        //Add a new transaction to the centrale
        Transactie transactie = new Transactie(IBAN1, IBAN2, getCurrentDateTime(), value);
        
        if (!description.isEmpty()) transactie.setDescription(description);
        transactions.add(transactie);
        
        //Database transfer
        pMediator.transferMoney(IBAN1, IBAN2, value);
        //Database add transactie
        pMediator.addTransaction(IBAN1, IBAN2, value, getCurrentDateTime(), description);
    }

    @Override
    public ArrayList<String> getTransactions(String IBAN) throws RemoteException {
        ArrayList<String> transList = new ArrayList();
        //Synchronize the list to prevent manipulation while iterating
        synchronized(transactions) {
            for (Transactie trans : transactions) {
                if (trans.getIBANTo().equals(IBAN) || trans.getIBANFrom().equals(IBAN)) {
                    transList.add(transactionToString(trans));
                }
            }
            }
        return transList;
    }

    /**
     * Converts a transaction to a String representing the the transaction
     * @param transaction
     * @return String representing a transaction
     */
    private String transactionToString(Transactie transaction) {
        String description = transaction.getDescription();
        if (description.isEmpty()) {
            return transaction.getDate() + ";" + String.valueOf(transaction.getAmount()) + ";" +
                    transaction.getIBANFrom() + ";" + transaction.getIBANTo();
        } else {
            return transaction.getDate() + ";" + String.valueOf(transaction.getAmount()) + ";" +
                    transaction.getIBANFrom() + ";" + transaction.getIBANTo() + ";" + transaction.getDescription();
        }
    }
    
    /**
     * Returns the local current date-time in String-value
     * @return date-time String-value
     */
    private String getCurrentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.toString();
    }
    
    /**
     * Set all database to lists in this class
     */
    private void setDatabaseData() throws RemoteException {
        //Set transacties
        for (String transValues : pMediator.getAllTransacties()) {
            transactions.add(stringToTransactie(transValues));
        }
    }
    
    /**
     * Converts the String of transactie-values to a Transactie object
     * @param values
     * @return Transactie
     */
    private Transactie stringToTransactie(String values) throws RemoteException {
        String[] fields = values.split(";");
        Transactie transactie = new Transactie(fields[3], fields[4], fields[2], Double.parseDouble(fields[1]));
        if (!fields[0].isEmpty()) transactie.setDescription(fields[0]);
        return transactie;
    }
}
