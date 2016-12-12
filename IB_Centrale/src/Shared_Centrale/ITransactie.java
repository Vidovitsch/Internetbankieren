package Shared_Centrale;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface ITransactie extends Remote {
    
    String getDate() throws RemoteException;
    
    double getAmount() throws RemoteException;
    
    String getDescription() throws RemoteException;
}
