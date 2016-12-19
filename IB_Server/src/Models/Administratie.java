package Models;

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
    private ArrayList<Bank> banks;
    
    public Administratie() throws RemoteException {
        sessies = new ArrayList();
        clients = new ArrayList();
        banks = new ArrayList();
    }

    public void addBank(Bank bank) {
        banks.add(bank);
    }
    
    @Override
    public Klant register(String userName, String password) throws IllegalArgumentException, RemoteException {
        //DB code (-1 = not successful, else successful)
        return null;
    }

    @Override
    public Klant login(String userName, String password) throws IllegalArgumentException, RemoteException {
        //DB code (-1 = not successful, else successful)
        addSession(null);
        return null;
    }

    @Override
    public IBank getBank(Klant klant) throws RemoteException {
        //DB code (bank.getName() in database vergelijken of de klant daarop accounts heeft)
        return null;
    }

    @Override
    public boolean removeKlant(Klant klant) throws RemoteException {
        boolean bool = false;
        for (Klant k : clients) {
            if (k.equals(klant)) {
                bool = true;
            }
        }
        if (bool) clients.remove(klant);
        //DB code (ook uit de DB verwijderen)
        return bool;
    }

    @Override
    public void logout(Klant klant) throws RemoteException {
        removeSession(klant);
    }
            
    /**
     * Adds a session on this server.
     * A session is added if a client is logged in.
     * @param klant 
     */
    private void addSession(Klant klant) {
        sessies.add(new Sessie(klant, this));
        //DB code (Sessie toevoegen aan DB)
    }
    
    /**
     * Removes a session from this server.
     * A session is removed if a client is inactive for too long.
     * @param klant 
     */
    private void removeSession(Klant klant) {
        Sessie sessie = null;
        for (Sessie s : sessies) {
            if (s.getClient().equals(klant)) {
                sessie = s;
            }
        }
        sessies.remove(sessie);
        //DB code (Sessie weghalen uit de DB)
    }
    
    /**
     * Checks if a client has still a session running.
     * @param klant
     * @return True if session is running, else false.
     */
    public boolean checkSession(Klant klant) {
        for (Sessie sessie : sessies) {
            if (sessie.getClient().equals(klant)) {
                return true;
            }
        }
        return false;
    }
}
