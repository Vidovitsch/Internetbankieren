package Models;


/**
 *
 * @author David
 */
public class Bankrekening {
    
    private String IBAN;
    private double balance;
    private double credit = 100;
    
    public Bankrekening(String IBAN, double balance, double credit, Bank bank) {
        
    }
    
    /**
     * Adds a value of money to this bank account.
     * @param value of money.
     * @return True if succesful, else false.
     */
    public boolean addToBalance(double value) {
        return true;
    }
    
    /**
     * Removes a value of money from this bank account.
     * @param value of money
     * @return True if succesful, else false.
     */
    public boolean removeFromBalance(double value) {
        return true;
    }
}
