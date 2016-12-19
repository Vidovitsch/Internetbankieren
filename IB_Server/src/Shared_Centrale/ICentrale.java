package Shared_Centrale;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
     * @throws RemoteException 
     */
    void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws RemoteException;
    
    /**
     * Gives all the transaction of the bank account
     * linked with the IBAN parameter.
     * @param IBAN linked with a bank account.
     * @throws RemoteException 
     */
    void getTransactions(String IBAN) throws RemoteException;
}
