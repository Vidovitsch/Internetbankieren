package Models;

import Exceptions.LoginException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import fontyspublisher.IRemotePublisherForDomain;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

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

    public void setPersistencyMediator(IPersistencyMediator pMediator) throws RemoteException {
        this.pMediator = pMediator;
        setDatabaseData();
    }
    
    /**
     * This method only gets called if there are more banks involved within this concept.
     * @param bank 
     * @return  
     * @throws java.rmi.RemoteException  
     */
    public boolean addBank(Bank bank) throws RemoteException {
        for (Bank b : banks) {
            if (b.getName().equals(bank.getName())) {
                return false;
            }
        }
        banks.add(bank);
        return true;
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
            pMediator.registerAccount(naam, woonplaats, password);
            klant = new Klant(naam, woonplaats);
            clients.add(klant);
            addSession(klant);
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
        //Synchronize the list to prevent manipulation while iterating
        synchronized(clients) {
            for (Klant k : clients) {
                if (k.getUsername().equals(name + residence)) {
                    if (pMediator.removeKlant(name, residence, password)) {
                        klant = k;
                        bool = true;
                    }
                }
            }
        }
        if (bool) {
            clients.remove(klant);
        }
        return bool;
    }

    @Override
    public void logout(Klant klant) throws RemoteException {
        removeSessionDatabase(klant);
        removeSessionLocal(klant);
    }
    
    /**
     * Checks if a client has still a session running.
     * @param username
     * @return True if session is running, else false.
     */
    public boolean checkSession(String username) {
        //Synchronize the list to prevent manipulation while iterating
        synchronized(sessies) {
            for (Sessie sessie : sessies) {
                if (sessie.getClient().getUsername().equals(username)) {
                    return true;
                }
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
        //Synchronize the list to prevent manipulation while iterating
        synchronized(clients) {
            for (Klant k : clients) {
                if (k.getUsername().equals(username)) {
                    klant = k;
                }
            }
        }
        return klant;
    }
    
    /**
     * Refreshing the session of a client on an action.
     * The session timer resets.
     * @param klant 
     */
    public void refreshSession(Klant klant) {
        //Synchronize the list to prevent manipulation while iterating
        synchronized(sessies) {
            for (Sessie s : sessies) {
                if (s.getClient().getUsername().equals(klant.getUsername())) {
                    s.refreshSession();
                }
            }
        }
    }
    
    /**
     * Publishes the updated list of transactions and bankaccounts to the
     * involved users with a running session.
     * @param usernameTo
     * @param usernameFrom
     * @param bank
     * @return true if succesfull, else false.
     * @throws RemoteException 
     * @throws Exceptions.SessionExpiredException 
     */
    public boolean publishTransaction(String usernameFrom, String usernameTo, Bank bank) throws RemoteException, SessionExpiredException {
        ArrayList<String> updatedBankAccounts;
        Klant klantIBANFrom = null;
        
        //No need to check for a running session
        //The user making a transaction has always a running session
        klantIBANFrom = getKlantByUsername(usernameFrom);
        updatedBankAccounts = klantIBANFrom.getBankAccounts(bank);
        publisher.inform(usernameFrom, null, updatedBankAccounts);
            
        if (checkSession(usernameTo)) {
            Klant klantIBANTo = getKlantByUsername(usernameTo);
            //Check if user isn't making a transaction with himself. If he does, he doesn't need
            //another updated list.
            if (!klantIBANFrom.getUsername().equals(klantIBANTo.getUsername())) {
                updatedBankAccounts = getKlantByUsername(usernameTo).getBankAccounts(bank);
                publisher.inform(usernameTo, null, updatedBankAccounts);
            }
        }
        return true;
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
        //Register a property for this user
        publisher.registerProperty(klant.getUsername());
    }
    
    /**
     * Removes a session from this server locally
     * A session is removed if a client is inactive for too long or a client
     * logs out.
     * @param klant 
     */
    public void removeSessionLocal(Klant klant) {
        Sessie sessie = null;
        //Synchronize the list to prevent manipulation while iterating
        synchronized(sessies) {
            for (Sessie s : sessies) {
                if (s.getClient().getUsername().equals(klant.getUsername())) {
                    sessie = s;
                }
            }
        }
        if (sessie != null) {
            sessie.stopSession();
            sessies.remove(sessie);
        }
    }
    
    /**
     * Removes a session from the database.
     * A session is removed if a client is inactive for too long or a client
     * logs out.
     * Also the publisher unregisters the property of this client.
     * This method has to be called before removeSessionLocal!
     * @param klant
     * @throws RemoteException 
     */
    private void removeSessionDatabase(Klant klant) throws RemoteException {
        pMediator.endSession(klant.getName(), klant.getResidence());
        if (this.checkSession(klant.getUsername())) {
            //Unregister a property for this user
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
