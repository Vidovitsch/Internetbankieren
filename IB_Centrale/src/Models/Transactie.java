package Models;

import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public class Transactie {

    private String date;
    private double amount;
    private String description = "";
    private String IBAN1;
    private String IBAN2;
            
    /**
     * This class is used to represent the made transaction
     * @param IBAN1
     * @param IBAN2
     * @param date
     * @param amount 
     */
    public Transactie(String IBAN1, String IBAN2, String date, double amount) {
        this.date = date;
        this.amount = amount;
        this.IBAN1 = IBAN1;
        this.IBAN2 = IBAN2;
    }
    
    /**
     * Sets the description of a transaction.
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the date of creation (in text) of this transaction.
     * @return the date as a String type. 
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the amount of money transferred in this transaction.
     * @return the amount of money. 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the description of this transaction.
     * @return the description of this transaction. 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the IBAN from the bank account who made the transaction.
     * @return IBAN 
     */
    public String getIBANFrom() {
        return IBAN1;
    }

    /**
     * Returns the IBAN from the bank account who received the transaction.
     * @return IBAN 
     */
    public String getIBANTo() {
        return IBAN2;
    }
}
