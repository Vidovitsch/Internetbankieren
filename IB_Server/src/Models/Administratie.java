package Models;

import Shared_Centrale.ICentrale;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Client.Klant;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


/**
 *
 * @author David
 */
public class Administratie extends UnicastRemoteObject implements IAdmin {

    private ArrayList<Sessie> sessies;
    private ArrayList<Klant> clients;
    private ICentrale centrale;
    private Bank bank;
    
    public Administratie(ICentrale centrale, Bank bank) throws RemoteException {
        this.centrale = centrale;
        this.bank = bank;
        sessies = new ArrayList();
        clients = new ArrayList();
    }

    @Override
    public Klant register(String name, String residence, String password) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Klant login(String name, String residence, String password) throws IllegalArgumentException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public IBank getBank(Klant klant) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeKlant(Klant klant) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logout(Klant klant) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Adds a session on this server.
     * A session is added if a client is logged in.
     * @param klant 
     */
    private void addSessie(Klant klant) {
        
    }
    
    /**
     * Removes a session from this server.
     * A session is removed if a client is inactive for too long.
     * @param klant 
     */
    private void removeSessie(Klant klant) {
        
    }
    
    /**
     * Checks if a IBAN is a valid and existing one.
     * @param IBAN
     * @return True if it exists, else false.
     */
    private boolean checkIBAN(String IBAN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
