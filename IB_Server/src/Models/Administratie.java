package Models;

import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author David
 */
public class Administratie extends UnicastRemoteObject implements IAdmin {

    private ICentrale centrale;
    private IPersistencyMediator pMediator;
    private ArrayList<Sessie> sessies;
    private ArrayList<Klant> clients;
    private ArrayList<Bank> banks;
    
    public Administratie(ICentrale centrale) throws RemoteException {
        sessies = new ArrayList();
        clients = new ArrayList();
        banks = new ArrayList();
        this.centrale = centrale;
    }

    public void setPersistencyMediator(IPersistencyMediator pMediator) {
        this.pMediator = pMediator;
        try {
            setDatabaseData();
        } catch (RemoteException ex) {
            Logger.getLogger(Administratie.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        int userID = pMediator.Login(userName, password);
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
     * Set all database to lists in this class
     */
    private void setDatabaseData() throws RemoteException {
        //Set banks
        for (String bankValues : pMediator.getAllBanks()) {
            banks.add(stringToBank(bankValues));
        }
        //Set clients
        for (String clientValues : pMediator.getAllKlanten()) {
            clients.add(stringToKlant(clientValues));
        }
    }
    
    /**
     * Converts the String of klant-values to a Klant object.
     * Als de klant een geldige sessie heeft draaien wordt er ook
     * voor deze klant een sessie gestart.
     * @param values
     * @return Klant
     */
    private Klant stringToKlant(String values) {
        String[] fields = values.split(";");
        System.out.println(fields[0] + " :: " + fields[1] + " :: " + fields[2]);
        Klant klant = new Klant(fields[0], fields[1]);
        if (fields[2].equals("1")) {
            System.out.println("sessie added");
            sessies.add(new Sessie(klant, this));
        } 
        return new Klant(fields[0], fields[1]);
    }
    
    /**
     * Converts the String of bank-values to a Bank object
     * @param values
     * @return Bank
     */
    private Bank stringToBank(String values) throws RemoteException {
        String[] fields = values.split(";");
        Bank bank = new Bank(fields[0], fields[1], this, centrale);
        bank.setPersistencyMediator(pMediator);
        return bank;
    }
}
