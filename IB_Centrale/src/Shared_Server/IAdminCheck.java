package Shared_Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface IAdminCheck extends Remote {
    
    boolean checkIBAN(int IBAN) throws RemoteException;
}
