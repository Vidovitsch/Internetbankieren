package Models;

import Shared_Centrale.ITransactie;
import Shared_Centrale.IBankTrans;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 *
 * @author David
 */
public class Transactie extends UnicastRemoteObject implements ITransactie {

    private String date;
    private double amount;
    private String description;
    private IBankTrans bank;
    private String IBAN1;
    private String IBAN2;
            
    
    /**
     * This class is used to represent the made transaction
     * @param IBAN1
     * @param IBAN2
     * @param date
     * @param amount
     * @param bank
     * @throws RemoteException 
     */
    public Transactie(String IBAN1, String IBAN2, String date, double amount, IBankTrans bank) throws RemoteException {
        this.date = date;
        this.amount = amount;
        this.bank = bank;
        this.IBAN1 = IBAN1;
        this.IBAN2 = IBAN2;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDate() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getAmount() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getIBANFrom() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getIBANTo() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
