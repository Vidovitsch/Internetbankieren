import Exceptions.LoginException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Models.Administratie;
import Models.Bank;
import Models.Bankrekening;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Client.IBank;
import Shared_Client.Klant;
import Shared_Data.IPersistencyMediator;
import fontyspublisher.RemotePublisher;
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

//Test coverage:
//
//GetName inspection method
//GetShortName inspection method
//GetAccounts inspection method
//GetTransactions inspection method
//

//Zet de centrale en de database aan voor deze tests
public class BankTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private Klant dummyKlant1;
    private Klant dummyKlant2;
    private Administratie admin;
    private ICentrale centrale;
    private IBank bank;
    private IPersistencyMediator database;
    
    public BankTest() {
        try {
            Registry centraleRegistry = LocateRegistry.getRegistry("localhost", 1100);
            centrale = (ICentrale) centraleRegistry.lookup("centrale");
            Registry dataBaseRegistry = LocateRegistry.getRegistry(ipAddressDB, portNumber);
            database = (IPersistencyMediator) dataBaseRegistry.lookup(bindingName);
            admin = new Administratie(centrale, new RemotePublisher());
            admin.setPersistencyMediator(database);
        } catch (RemoteException | NotBoundException ex) {
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
        try {
            //Register two dummyUsers and a bank
            dummyKlant1 = admin.register("DummyUser", "DummyUser", "123456789");
            bank = admin.getBank(dummyKlant1);
            dummyKlant2 = admin.register("DummyUser2", "DummyUser2", "123456789");
            dummyKlant2.addBankAccount(bank);
            dummyKlant1.addBankAccount(bank);
        } catch (RegisterException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        try {
            //Remove both dummyUsers from the database
            admin.removeKlant("DummyUser", "DummyUser", "123456789");
            admin.removeKlant("DummyUser2", "DummyUser2", "123456789");
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests concerning the interface IBank, implemented by the class Bank
    
    //Tests the inspection method getName
    @Test
    public void getNameTest() {
            /**
            * Returns the name of this bank.
            * @return Name of this bank.
            * @throws RemoteException
            */
        try {
            Bank testBank = new Bank("TestBank", "TB", admin, centrale);
            assertEquals("TestBank's name is TestBank", testBank.getName(), "TestBank");
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the inspection method getShortName
    @Test
    public void getShortNameTest() {
        /**
        * Returns the short name of this bank
        * @return Short name of this bank.
        * @throws RemoteException 
        */
        try {
            Bank testBank = new Bank("TestBank", "TB", admin, centrale);
            assertEquals("TestBank's name is TestBank", testBank.getShortName(), "TB");
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the inspection method getAccounts with a valid klant with a valid session
    @Test
    public void getBankAccountsValidKlantTest() {
        /**
        * Returns a list of Strings. Every String is
        * representing a bank account.
        * @param klant, if null throws IllegalArgumentException.
        * @return A list of Strings representing a bank account.
        * @throws Exceptions.SessionExpiredException
        * @throws RemoteException
        */
        
        //IBAN is random generated, so it can't be tested
        try {
            ArrayList<String> bankAccounts = bank.getAccounts(dummyKlant1);
            Bankrekening rekening = stringToBankrekening(bankAccounts.get(0));
            //Test list-size
            assertEquals("DummyKlant1 has 1 bank account", bankAccounts.size(), 1);
            //Test user
            assertEquals("Linked username is DummyUserDummyUser", rekening.getKlant().getUsername(), "DummyUserDummyUser");
            //Test balance
            assertEquals("The balance is zero", bankAccounts.get(0).split(";")[1], "0.0");
        } catch (RemoteException | SessionExpiredException | IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the inspection method getAccounts with a null klant
    @Test
    public void getBankAccountsNullKlantTest() {
        try {
            Klant klant = null;
            bank.getAccounts(klant);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the inspection method getAccounts with a logged-out klant
    @Test
    public void getBankAccountsNoSessionKlantTest() {
        try {
            admin.logout(dummyKlant1);
            bank.getAccounts(dummyKlant1);
            fail();
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the inspection method getTransactions with a valid klant
    @Test
    public void getTransactionsValidKlantTest() {
        /**
        * Returns a list of Strings. Every String is
        * representing a transaction.
        * If session is over, SessionExpiredException.
        * @param IBAN, if empty throws IllegalArgumentException.
        * @param klant
        * @return A list of Strings representing a transaction.
        * @throws Exceptions.SessionExpiredException
        * @throws RemoteException
        */
        try {
            //Get IBAN2 from dummyKlant2
            String IBAN2 = dummyKlant2.getBankAccounts(bank).get(0).split(";")[0];
            //Get IBAN1 from dummyKlant1
            String IBAN1 = dummyKlant1.getBankAccounts(bank).get(0).split(";")[0];
            //Make a transaction
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1.0, "iets", bank);
            //Get transaction
            ArrayList<String> transactions = bank.getTransactions(IBAN1, dummyKlant1);
            //Test list-size
            assertEquals("Transacions' list size is 1", transactions.size(), 1);
            //Test IBANTo
            assertEquals(IBAN2, transactions.get(0).split(";")[2]);
            //Test IBANFrom
            assertEquals(IBAN1, transactions.get(0).split(";")[3]);
            //Test amount transferred
            assertEquals("1 euro has been transferred", transactions.get(0).split(";")[1], "1.0");
            //Test description
            assertEquals("Description is 'iets'", transactions.get(0).split(";")[4], "iets");
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the inspection method getTransactions with an empty IBAN
    @Test
    public void getTransactionsEmptyIBANTest() {
        try {
            String IBAN = "";
            bank.getTransactions(IBAN, dummyKlant1);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the inspection method getTransactions with a null klant
    @Test
    public void getTransactionsNullKlantTest() {
        try {
            //Get IBAN from dummyKlant1
            String IBAN = dummyKlant1.getBankAccounts(bank).get(0).split(";")[0];
            
            Klant klant = null;
            bank.getTransactions(IBAN, klant);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the inspectionmethod getTransactions with a logged-out klant
    @Test
    public void getTransactionsNoSessionTest() {
        try {
            //Get IBAN from dummyKlant1
            String IBAN = dummyKlant1.getBankAccounts(bank).get(0).split(";")[0];
            
            admin.logout(dummyKlant1);
            bank.getTransactions(IBAN, dummyKlant1);
            fail();
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the inspection method getTransactions with a invalid IBAN
    @Test
    public void getTransactionsInvalidIBANTest() {
        try {
            //Get IBAN from dummyKlant2
            String IBAN = dummyKlant2.getBankAccounts(bank).get(0).split(";")[0];
            
            bank.getTransactions(IBAN, dummyKlant1);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method addBankAccount with a valid klant
    @Test
    public void addBankAccountValidKlantTest() {
            /**
            * Adds a bank account for a user.
            * If session is over, SessionExpiredException.
            * @param klant, if empty throws IllegalArgumentException.
            * @throws Exceptions.SessionExpiredException
            * @throws RemoteException
            */
        try {
            ArrayList<String> accounts = dummyKlant1.getBankAccounts(bank);
            //Test list-size before
            assertEquals("The list-size is 1", accounts.size(), 1);
            //Add a new account
            bank.addBankAccount(dummyKlant1);
            accounts = dummyKlant1.getBankAccounts(bank);
            //Test list-size after
            assertEquals("The list-size is 2", accounts.size(), 2);
            //Test correct linked username
            Bankrekening account = stringToBankrekening(accounts.get(1));
            String username = account.getKlant().getUsername();
            assertEquals("The linked username is DummyUserDummyUser", username, "DummyUserDummyUser");
            //Remove bank account
            dummyKlant1.removeBankAccount(account.toString().split(";")[0], bank);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method addBankAccount with a null klant
    @Test
    public void addBankAccountNullKlantTest() {
        try {
            Klant klant = null;
            bank.addBankAccount(klant);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method addBankAccount with logged-out user
    @Test
    public void addBankAccountNoSession() {
        try {
            admin.logout(dummyKlant1);
            bank.addBankAccount(dummyKlant1);
            fail();
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Help methods
    
    /**
     * Converts the String of klant-values to a Klant object.
     * Als de klant een geldige sessie heeft draaien wordt er ook
     * voor deze klant een sessie gestart.
     * @param values
     * @return Bankrekening
     */
    private Bankrekening stringToBankrekening(String values) throws RemoteException {
        String[] rFields = values.split(";");
        String[] klantFields = database.getKlantByID(Integer.valueOf(rFields[1])).split(";");
        String username = klantFields[0] + klantFields[1];
        Klant klant = admin.getKlantByUsername(username);
        Bankrekening rekening = new Bankrekening(rFields[0], klant);
        return rekening; 
    }
}
