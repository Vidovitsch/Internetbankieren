package Shared_Client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public interface IBank extends Remote {
    
    /**
     * Returns the name of this bank.
     * @return Name of this bank.
     * @throws RemoteException
     */
    String getNaam() throws RemoteException;
    
    /**
     * Returns a list of Strings. Every String is
     * representing a bank account.
     * @param klant
     * @return A list of Strings representing a bank account.
     * @throws RemoteException
     */
    ArrayList<String> getAccounts(Klant klant) throws RemoteException;
    
    /**
     * Returns a list of Strings. Every String is
     * representing a transaction.
     * @param IBAN
     * @return A list of Strings representing a transaction.
     * @throws RemoteException
     */
    ArrayList<String> getTransactions(String IBAN) throws RemoteException;
    
    /**
     * Adds a bank account for a user.
     * @param klant
     * @return A String representing the new bank account.
     * @throws RemoteException
     */
    String addAccount(Klant klant) throws RemoteException;
    
    /**
     * Removes a account from a user.
     * @param IBAN representing the bank account.
     * @return True if succesful, else false.
     * @throws RemoteException
     */
    boolean removeAccount(String IBAN) throws RemoteException;
    
    /**
     * Starts a transaction between two bank accounts.
     * @param IBAN1 representing the bank account.
     * @param IBAN2 representing the bank account.
     * @param value of the money to be transferred. Has to be greater than 0, else IllegalArgumentException.
     * @return True if succesful, else false.
     * @throws RemoteException
     */
    boolean startTransaction(String IBAN1, String IBAN2, double value) throws IllegalArgumentException, RemoteException;
}
