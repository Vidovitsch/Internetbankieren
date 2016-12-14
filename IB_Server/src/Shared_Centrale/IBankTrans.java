package Shared_Centrale;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface IBankTrans extends Remote {
    
    /**
     * Adds a certain value of money to the bank account linked with
     * the IBAN parameter.
     * @param IBAN not empty, else IllegalArgumentException.
     * @param value of money to be added, has to be greater than 0, else IllegalArgumentException.
     * @throws RemoteException 
     */
    void addSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException;
    
    /**
     * Removes a certain value of money from the bank account linked
     * with the IBAN parameter.
     * @param IBAN not empty, else IllegalArgumentException.
     * @param value of money to be removed, must be postive else IllegalArgumentException.
     * @throws RemoteException 
     */
    void removeSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException;
}
