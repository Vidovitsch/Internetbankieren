package Shared_Client;

import java.io.Serializable;


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
    
}

