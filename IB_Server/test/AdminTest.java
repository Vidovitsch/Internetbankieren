import Exceptions.LoginException;
import Exceptions.RegisterException;
import Models.Administratie;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David
 */

//Zet de centrale en de database aan voor deze tests
public class AdminTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private Administratie admin;
    
    public AdminTest() {
        try {
            Registry dataBaseRegistry = LocateRegistry.getRegistry(ipAddressDB, portNumber);
            IPersistencyMediator database = (IPersistencyMediator) dataBaseRegistry.lookup(bindingName);
            admin = new Administratie(new ICentrale() {
                @Override
                public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws RemoteException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                public ArrayList<String> getTransactions(String IBAN) throws RemoteException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            admin.setPersistencyMediator(database);
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    //Tests the exception thrown when trying to login with an empty name
    @Test
    public void emptyNameLoginTest() {
            /**
             * Logs in a user and creating a session if succesful.
             * @param naam
             * @param woonplaats
             * @param password not null, else IllegalArgumentException.
             * @return Klant object if succesfull, else null.
             * @throws Exceptions.LoginException
             * @throws IllegalArgumentException
             * @throws RemoteException
             */
        try {
            admin.login("", "woonplaats", "wachtwoord");
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (LoginException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the exception thrown when trying to login with an empty residence
    @Test
    public void emptyResidenceLoginTest() {
        try {
            admin.login("naam", "", "wachtwoord");
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (LoginException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the exception thrown when trying to login with an empty password
    @Test
    public void emptyPasswordLoginTest() {
        try {
            admin.login("naam", "woonplaats", "");
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (LoginException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the exception thrown when trying to login with an invalid username
    @Test
    public void invalidUsernameLoginTest() {
        try {
            admin.login("DummyUser", "InvalidUser", "123456789");
            fail();
        } catch (LoginException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the exception thrown when trying to login with an invalid username
    @Test
    public void invalidPasswordLoginTest() {
        try {
            admin.login("DummyUser", "DummyUser", "InvalidPassword");
            fail();
        } catch (LoginException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests if the client gets a valid Klant object and session after login
    @Test
    public void validKlantAndSessionOnLogin() {
        try {
            Klant klant = admin.login("DummyUser", "DummyUser", "123456789");
            assertEquals("Username is DummyUserDummyUser", "DummyUserDummyUser", klant.getUsername());
            assertTrue("This client has a valid session", admin.checkSession(klant));
        } catch (LoginException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    
    //Tests the exception thrown when trying to register with an empty name
    @Test
    public void emptyNameRegisterTest() {
            /**
            * Registers a user at the administration.
            * @param naam
            * @param woonplaats
            * @param password not null or size bigger than 7, else IllegalArgumentException.
            * @return Klant object if succesfull, else null.
            * @throws Exceptions.RegisterException
            * @throws IllegalArgumentException
            * @throws RemoteException 
            */
        try {
            admin.register("", "RegisterTest", "RegisterTest");
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RemoteException | RegisterException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the exception thrown when trying to register with an empty residence
    @Test
    public void emptyResidenceRegisterTest() {
        try {
            admin.register("RegisterTest", "", "RegisterTest");
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RegisterException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the exception thrown when trying to register with an empty password
    @Test
    public void emptyPasswordRegisterTest() {
        try {
            admin.register("RegisterTest", "RegisterTest", "");
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RegisterException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    
    //Tests the exception thrown when trying to register with an password smaller than 9 characters
    @Test
    public void invalidPasswordRegisterTest() {
        try {
            admin.register("RegisterTest", "RegisterTest", "abcd");
            fail();
        } catch (RegisterException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the exception thrown when trying to register with an existing username
    @Test
    public void existingUsernameRegisterTest() {
        try {
            admin.register("DummyUser", "DummyUser", "123456789");
            fail();
        } catch (RegisterException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests if the client gets a valid Klant object and session after register
    @Test
    public void validKlantAndSessionOnRegister() {
        try {
            Klant klant = admin.register("RegisterTest", "RegisterTest", "123456789");
            assertEquals("Username is RegisterTestRegisterTest", "RegisterTestRegisterTest", klant.getUsername());
            assertTrue("This client has a valid session", admin.checkSession(klant));
            admin.removeKlant("RegisterTest", "RegisterTest", "123456789");
        } catch (IllegalArgumentException | RemoteException | RegisterException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
