package Server;

import Models.Centrale;
import Shared_Centrale.ICentrale;
import Shared_Centrale.IAdminCheck;
import Shared_Centrale.IBankTrans;
import java.rmi.AccessException;
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
public class CentraleServer
{
    private Registry centraleRegistry;
    private Registry serverRegistry;
    private IAdminCheck adminCheck;
    private IBankTrans bank;
    private Centrale centrale;
    
    /**
     * Handles all RMI-based processes.
     */
    public CentraleServer() {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            centraleRegistry = LocateRegistry.createRegistry(1100);
            getServerRegistryBinds();
            setCentraleRegistryBinds();
        } catch (RemoteException ex) {
            System.out.println("Server: Cannot create registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            Logger.getLogger(CentraleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Gets the registry of the Server and its binds.
     */
    private void getServerRegistryBinds() {
        try {
            serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
            adminCheck = (IAdminCheck) serverRegistry.lookup("adminCheck");
            bank = (IBankTrans) serverRegistry.lookup("bankTrans");
        } catch (RemoteException | NotBoundException ex)
        {
            System.out.println("Server: Cannot locate registry");
            System.out.println("Server: RemoteException: " + ex.getMessage());
            Logger.getLogger(CentraleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sets the binds of the centraleRegistry.
     */
    private void setCentraleRegistryBinds() {
        try {
            centrale = new Centrale(adminCheck, bank);
            centraleRegistry.bind("centrale", centrale);
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
