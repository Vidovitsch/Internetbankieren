package Shared_Client;

import Exceptions.SessionExpiredException;
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
    String getName() throws RemoteException;
    
    /**
     * Returns a list of Strings. Every String is
     * representing a bank account.
     * NoDataFounException when client has no bank accounts.
     * @param klant, if null throws IllegalArgumentException.
     * @return A list of Strings representing a bank account.
     * @throws RemoteException
     */
    ArrayList<String> getAccounts(Klant klant) throws IllegalArgumentException, RemoteException;
    
    /**
     * Returns a list of Strings. Every String is
     * representing a transaction.
     * If session is over, SessionExpiredException.
     * NoDataFounException when client has no bank transactions.
     * @param IBAN, if empty throws IllegalArgumentException.
     * @param klant
     * @return A list of Strings representing a transaction.
     * @throws Exceptions.SessionExpiredException
     * @throws RemoteException
     */
    ArrayList<String> getTransactions(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException;
    
    /**
     * Adds a bank account for a user.
     * If session is over, SessionExpiredException.
     * @param klant, if empty throws IllegalArgumentException.
     * @return A String representing the new bank account.
     * @throws Exceptions.SessionExpiredException
     * @throws RemoteException
     */
    String addAccount(Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException;
    
    /**
     * Removes a account from a user.
     * If session is over, SessionExpiredException.
     * @param IBAN representing the bank account, if empty or non-existing throws IllegalArgumentException..
     * @param klant
     * @return True if succesful, else false.
     * @throws Exceptions.SessionExpiredException
     * @throws RemoteException
     */
    boolean removeAccount(String IBAN, Klant klant) throws SessionExpiredException, IllegalArgumentException, RemoteException;
    
    /**
     * Starts a transaction between two bank accounts.
     * If session is over, SessionExpiredException.
     * @param klant
     * @param IBAN1 representing the bank account, if empty throws IllegalArgumentException.
     * @param IBAN2 representing the bank account, if empty throws IllegalArgumentException.
     * @param value of the money to be transferred. Has to be greater than 0 or not empty, else IllegalArgumentException.
     * @return True if succesful, else false.
     * @throws Exceptions.SessionExpiredException
     * @throws RemoteException
     */
    boolean startTransaction(Klant klant, String IBAN1, String IBAN2, double value) throws SessionExpiredException, IllegalArgumentException, RemoteException;
}
