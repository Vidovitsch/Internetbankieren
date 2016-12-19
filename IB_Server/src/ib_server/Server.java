package ib_server;

import Models.Administratie;
import Models.Bank;
import Shared_Centrale.ICentrale;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
import Shared_Data.IPersistencyMediator;
import fontyspublisher.IRemotePublisherForDomain;
import fontyspublisher.RemotePublisher;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class Server
{
    private final String ipAddressDB = "145.93.177.68";
    private final int portNumber = 1088;
    private static final String bindingName = "Database";
    private IPersistencyMediator database = null;
    private boolean connectedToDatabase = false;
    
    public IRemotePublisherForDomain publisher;
    private Registry centraleRegistry;
    private Registry serverRegistry;
    private Registry dataBaseRegistry;
    
    private ICentrale centrale;
    private Administratie admin;
    
    /**
     * Handles all RMI-based processes.
     */
    public Server() {
         try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            serverRegistry = LocateRegistry.createRegistry(1099);
            getCentraleRegistryBinds();
            setCentraleRegistryBinds();
        } catch (RemoteException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    /**
     * Gets the registry of the Server and its binds.
     */
    private void getCentraleRegistryBinds() {
        try {
            centraleRegistry = LocateRegistry.getRegistry("localhost", 1100);
            System.out.println("Centrale registry found");
            
            centrale = (ICentrale) centraleRegistry.lookup("centrale");
            System.out.println("Centrale lookup completed");
        } catch (RemoteException | NotBoundException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sets the binds of the centraleRegistry.
     */
    private void setCentraleRegistryBinds() {
        try {
            //Make connection with the database server
            connectedToDatabase = connectToRMIDatabaseServer();
            
            //Instantiate publisher, administration and bank
            publisher = new RemotePublisher();
            admin = new Administratie();
            Bank bank = new Bank("Rabobank", "RABO", admin, centrale);
            admin.addBank(bank);
            
            //Adding database mediator to administration and bank
            bank.setPersistencyMediator(database);
            admin.setPersistencyMediator(database);
            
            //Bind admin with the registry
            serverRegistry.bind("admin", (IAdmin) admin);
            System.out.println("Centrale bound");
            
            //Bind bank with the registry
            serverRegistry.bind("bank", (IBank) bank);
            System.out.println("Bank bound");
            
            //Bind publisher with the registry
            serverRegistry.bind("serverPublisher", publisher);
            System.out.println("Publisher bound");
        } catch (RemoteException | AlreadyBoundException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sets the connection with the database server
     * @return true if connection successful, else false
     */
    public boolean connectToRMIDatabaseServer()
    {
        // Locate registry at IP address and port number
        try
        {
            dataBaseRegistry = LocateRegistry.getRegistry(ipAddressDB, portNumber);
        } catch (RemoteException ex)
        {
            System.out.println("Client: Cannot locate registry");
            System.out.println("Client: RemoteException: " + ex.getMessage());
            dataBaseRegistry = null;
        }

        // Bind student administration using registry
        if (dataBaseRegistry != null)
        {
            try
            {
                database = (IPersistencyMediator) dataBaseRegistry.lookup(bindingName);
                connectedToDatabase = true;
                System.out.println("Client: connection with " + bindingName + " successful");
            } catch (RemoteException ex)
            {
                System.out.println("Client: Cannot bind Database");
                System.out.println("Client: RemoteException: " + ex.getMessage());
                database = null;
                connectedToDatabase = false;
            } catch (NotBoundException ex)
            {
                System.out.println("Client: Cannot bind Database");
                System.out.println("Client: NotBoundException: " + ex.getMessage());
                database = null;
                connectedToDatabase = false;
            }
        }
        return connectedToDatabase;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server();
    }
}
