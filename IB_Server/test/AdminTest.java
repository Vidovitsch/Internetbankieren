import Exceptions.LoginException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Models.Administratie;
import Models.Bank;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Client.IBank;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import fontyspublisher.RemotePublisher;
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

//Test coverage:
//
//Login functionality
//Register functionality
//Logout functionality
//Remove client functionality
//GetBank inspection method
//Session check functionality
//GetKlanByUsername functionality
//Publish transaction functionality
//AddBank functionality

//Zet de centrale en de database aan voor deze tests
public class AdminTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private Administratie admin;
    private ICentrale centrale;
    
    private Klant dummyKlant1;
    private Klant dummyKlant2;
    
    public AdminTest() {
        try {
            centrale = new ICentrale() {
                @Override
                public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value, String description) throws RemoteException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                public ArrayList<String> getTransactions(String IBAN) throws RemoteException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            Registry dataBaseRegistry = LocateRegistry.getRegistry(ipAddressDB, portNumber);
            IPersistencyMediator database = (IPersistencyMediator) dataBaseRegistry.lookup(bindingName);
            admin = new Administratie(centrale, new RemotePublisher());
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
        try {
            //Register two dummyUsers
            dummyKlant1 = admin.register("DummyUser", "DummyUser", "123456789");
            dummyKlant2 = admin.register("DummyUser2", "DummyUser2", "123456789");
        } catch (RegisterException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        try {
            //Remove dummyUsers from the database
            admin.removeKlant("DummyUser", "DummyUser", "123456789");
            admin.removeKlant("DummyUser2", "DummyUser2", "123456789");
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            Klant klant = admin.register("DummyUser3", "DummyUser3", "123456789");
            admin.logout(klant);
            admin.login("DummyUser3", "DummyUser3", "123456789");
            assertEquals("Username is DummyUser3DummyUser3", "DummyUser3DummyUser3", klant.getUsername());
            assertTrue("This client has a valid session", admin.checkSession(klant.getUsername()));
            admin.removeKlant("DummyUser3", "DummyUser3", "123456789");
        } catch (LoginException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (RegisterException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if the account has already a session running
    @Test
    public void checkSessionRunningOnLogin() {
        try {
            admin.login("DummyUser", "DummyUser", "123456789");
            fail();
        } catch (LoginException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (IllegalArgumentException | RemoteException ex) {
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
    public void validKlantAndSessionOnRegisterTest() {
        try {
            Klant klant = admin.register("RegisterTest", "RegisterTest", "123456789");
            assertEquals("Username is RegisterTestRegisterTest", "RegisterTestRegisterTest", klant.getUsername());
            assertTrue("This client has a valid session", admin.checkSession(klant.getUsername()));
            admin.removeKlant("RegisterTest", "RegisterTest", "123456789");
        } catch (IllegalArgumentException | RemoteException | RegisterException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests if a user can logout after register
    @Test
    public void logoutAfterRegisterTest () {
            /**
            * Logs out a user.
            * Gets called when a user wants to log out, or a session has expired.
            * @param klant
            * @throws RemoteException
            */
        try {
            Klant klant = admin.register("RegisterTest", "RegisterTest", "123456789");
            assertTrue("This client has a valid session", admin.checkSession(klant.getUsername()));
            admin.logout(klant);
            assertFalse("This client has no session", admin.checkSession(klant.getUsername()));
            admin.removeKlant("RegisterTest", "RegisterTest", "123456789");
        } catch (RemoteException | RegisterException | IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a user can logout after login
    @Test
    public void logoutAfterLoginTest() {
     try {  
            assertTrue("This client has a valid session", admin.checkSession(dummyKlant1.getUsername()));
            admin.logout(dummyKlant1);
            assertFalse("This client has no session", admin.checkSession(dummyKlant1.getUsername()));
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a user can logout after putting a false user
    @Test
    public void logoutWithFalseKlant() {
        try {
            Klant klant = new Klant("Somebody", "Anywhere");
            assertFalse("klant has no running session", admin.checkSession(klant.getUsername()));
            admin.logout(klant);
            assertFalse("klant has again no running session", admin.checkSession(klant.getUsername()));
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a client gets removed correctly
    @Test
    public void removeValidClientTest() {
            /**
            * Removes a user from the administration. All data gets lost.
            * @param username
            * @param residence
            * @param password
            * @return True if succesful, else false.
            * @throws RemoteException
            */
        try {
            boolean value = admin.removeKlant("DummyUser", "DummyUser", "123456789");
            assertTrue("This user has been removed correctly", value);
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a client gets remove correctly without a valid name
    @Test
    public void removeClientUnvalidNameTest() {
        try {
            boolean value = admin.removeKlant("D", "DummyUser", "123456789");
            assertFalse("This user has not been removed correctly", value);
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a client gets remove correctly without a valid residence
    @Test
    public void removeClientUnvalidResidenceTest() {
        try {
            boolean value = admin.removeKlant("DummyUser", "D", "123456789");
            assertFalse("This user has not been removed correctly", value);
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a client gets remove correctly without a valid password
    @Test
    public void removeClientUnvalidPasswordTest() {
        try {
            boolean value = admin.removeKlant("DummyUser", "DummyUser", "1");
            assertFalse("This user has not been removed correctly", value);
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a klant gets the correct bank
    @Test
    public void getBankTest() {
        try {
            /**
             * Returns the subscribed bank of the user.
             * @param klant
             * @return IBank, null if the user has no bank.
             * @throws RemoteException
             */
            //Because we only use one bank within our concept. The returned bank
            //is alway RABOBAN.
            assertEquals("The bank name is RABOBANK", "RABOBANK", admin.getBank(dummyKlant1).getName());
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a logged klant has a running session
    @Test
    public void checkSessionLoggedKlantTest() {
        /**
        * Checks if a client has still a session running.
        * @param username
        * @return True if session is running, else false.
        */
        assertTrue("DummyKlant1 has a running session", admin.checkSession(dummyKlant1.getUsername()));
    }
    
    //Tests if a logged klant has no running session
    @Test
    public void checkSessionLoggedOutKlantTest() {
        try {
            admin.logout(dummyKlant1);
            assertFalse("DummyKlant1 has no running session", admin.checkSession(dummyKlant1.getUsername()));
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a correct klant is returned after putting a incorrect username
    @Test
    public void getKlantByFalseUsername() {
        /**
        * Returns a klant object by a given username
        * @param username
        * @return Klant
        */
        Klant klant = admin.getKlantByUsername("Somebody");
        assertEquals("This klant doesn't exist", null, klant);
    }
    
    //Tests if a correct klant is returned after putting a correct username
    @Test
    public void getKlantByValidUsername() {
        String username = dummyKlant1.getUsername();
        Klant klant = admin.getKlantByUsername(username);
        assertEquals("This klant does exist", klant.getUsername(), username);
    }
    
    //Tests publish method with 2 users with running sessions
    @Test
    public void publishTwoSessions() {
        try {
            /**
            * Publishes the updated list of transactions and bankaccounts to the
            * involved users with a running session.
            * @param usernameTo
            * @param usernameFrom
            * @param bank
            * @return true if succesfull, else false.
            * @throws RemoteException
            */
            String username1 = dummyKlant1.getUsername();
            String username2 = dummyKlant2.getUsername();
            //Ervan uitgaande dat er maar één bank in het spel is
            IBank bank = admin.getBank(dummyKlant1);
            assertTrue("Both users have a running session", admin.publishTransaction(username1, username2, (Bank) bank));
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests publish method with 2 users with no running session
    @Test
    public void publishNoSessions() {
        try {
            String username1 = dummyKlant1.getUsername();
            String username2 = dummyKlant2.getUsername();
            //Ervan uitgaande dat er maar één bank in het spel is
            IBank bank = admin.getBank(dummyKlant1);
            admin.logout(dummyKlant1);
            admin.logout(dummyKlant2);
            assertTrue("Both users have no running session", admin.publishTransaction(username1, username2, (Bank) bank));
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests publish method with 2 users with 1 running an 1 not running session
    @Test
    public void publishOneSession1() {
        try {
            String username1 = dummyKlant1.getUsername();
            String username2 = dummyKlant2.getUsername();
            //Ervan uitgaande dat er maar één bank in het spel is
            IBank bank = admin.getBank(dummyKlant1);
            admin.logout(dummyKlant1);
            assertTrue("Both users have no running session", admin.publishTransaction(username1, username2, (Bank) bank));
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests publish method with 2 users with 1 not running an 1 running session
    @Test
    public void publishOneSession2() {
        try {
            String username1 = dummyKlant1.getUsername();
            String username2 = dummyKlant2.getUsername();
            //Ervan uitgaande dat er maar één bank in het spel is
            IBank bank = admin.getBank(dummyKlant1);
            admin.logout(dummyKlant2);
            assertTrue("Both users have no running session", admin.publishTransaction(username1, username2, (Bank) bank));
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
    //Tests if a new bank is correctly added to the collection
    @Test
    public void addValidBankTest() {
            /**
            * This method only gets called if there are more banks involved within this concept.
            * @param bank
            * @return
            * @throws java.rmi.RemoteException
            */
        try {
            Bank dummyBank = new Bank("ING", "ING", admin, centrale);
            assertTrue("Bank is added succesfully", admin.addBank(dummyBank));
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if a existing bank is not correctly added to the collection
    @Test
    public void addExistingBankTest() {
        try {
            assertFalse("Bank is not added succesfully", admin.addBank((Bank) admin.getBank(dummyKlant1)));
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
