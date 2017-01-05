package Models;

import Shared_Centrale.ICentrale;
import Shared_Centrale.IBankTrans;
import Shared_Data.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void setPersistencyMediator(IPersistencyMediator pMediator) {
        this.pMediator = pMediator;
        try {
            setDatabaseData();
        } catch (RemoteException ex) {
            Logger.getLogger(Centrale.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws RemoteException {
        if (bank.removeSaldo(IBAN1, value)) {
            bank.addSaldo(IBAN2, value);
            //Melding geven als het niet lukt!!!
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
        for (Transactie trans : transactions) {
            if (trans.getIBANTo().equals(IBAN) || trans.getIBANFrom().equals(IBAN)) {
                transList.add(transactionToString(trans));
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
        try {
            String description = transaction.getDescription();
            if (description.isEmpty()) {
                return transaction.getDate() + ";" + String.valueOf(transaction.getAmount()) + ";" +
                        transaction.getIBANTo() + ";" + transaction.getIBANFrom();
            } else {
                return transaction.getDate() + ";" + String.valueOf(transaction.getAmount()) + ";" +
                        transaction.getIBANTo() + ";" + transaction.getIBANFrom() + ";" + transaction.getDescription();
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Centrale.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
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
