package ib_server;

import Models.Administratie;
import Models.Bank;
import Shared_Centrale.ICentrale;
import Shared_Client.IAdmin;
import Shared_Client.IBank;
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
    public IRemotePublisherForDomain publisher;
    private Registry centraleRegistry;
    private Registry serverRegistry;
    private ICentrale centrale;
    private Administratie admin;
    
    /**
     * Handles all RMI-based processes.
     */
    public Server() {
         try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            serverRegistry = LocateRegistry.createRegistry(1099);
            setCentraleRegistryBinds();
            getCentraleRegistryBinds();
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
            publisher = new RemotePublisher();
            Bank bank = new Bank();
            admin = new Administratie(centrale, bank);
            
            serverRegistry.bind("admin", (IAdmin) admin);
            System.out.println("Centrale bound");
            
            serverRegistry.bind("bank", (IBank) bank);
            System.out.println("Bank bound");
            
            serverRegistry.bind("serverPublisher", publisher);
            System.out.println("Publisher bound");
        } catch (RemoteException | AlreadyBoundException ex)
        {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server();
    }
}
