package ib_client;

import Shared_Client.IAdmin;
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
public class GUIController extends UnicastRemoteObject implements IRemotePropertyListener {

    private GUI gui;
    private IAdmin admin;
    private IRemotePublisherForListener publisher;
    
    /**
     * Handles all RMI-based processes. Updates the GUI
     * @param gui
     * @throws java.rmi.RemoteException
     */
    public GUIController(GUI gui) throws RemoteException {
        try {
            Registry serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
            admin = (IAdmin) serverRegistry.lookup("admin");
            System.out.println("Admin lookup completed");
            
            publisher = (IRemotePublisherForListener) serverRegistry.lookup("serverPublisher");
            System.out.println("Publisher lookup completed");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
