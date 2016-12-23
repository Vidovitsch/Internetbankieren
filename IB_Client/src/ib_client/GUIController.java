package ib_client;

import Exceptions.LoginException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Client.Klant;
import Utility.PropertyHandler;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForListener;
import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class GUIController extends UnicastRemoteObject implements IRemotePropertyListener
{
    private PropertyHandler pHandler;
    private GUI gui;
    private Klant klant;
    private IAdmin admin;
    private IBank bank;
    private IRemotePublisherForListener publisher;

    /**
     * Handles all RMI-based processes. Updates the GUI
     * @param gui
     * @throws java.rmi.RemoteException
     */
    public GUIController(GUI gui) throws RemoteException {
        try {
            pHandler = new PropertyHandler();
            Registry serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
            admin = (IAdmin) serverRegistry.lookup("admin");
            System.out.println("Admin lookup completed");

            publisher = (IRemotePublisherForListener) serverRegistry.lookup("serverPublisher");
            System.out.println("Publisher lookup completed");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void login(String naam, String woonplaats, String password) {
        try {
            klant = admin.login(naam, woonplaats, password);
            //Set properties
            pHandler.setLoginProperties(naam, woonplaats);
            //Subscribe to server
            publisher.subscribeRemoteListener(this, klant.getUsername());
        } catch (IllegalArgumentException | LoginException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logout() {
        try {
            admin.logout(klant);
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void register(String naam, String woonplaats, String password) {
        try {
            klant = admin.register(naam, woonplaats, password);
            //Set properties
            pHandler.setLoginProperties(naam, woonplaats);
            //Subscribe to server
            publisher.subscribeRemoteListener(this, klant.getUsername());
        } catch (IllegalArgumentException | RegisterException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setBank() {
        try {
            bank = admin.getBank(klant);
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeKlant() {
        try {
            admin.removeKlant("iets", "iets", "iets");
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBankName() {
        try {
            if (bank == null) {
                throw new NullPointerException("Bank isn't initialized yet");
            } else {
                return bank.getName();
            }
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getUsername() {
        if (klant == null) {
            throw new NullPointerException("Klant isn't initialized yet");
        } else {
            return klant.getUsername();
        }
    }
    
    public void getAccounts() {
        try {
            System.out.println(klant.getBankAccounts(bank).get(0));
            //gui.setAccountList(klant.getBankAccounts(bank));
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            //gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getTransactions(String IBAN) {
        try {
            gui.setTransactionList(klant.getTransactions(IBAN, bank));
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addBankAccount() {
        try {
            klant.addBankAccount(bank);
            getAccounts();
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeBankAccount(String IBAN) {
        try {
            klant.removeBankAccount(IBAN, bank);
            getAccounts();
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startTransaction(String IBAN1, String IBAN2, double value, String description)
    {
        System.out.println("haskdfh");
        try {
            if (klant.startTransaction(klant, IBAN1, IBAN2, value, description, bank)) {
                //getTransactions(IBAN1);
                //gui.initSuccessMessage("Transaction successful");
            } else {
                //gui.initErrorMessage("Transaction failed");
            }
        } catch (SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String[] getLastLogged() {
        return pHandler.getLoginProperties();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent pce) throws RemoteException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
