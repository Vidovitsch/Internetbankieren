package ib_client;

import Exceptions.LoginException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Client.Klant;
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

    private GUI gui;
    private Klant klant;
    private IAdmin admin;
    private IBank bank;
    private IRemotePublisherForListener publisher;
    private boolean testing = true;

    /**
     * Handles all RMI-based processes. Updates the GUI
     *
     * @param gui
     * @throws java.rmi.RemoteException
     */
    public GUIController(GUI gui) throws RemoteException
    {
        if (!testing)
        {
            try
            {
                Registry serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
                admin = (IAdmin) serverRegistry.lookup("admin");
                System.out.println("Admin lookup completed");

                publisher = (IRemotePublisherForListener) serverRegistry.lookup("serverPublisher");
                System.out.println("Publisher lookup completed");
            } catch (RemoteException | NotBoundException ex)
            {
                Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void login(String naam, String woonplaats, String password) {
        try {
            klant = admin.login(naam, woonplaats, password);
        } catch (IllegalArgumentException | LoginException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logout()
    {
        try
        {
            admin.logout(klant);
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void register(String naam, String woonplaats, String password) {
        try {
            klant = admin.register(naam, woonplaats, password);
        } catch (IllegalArgumentException | RegisterException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getBank()
    {
        try
        {
            bank = admin.getBank(klant);
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeKlant()
    {
        try
        {
            admin.removeKlant(klant);
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBankName()
    {
        try
        {
            return bank.getName();
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getUsername()
    {
        return klant.getUsername();
    }

    public void getAccounts()
    {
        try
        {
            gui.setAccountList(klant.getBankAccounts(bank));
        } catch (SessionExpiredException | IllegalArgumentException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SessionExpiredException | IllegalArgumentException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SessionExpiredException | IllegalArgumentException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SessionExpiredException | IllegalArgumentException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startTransaction(Klant klant, String IBAN1, String IBAN2, double value, String description)
    {
        try
        {
            if (klant.startTransaction(klant, IBAN1, IBAN2, value, description, bank))
            {
                getTransactions(IBAN1);
                gui.initErrorMessage("Transaction successful");
            } else
            {
                gui.initErrorMessage("Transaction failed");
            }
        } catch (SessionExpiredException | IllegalArgumentException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
            gui.initErrorMessage(ex.getMessage());
        } catch (RemoteException ex)
        {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) throws RemoteException
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
