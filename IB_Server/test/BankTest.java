import Exceptions.SessionExpiredException;
import Models.Administratie;
import Models.Bank;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import java.rmi.RemoteException;
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
public class BankTest {
    
    private Bank bank;
    private Administratie admin;
    
    public BankTest() {
         try {
            //Aanmaken van een dummy centrale
            ICentrale centrale = new ICentrale() {
                @Override
                public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value) throws RemoteException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void getTransactions(String IBAN) throws RemoteException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            //Aanamken van eeen dummy administratie
            admin = new Administratie(centrale);
            bank = new Bank("Rabobank", admin);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
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

    //Tests the constructor of the Bank class
    @Test
    public void testConstuctor() {
        /**
        * A bank registered at the administration.
        * @param name of the bank, if null IllegalArgumentException
        * @throws RemoteException, IllegalArgumentExcpetion
        */
        
        //Name is correct and not empty
        String name = "Rabobank";
        try {
            Bank bankTest = new Bank(name, admin);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Name is incorrect and empty
        name = "";
        try {
            try {
                Bank bankTest = new Bank(name, admin);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Administratie is incorrect and null
        name = "Rabobank";
        try {
            try {
                Bank bankTest = new Bank(name, null);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
    }
    
    //Tests the inspection method getGame of the Bank class
    @Test
    public void testGetName() {
        /**
        * Returns the name of this bank.
        * @return Name of this bank.
        * @throws RemoteException
        */
        
        try {
            assertEquals("This bank's name is 'Rabobank'", bank.getName(), "Rabobank");
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests if adding accounts goes succesful
    @Test
    public void testAddAccount() {
        /**
        * Adds a bank account for a user.
        * If session is over, SessionExpiredException.
        * @param klant, if empty throws IllegalArgumentException.
        * @return A String representing the new bank account.
        * @throws Exceptions.SessionExpiredException
        * @throws RemoteException
        */
        
        //Making a dummy client without no session
        Klant dummyNoSession = new Klant("Dummy1", "Dummy1");
        
        //Making a dummy client with a session
        Klant dummySession = null;
        try {
            dummyNoSession = admin.login("TestDummy", "TesetDummy", "TestDummy");
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Adding an account to bank with an invalid client
        try {
            try {
                bank.addAccount(null);           
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            } catch (SessionExpiredException ex) {
                assertEquals("An unexpected exception has been thrown", 1, 0);
                Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (RemoteException ex) {
            assertEquals("An unexpected exception has been thrown", 1, 0);
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Adding an account to bank with an expired session
        try {
            try {
                bank.addAccount(dummyNoSession);
            }
            catch (SessionExpiredException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Adding an account to bank with a valid client
        try {
            try {
                bank.addAccount(dummySession);
                assertEquals("dummySession has 1 bank account", bank.getAccounts(dummySession).size(), 1);
                assertEquals("No exception has been thrown", 0, 0);
            }
            catch (SessionExpiredException ex) {
                assertEquals("An unexpected exception has been thrown", 1, 0);
            }
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
    }
    
    //Tests if removing accounts goes succesful
    @Test
    public void testRemoveAccount() {
        /**
        * Removes a account from a user.
        * If session is over, SessionExpiredException.
        * @param IBAN representing the bank account, if empty or non-existing throws IllegalArgumentException..
        * @return True if succesful, else false.
        * @throws Exceptions.SessionExpiredException
        * @throws RemoteException
        */
        
        //Making a dummy client without no session
        Klant dummyNoSession = new Klant("Dummy1", "Dummy1");
        try {
            bank.addAccount(dummyNoSession);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Making a dummy client with a session
        Klant dummySession = null;
        try {
            dummySession = admin.login("TestDummy1", "TesetDummy1", "TestDummy1");
            bank.addAccount(dummySession);
        } catch (IllegalArgumentException | RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Removing an account from bank with an invalid IBAN
        try {
            try {
                bank.removeAccount("daflbhoieawofj", dummySession);           
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            assertEquals("An unexpected exception has been thrown", 1, 0);
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Removing an account from bank with an expired session
        try {
            try {
                bank.removeAccount("TestIBAN001", dummyNoSession);
            }
            catch (SessionExpiredException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Removing an account from bank with an invalid client
        try {
            try {
                bank.removeAccount("TestIBAN", null);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Removing an account from bank with a valid client and account
        try {
            bank.removeAccount("TestIBAN", dummySession);
            assertEquals("dummySession has 0 bank accounts", bank.getAccounts(dummySession).size(), 0);
            assertEquals("No exception has been thrown", 0, 0);
        } catch (RemoteException | SessionExpiredException | IllegalArgumentException ex ) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
    }
}
