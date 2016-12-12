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
    
    public Transactie(String date, double amount, String description, IBankTrans bank) throws RemoteException {
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.bank = bank;
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

}
