package Shared_Centrale;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface ITransactie extends Remote {
    
    /**
     * Returns the date of creation (in text) of this transaction.
     * @return the date as a String type.
     * @throws RemoteException 
     */
    String getDate() throws RemoteException;
    
    /**
     * Returns the amount of money transferred in this transaction.
     * @return the amount of money.
     * @throws RemoteException 
     */
    double getAmount() throws RemoteException;
    
    /**
     * Returns the description of this transaction.
     * @return the description of this transaction.
     * @throws RemoteException 
     */
    String getDescription() throws RemoteException;
}
