package Models;

import Shared_Centrale.ICentrale;
import Shared_Centrale.ITransactie;
import Shared_Server.IAdminCheck;
import Shared_Server.IBankTrans;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Centrale extends UnicastRemoteObject implements ICentrale {

    private ArrayList<ITransactie> transacties;
    private IAdminCheck adminCheck;
    
    public Centrale(IAdminCheck adminCheck) throws RemoteException {
        transacties = new ArrayList();
        this.adminCheck = adminCheck;
    }
    
    @Override
    public void startTransaction(int IBAN1, int IBAN2, IBankTrans bank, double value) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getTransactions(int IBAN) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
