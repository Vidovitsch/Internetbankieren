package Server;

import Models.Centrale;
import Shared_Data.IPersistencyMediator;
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
public final class CentraleServer
{
    private final String ipAddressDB = "145.93.177.68";
    private final int portNumber = 1088;
    private static final String bindingName = "Database";
    private IPersistencyMediator database = null;
    private boolean connectedToDatabase = false;
    
    private Registry centraleRegistry;
    private Registry dataBaseRegistry;
    private Centrale centrale;
    
    /**
     * Handles all RMI-based processes.
     */
    public CentraleServer() {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            
            centraleRegistry = LocateRegistry.createRegistry(1100);
            System.out.println("Centrale registry created");
            
            setCentraleRegistryBinds();
            connectedToDatabase = connectToRMIDatabaseServer();
            centrale.setPersistencyMediator(database);
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            Logger.getLogger(CentraleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sets the binds of the centraleRegistry.
     */
    private void setCentraleRegistryBinds() {
        try {
            centrale = new Centrale();
            centraleRegistry.bind("centrale", centrale);
            System.out.println("Centrale bound");
        } catch (RemoteException | AlreadyBoundException ex)
        {
            Logger.getLogger(CentraleServer.class.getName()).log(Level.SEVERE, null, ex);
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
        CentraleServer server = new CentraleServer();
    }
}
