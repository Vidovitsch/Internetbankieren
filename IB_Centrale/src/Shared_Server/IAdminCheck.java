package Shared_Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface IAdminCheck extends Remote {
    
    /**
     * Checks if the parameter IBAN exists within the database.
     * @param IBAN to be checked.
     * @return True if the IBAN exists. False if it doesn't.
     * @throws RemoteException 
     */
    boolean checkIBAN(int IBAN) throws RemoteException;
}
