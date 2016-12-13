package Server;

import Models.Centrale;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class CentraleServer
{
    private Registry centraleRegistry;
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CentraleServer server = new CentraleServer();
    }
}
