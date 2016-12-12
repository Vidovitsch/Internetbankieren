package ib_client;

import Shared_Client.IAdmin;
import java.rmi.AccessException;
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
public class GUIController {

    private GUI gui;
    private IAdmin admin;
    
    /**
     * Handles all RMI-based processes. Updates the GUI
     * @param gui
     */
    public GUIController(GUI gui) {
        try {
            Registry serverRegistry = LocateRegistry.getRegistry("localhost", 1099);
            admin = (IAdmin) serverRegistry.lookup("admin");
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
