package Models;

import Shared_Client.Klant;


/**
 *
 * @author David
 */
public class Bankrekening {
    
    private String IBAN;
    private double balance = 0;
    private final double credit = 100;
    private Klant klant;
    
    public Bankrekening(String IBAN, Klant klant) {
        this.IBAN = IBAN;
        this.klant = klant;
    }
    
    public Klant getKlant() {
        return klant;
    }
    
    /**
     * Adds a value of money to this bank account.
     * @param value of money.
     */
    public void addToBalance(double value) {
        balance += value;
    }
    
    /**
     * Removes a value of money from this bank account.
     * @param value of money
     * @return true if successful, else false.
     */
    public boolean removeFromBalance(double value) {
        double newBalance = balance - value;
        if (newBalance < credit * -1) {
            return false;
        } else {
            balance = newBalance;
            return true;
        }
    }
    
    @Override
    public String toString() {
        return IBAN + ";"  + String.valueOf(balance);
    }
}
