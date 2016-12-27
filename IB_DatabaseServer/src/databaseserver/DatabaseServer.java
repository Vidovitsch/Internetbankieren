package databaseserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Michiel van Eijkeren
 */
public class DatabaseServer
{    
    // Set port number
    private static final int portNumber = 1088;

    // Set binding name for student administration
    private static final String bindingName = "Database";

    // References to registry and student administration
    private Registry registry = null;
    private DatabaseMediator database = null;

    // Constructor
    public DatabaseServer()
    {
        // Create student administration
        try {
            database = new DatabaseMediator();
        } catch (RemoteException ex) {
            database = null;
        }

        // Create registry at port number
        try {
            registry = LocateRegistry.createRegistry(portNumber);
        } catch (RemoteException ex) {
            registry = null;
        }

        // Bind student administration using registry
        try {
            registry.rebind(bindingName, database);
        } catch (RemoteException ex) { }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        DatabaseServer d = new DatabaseServer();
    }
    
}
