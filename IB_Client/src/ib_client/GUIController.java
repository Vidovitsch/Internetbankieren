package ib_client;

import Exceptions.LimitReachedException;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Pane;

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
            this.gui = gui;
            pHandler = new PropertyHandler();
            Registry serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
            admin = (IAdmin) serverRegistry.lookup("admin");

            publisher = (IRemotePublisherForListener) serverRegistry.lookup("serverPublisher");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String login(String naam, String woonplaats, String password) {
        String exMessage = "";
        try {
            klant = admin.login(naam, woonplaats, password);
            //Set properties
            pHandler.setLoginProperties(naam, woonplaats);
            //Subscribe to server
            publisher.subscribeRemoteListener(this, klant.getUsername());
        } catch (IllegalArgumentException | LoginException | RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            exMessage = ex.getMessage();
        }
        return exMessage;
    }

    public void logout() {
        try {
            admin.logout(klant);
            gui.logoutScreen();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String register(String naam, String woonplaats, String password) {
        String exMessage = "";
        try {
            klant = admin.register(naam, woonplaats, password);
            //Set properties
            pHandler.setLoginProperties(naam, woonplaats);
            //Subscribe to server
            publisher.subscribeRemoteListener(this, klant.getUsername());
        } catch (IllegalArgumentException | RegisterException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            exMessage = ex.getMessage();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exMessage;
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
    
    public String getAccounts() {
        String exMessage = "";
        try {
            gui.setAccountList(klant.getBankAccounts(bank));
        } catch (SessionExpiredException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            exMessage = ex.getMessage();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exMessage;
    }

    public String getTransactions(String IBAN) {
        String exMessage = "";
        try {
            gui.setTransactionList(klant.getTransactions(IBAN, bank));
        } catch (SessionExpiredException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            exMessage = ex.getMessage();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exMessage;
    }

    public String addBankAccount() {
        String exMessage = "";
        try {
            klant.addBankAccount(bank);
            getAccounts();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            exMessage = ex.getMessage();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exMessage;
    }

    public String removeBankAccount(String IBAN) {
        String exMessage = "";
        try {
            klant.removeBankAccount(IBAN, bank);
            getAccounts();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            exMessage = ex.getMessage();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exMessage;
    }

    public String startTransaction(String IBAN1, String IBAN2, double value, String description) {
        String exMessage = "";
        try {
            if (klant.startTransaction(IBAN1, IBAN2, value, description, bank)) {
                getTransactions(IBAN1);
                gui.initSuccessMessage("Transacion successful");
            } else {
                gui.initErrorMessage("Transaction failed");
            }
        } catch (SessionExpiredException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            exMessage = ex.getMessage();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException | LimitReachedException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            exMessage = ex.getMessage();
        }
        return exMessage;
    }

    public String[] getLastLogged() {
        return pHandler.getLoginProperties();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent pce) throws RemoteException {
        //gui.setAccountList((ArrayList<String>) pce.getNewValue());
    }
}
