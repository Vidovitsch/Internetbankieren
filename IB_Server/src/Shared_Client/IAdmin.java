package Shared_Client;

import Exceptions.LoginException;
import Exceptions.RegisterException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author David
 */
public interface IAdmin extends Remote {
    
    /**
     * Registers a user at the administration.
     * @param naam
     * @param woonplaats
     * @param password not null or size bigger than 7, else IllegalArgumentException.
     * @return Klant object if succesfull, else null.
     * @throws Exceptions.RegisterException
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    Klant register(String naam, String woonplaats, String password) throws RegisterException, IllegalArgumentException, RemoteException;
    
    /**
     * Logs in a user and creating a session if succesful.
     * @param naam
     * @param woonplaats
     * @param password not null, else IllegalArgumentException.
     * @return Klant object if succesfull, else null.
     * @throws Exceptions.LoginException
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    Klant login(String naam, String woonplaats, String password) throws LoginException, IllegalArgumentException, RemoteException;
    
    /**
     * Returns the subscribed bank of the user.
     * @param klant
     * @return IBank, null if the user has no bank.
     * @throws RemoteException 
     */
    IBank getBank(Klant klant) throws RemoteException;
    
    /**
     * Removes a user from the administration. All data gets lost.
     * @param username
     * @param residence
     * @param password
     * @return True if succesful, else false.
     * @throws RemoteException 
     */
    boolean removeKlant(String username, String residence, String password) throws RemoteException;
    
    /**
     * Logs out a user.
     * Gets called when a user wants to log out, or a session has expired.
     * @param klant
     * @throws RemoteException 
     */
    void logout(Klant klant) throws RemoteException;
}
