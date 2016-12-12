package Shared_Server;

import Shared_Centrale.ITransactie;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface IBankTrans extends Remote {
    
    void addSaldo(int IBAN, double value) throws RemoteException;
    
    void removeSaldo(int IBAN, double value) throws RemoteException;
    
    void addTransactie(int IBAN, ITransactie transactie) throws RemoteException;
}
