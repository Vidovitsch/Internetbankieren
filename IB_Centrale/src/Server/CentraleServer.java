package Server;

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
    
    public CentraleServer() {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            centraleRegistry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            Logger.getLogger(CentraleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setCentraleRegistryBinds() {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CentraleServer server = new CentraleServer();
    }
    
}
