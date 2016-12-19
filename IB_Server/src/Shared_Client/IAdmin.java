package Shared_Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface IAdmin extends Remote {
    
    /**
     * Registers a user at the administration.
     * @param userName
     * @param password not null or size bigger than 7, else IllegalArgumentException.
     * @return Klant object if succesfull, else null.
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    Klant register(String userName, String password) throws IllegalArgumentException, RemoteException;
    
    /**
     * Logs in a user and creating a session if succesful.
     * @param userName
     * @param password not null, else IllegalArgumentException.
     * @return Klant object if succesfull, else null.
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    Klant login(String userName, String password) throws IllegalArgumentException, RemoteException;
    
    /**
     * Returns the subscribed bank of the user.
     * @param klant
     * @return IBank, null if the user has no bank.
     * @throws RemoteException 
     */
    IBank getBank(Klant klant) throws RemoteException;
    
    /**
     * Removes a user from the administration. All data gets lost.
     * @param klant
     * @return True if succesful, else false.
     * @throws RemoteException 
     */
    boolean removeKlant(Klant klant) throws RemoteException;
    
    /**
     * Logs out a user.
     * Gets called when a user wants to log out, or a session has expired.
     * @param klant
     * @throws RemoteException 
     */
    void logout(Klant klant) throws RemoteException;
}
