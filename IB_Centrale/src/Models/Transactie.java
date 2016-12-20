package Models;

import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public class Transactie {

    private String date;
    private double amount;
    private String description;
    private String IBAN1;
    private String IBAN2;
            
    
    /**
     * This class is used to represent the made transaction
     * @param IBAN1
     * @param IBAN2
     * @param date
     * @param amount
     * @throws RemoteException 
     */
    public Transactie(String IBAN1, String IBAN2, String date, double amount) throws RemoteException {
        this.date = date;
        this.amount = amount;
        this.IBAN1 = IBAN1;
        this.IBAN2 = IBAN2;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the date of creation (in text) of this transaction.
     * @return the date as a String type.
     * @throws RemoteException 
     */
    public String getDate() throws RemoteException {
        return date;
    }

    /**
     * Returns the amount of money transferred in this transaction.
     * @return the amount of money.
     * @throws RemoteException 
     */
    public double getAmount() throws RemoteException {
        return amount;
    }

    /**
     * Returns the description of this transaction.
     * @return the description of this transaction.
     * @throws RemoteException 
     */
    public String getDescription() throws RemoteException {
        return description;
    }

    /**
     * Returns the IBAN from the bank account who made the transaction.
     * @return IBAN
     * @throws RemoteException 
     */
    public String getIBANFrom() throws RemoteException {
        return IBAN1;
    }

    /**
     * Returns the IBAN from the bank account who received the transaction.
     * @return IBAN
     * @throws RemoteException 
     */
    public String getIBANTo() throws RemoteException {
        return IBAN2;
    }
}
