package Shared_Centrale;

import Exceptions.LimitReachedException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public interface ICentrale extends Remote {
    
    /**
     * Inits a transaction between the two bank accounts
     * linked with the two IBAN parameters.
     * @param IBAN1 linked with a bank account.
     * @param IBAN2 linked with a bank account.
     * @param bank
     * @param value in money to be transferred.
     * @param description
     * @throws Exceptions.LimitReachedException
     * @throws RemoteException 
     */
    void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws LimitReachedException, RemoteException;
    
    /**
     * Gives all the transaction of the bank account
     * linked with the IBAN parameter.
     * @param IBAN linked with a bank account.
     * @return 
     * @throws RemoteException 
     */
    ArrayList<String> getTransactions(String IBAN) throws RemoteException;
}
