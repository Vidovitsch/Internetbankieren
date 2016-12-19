/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
        // Print port number for registry
        System.out.println("DatabaseServer: Port number " + portNumber);

        // Create student administration
        try {
            database = new DatabaseMediator();
            System.out.println("DatabaseServer: Database created");
        } catch (RemoteException ex) {
            System.out.println("DatabaseServer: Cannot create Database");
            System.out.println("DatabaseServer: RemoteException: " + ex.getMessage());
            database = null;
        }

        // Create registry at port number
        try {
            registry = LocateRegistry.createRegistry(portNumber);
            System.out.println("DatabaseServer: Registry created on port number " + portNumber);
        } catch (RemoteException ex) {
            System.out.println("DatabaseServer: Cannot create registry");
            System.out.println("DatabaseServer: RemoteException: " + ex.getMessage());
            registry = null;
        }

        // Bind student administration using registry
        try {
            registry.rebind(bindingName, database);
        } catch (RemoteException ex) {
            System.out.println("DatabaseServer: Cannot bind Databse");
            System.out.println("DatabaseServer: RemoteException: " + ex.getMessage());
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        DatabaseServer d = new DatabaseServer();
    }
    
}
