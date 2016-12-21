package Shared_Client;

import Exceptions.SessionExpiredException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Klant implements Serializable {

    private String name;
    private String residence;
    
    public Klant(String name, String residence) {
        this.name = name;
        this.residence = residence;
    }
    
    /**
     * Adds a bank account on the server linked with this Client.
     * @param bank
     * @throws Exceptions.SessionExpiredException
     * @throws java.rmi.RemoteException
     */
    public void addBankAccount(IBank bank) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        bank.addBankAccount(this);
    }
    
    /**
     * Removes a bank account on the server linked with this Client.
     * @param IBAN
     * @param bank
     * @return True if succesful, else false
     * @throws Exceptions.SessionExpiredException
     * @throws java.rmi.RemoteException
     */
    public boolean removeBankAccount(String IBAN, IBank bank) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        return bank.removeBankAccount(IBAN, this);
    }
    
    /**
     * Returns the username of this Client.
     * The username is a combination of this user's name and residence.
     * @return The user's username
     */
    public String getUsername() {
        return name + residence;
    }
    
    public String getName() {
        return name;
    }
    
    public String getResidence() {
        return residence;
    }
    
    /**
     * Returns a list of Strings. Every String is
     * representing a bank account.
     * @param bank
     * @return A list of Strings representing a bank account.
     * @throws Exceptions.SessionExpiredException
     * @throws java.rmi.RemoteException
     */
    public ArrayList<String> getBankAccounts(IBank bank) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        return bank.getAccounts(this);
    }
    
    /**
     * Returns a list of Strings. Every String is
     * representing a transaction.
     * @param IBAN
     * @param bank
     * @return A list of Strings representing a transaction.
     * @throws Exceptions.SessionExpiredException
     * @throws java.rmi.RemoteException
     */
    public ArrayList<String> getTransactions(String IBAN, IBank bank) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        return bank.getTransactions(IBAN, this);
    }
    
    public boolean startTransaction(Klant klant, String IBAN1, String IBAN2, double value, String description, IBank bank) throws SessionExpiredException, IllegalArgumentException, RemoteException {
        return bank.startTransaction(klant, IBAN1, IBAN2, value, description);
    }
}

