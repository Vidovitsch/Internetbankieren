package Shared_Server;

import Shared_Centrale.ITransactie;
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
     * @param IBAN
     * @param value of money to be added.
     * @throws RemoteException 
     */
    void addSaldo(int IBAN, double value) throws RemoteException;
    
    /**
     * Removes a certain value of money from the bank account linked
     * with the IBAN parameter.
     * @param IBAN
     * @param value of money to be removed.
     * @throws RemoteException 
     */
    void removeSaldo(int IBAN, double value) throws RemoteException;
    
    /**
     * Sends the remote interface ITransactie to IBAN parameter.
     * This method gets called twice (for each bank account).
     * @param IBAN
     * @param transactie
     * @throws RemoteException 
     */
    void addTransactie(int IBAN, ITransactie transactie) throws RemoteException;
}
