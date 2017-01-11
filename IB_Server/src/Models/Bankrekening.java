package Models;

import Shared_Client.Klant;

/**
 *
 * @author David
 */
public class Bankrekening {
    
    private String IBAN;
    private double balance = 0;
    private double credit = 100;
    private Klant klant;
    
    public Bankrekening(String IBAN, Klant klant) {
        this.IBAN = IBAN;
        this.klant = klant;
    }
    
    /**
     * Returns the owner of this bank account
     * @return klant
     */
    public Klant getKlant() {
        return klant;
    }
    
    /**
     * Adds a value of money to this bank account.
     * @param value of money.
     */
    public synchronized void addToBalance(double value) {
        balance += value;
    }
    
    /**
     * Removes a value of money from this bank account.
     * @param value of money
     * @return true if successful, else false.
     */
    public synchronized boolean removeFromBalance(double value) {
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
