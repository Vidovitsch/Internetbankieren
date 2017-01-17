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
import javafx.application.Platform;

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
     *
     * @param gui
     * @throws java.rmi.RemoteException
     */
    public GUIController(GUI gui) throws RemoteException
    {
        try
        {
            this.gui = gui;
            pHandler = new PropertyHandler();
            Registry serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
            admin = (IAdmin) serverRegistry.lookup("admin");

            publisher = (IRemotePublisherForListener) serverRegistry.lookup("serverPublisher");
        } catch (RemoteException | NotBoundException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean login(String naam, String woonplaats, String password) {
        try
        {
            klant = admin.login(naam, woonplaats, password);
            //Set properties
            pHandler.setLoginProperties(naam, woonplaats);
            //Subscribe to server
            publisher.subscribeRemoteListener(this, klant.getUsername());
        } catch (IllegalArgumentException | LoginException | RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void logout() {
        try
        {
            admin.logout(klant);
            gui.logoutScreen();
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean register(String naam, String woonplaats, String password) {
        try {
            klant = admin.register(naam, woonplaats, password);
            //Set properties
            pHandler.setLoginProperties(naam, woonplaats);
            //Subscribe to server
            publisher.subscribeRemoteListener(this, klant.getUsername());
        } catch (IllegalArgumentException | RegisterException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
            return false;
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void setBank()
    {
        try {
            bank = admin.getBank(klant);
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBankShortName() {
        //Implemntation has to be changed if more banks a implemented
        String shortName = "";
        try {
            return bank.getShortName();
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return shortName;
    }
    
    public void removeKlant()
    {
        try
        {
            admin.removeKlant("iets", "iets", "iets");
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBankName()
    {
        try
        {
            if (bank == null)
            {
                throw new NullPointerException("Bank isn't initialized yet");
            } else
            {
                return bank.getName();
            }
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getUsername()
    {
        if (klant == null)
        {
            throw new NullPointerException("Klant isn't initialized yet");
        } else
        {
            return klant.getUsername();
        }
    }

    public void getAccounts()
    {
        try
        {
            gui.setAccountList(klant.getBankAccounts(bank));
        } catch (SessionExpiredException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getTransactions(String IBAN)
    {
        try
        {
            gui.setTransactionList(klant.getTransactions(IBAN, bank));
        } catch (SessionExpiredException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addBankAccount()
    {
        try
        {
            klant.addBankAccount(bank);
            getAccounts();
        } catch (SessionExpiredException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeBankAccount(String IBAN)
    {
        try
        {
            klant.removeBankAccount(IBAN, bank);
            getAccounts();
        } catch (SessionExpiredException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startTransaction(String IBAN1, String IBAN2, double value, String description)
    {
        try
        {
            if (klant.startTransaction(IBAN1, IBAN2, value, description, bank))
            {
                getTransactions(IBAN1);
                gui.initSuccessMessage("Transaction successful!");
            } else
            {
                gui.initErrorMessage("Transaction failed");
            }
        } catch (SessionExpiredException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            logout();
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException | LimitReachedException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        }
    }

    public String[] getLastLogged()
    {
        return pHandler.getLoginProperties();
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) throws RemoteException
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> currentIBANs = gui.getCurrentIBANs();
                gui.setAccountList((ArrayList<String>) pce.getNewValue());
                
                String changedIBAN = getChangedIBAN(currentIBANs, (ArrayList<String>) pce.getNewValue());
                if (!changedIBAN.isEmpty()) {
                    gui.initAlertMessage("A transacation has been made on " + changedIBAN + ".");
                }
            }
        });
    }
    
    private String getChangedIBAN(ArrayList<String> oldValues, ArrayList<String> newValues) {
        String value = "";
        for (int i = 0; i < newValues.size(); i++) {
            String ov = oldValues.get(i);
            String nv = newValues.get(i);
            if (!ov.equals(nv)) {
                value = nv;
                if (gui.accountToAmount(value).contains("-")) {
                    return "";
                }
                return gui.accountToIBAN(value);
            }
        }
        return value;
    }
}
