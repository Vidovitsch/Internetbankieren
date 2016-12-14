package Shared_Client;

import Exceptions.SessionExpiredException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


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
     * @return Sting representing the new bankaccount
     */
    public String addBankAccount(IBank bank) {
        try {
            return bank.addAccount(this);
        } catch (RemoteException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Removes a bank account on the server linked with this Client.
     * @param IBAN
     * @param bank
     * @return True if succesful, else false
     */
    public boolean removeBankAccount(String IBAN, IBank bank) {
        try {
            return bank.removeAccount(IBAN, this);
        } catch (RemoteException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Returns the username of this Client.
     * The username is a combination of this user's name and residence.
     * @return The user's username
     */
    public String getUsername() {
        return name + residence;
    }
    
    /**
     * Returns a list of Strings. Every String is
     * representing a bank account.
     * @param bank
     * @return A list of Strings representing a bank account.
     */
    public ArrayList<String> getBankAccounts(IBank bank) {
        try {
            return bank.getAccounts(this);
        } catch (RemoteException | IllegalArgumentException | SessionExpiredException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Returns a list of Strings. Every String is
     * representing a transaction.
     * @param IBAN
     * @param bank
     * @return A list of Strings representing a transaction.
     */
    public ArrayList<String> getTransactions(String IBAN, IBank bank) {
        try {
            return bank.getTransactions(IBAN, this);
        } catch (RemoteException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalArgumentException | SessionExpiredException ex) {
            Logger.getLogger(Klant.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}

