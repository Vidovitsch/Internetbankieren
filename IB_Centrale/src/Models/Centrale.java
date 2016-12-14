package Models;

import Shared_Centrale.ICentrale;
import Shared_Centrale.ITransactie;
import Shared_Centrale.IBankTrans;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class Centrale extends UnicastRemoteObject implements ICentrale {
    private ArrayList<ITransactie> transacties;
    
    public Centrale() throws RemoteException {
        transacties = new ArrayList();
    }

    @Override
    public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value) throws RemoteException {

    }

    @Override
    public void getTransactions(String IBAN) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
