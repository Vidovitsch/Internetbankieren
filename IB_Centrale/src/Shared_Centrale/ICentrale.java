package Shared_Centrale;

import Shared_Server.IBankTrans;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface ICentrale extends Remote {
    
    void startTransaction(int IBAN1, int IBAN2, IBankTrans bank, double value) throws RemoteException;
    
    void getTransactions(int IBAN) throws RemoteException;
}
