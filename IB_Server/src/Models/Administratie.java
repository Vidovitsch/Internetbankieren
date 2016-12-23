package Models;

import Exceptions.LoginException;
import Exceptions.RegisterException;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import fontyspublisher.IRemotePublisherForDomain;
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
    private IRemotePublisherForDomain publisher;
    private ArrayList<Sessie> sessies;
    private ArrayList<Klant> clients;
    private ArrayList<Bank> banks;
    
    //De publisher is er om de juiste gebruikers te subscriben
    public Administratie(ICentrale centrale, IRemotePublisherForDomain publisher) throws RemoteException {
        sessies = new ArrayList();
        clients = new ArrayList();
        banks = new ArrayList();
        this.centrale = centrale;
        this.publisher = publisher;
    }

    public void setPersistencyMediator(IPersistencyMediator pMediator) {
        this.pMediator = pMediator;
        try {
            setDatabaseData();
        } catch (RemoteException ex) {
            Logger.getLogger(Administratie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method only gets called if there are more banks involved within this concept.
     * @param bank 
     */
    public void addBank(Bank bank) {
        banks.add(bank);
    }
    
    @Override
    public Klant register(String naam, String woonplaats, String password) throws RegisterException, IllegalArgumentException, RemoteException {
        Klant klant = null;
        if (naam.isEmpty() || woonplaats.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Fill all fields");
        }
        else if (password.length() <= 8) {
            throw new IllegalArgumentException("Password has to be larger than 8 characters");
        }
        else if (getKlantByUsername(naam + woonplaats) != null) {
            throw new RegisterException("This username already exists");
        }
        else {
            if (pMediator.registerAccount(naam, woonplaats, password)) {
                klant = new Klant(naam, woonplaats);
                clients.add(klant);
                addSession(klant);
            }
        }
        return klant;
    }

    @Override
    public Klant login(String naam, String woonplaats, String password) throws LoginException, IllegalArgumentException, RemoteException {
        Klant klant = null;
        if (naam.isEmpty() || woonplaats.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Fill all fields");
        }
        else {
            int userID = pMediator.login(naam, woonplaats, password);
            if (userID == -1) {
                throw new LoginException("Invalid username or password");
            }
            else if (checkSession(naam + woonplaats)) {
                throw new LoginException("This account has already a session running");
            }
            else {
                klant = getKlantByUsername(naam + woonplaats);
                addSession(klant);
            }
        }
        return klant;
    }

    @Override
    public IBank getBank(Klant klant) throws RemoteException {
        //Moet vervangen worden als er meerdere banken bestaan.
        //Uit de database moet gekeken worden op welke banken de gebruiker
        //subscribed is. Deze banken worden als een lijst teruggegeven.
        return (IBank) banks.get(0);
    }

    @Override
    public boolean removeKlant(String name, String residence, String password) throws RemoteException {
        Klant klant = null;
        boolean bool = false;
        for (Klant k : clients) {
            if (k.equals(getKlantByUsername(name + residence))) {
                if (pMediator.removeKlant(name, residence, password)) {
                    klant = k;
                    bool = true;
                }
            }
        }
        if (bool) clients.remove(klant);
        return bool;
    }

    @Override
    public void logout(Klant klant) throws RemoteException {
        removeSession(klant);
    }
            
    /**
     * Checks if a client has still a session running.
     * @param username
     * @return True if session is running, else false.
     */
    public boolean checkSession(String username) {
        for (Sessie sessie : sessies) {
            if (sessie.getClient().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns a klant object by a given username
     * @param username
     * @return Klant
     */
    public Klant getKlantByUsername(String username) {
        Klant klant = null;
        for (Klant k : clients) {
            if (k.getUsername().equals(username)) {
                klant = k;
            }
        }
        return klant;
    }
    
    /**
     * Adds a session on this server (only in local lists).
     * and registers the user.
     * A session is added if a client is logged in.
     * @param klant 
     */
    private void addSession(Klant klant) throws RemoteException {
        Sessie session = new Sessie(klant, this);
        session.startSession();
        sessies.add(session);
        //Registers a property for this user
        publisher.registerProperty(klant.getUsername());
    }
    
    /**
     * Removes a session from this server
     * and unregisters the user.
     * A session is removed if a client is inactive for too long.
     * @param klant 
     */
    private void removeSession(Klant klant) throws RemoteException {
        Sessie sessie = null;
        for (Sessie s : sessies) {
            if (s.getClient().equals(klant)) {
                sessie = s;
            }
        }
        if (sessie != null) {
            sessie.stopSession();
            sessies.remove(sessie);
            pMediator.endSession(klant.getName(), klant.getResidence());
            //Unregisters a property for this user
            publisher.unregisterProperty(klant.getUsername());
        }
    }
    
    /**
     * Set all database to lists in this class
     */
    private void setDatabaseData() throws RemoteException {
        //Set clients
        for (String clientValues : pMediator.getAllKlanten()) {
            clients.add(stringToKlant(clientValues));
        }
        //Set banks
        for (String bankValues : pMediator.getAllBanks()) {
            banks.add(stringToBank(bankValues));
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
        Klant klant = new Klant(fields[0], fields[1]);
        if (fields[2].equals("1")) {
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
