package Shared_Client;

import java.io.Serializable;
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
    
    public void addBankAccount(IBank bank) {
        
    }
    
    public void removeBankAccount(String IBAN) {
        
    }
    
    public String getUsername() {
        return name + residence;
    }
    
    public ArrayList<String> getBankAccounts() {
        return null;
    }
    
    public ArrayList<String> getTransactions() {
        return null;
    }
}

