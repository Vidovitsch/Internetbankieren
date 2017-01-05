/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Exceptions.LimitReachedException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Models.Administratie;
import Models.Bankrekening;
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

//Test Coverage:
//
//addBankAccount functionality
//removeBankAccount functionality
//GetUserName inspection method
//GetName inspection method
//GetResidence inspection method
//GetBankAccounts inspection method
//GetTransactions inspection method

//Zet de centrale en de database aan voor deze tests
//Almost all methods tested here are the same as in BankTest
//Only difference is that an object of Bank has been replaced by an object of Klant
//Note: Class Klant is used as a link between client and server.
public class KlantTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private Klant dummyKlant1;
    private Klant dummyKlant2;
    private Administratie admin;
    private ICentrale centrale;
    private IBank bank;
    private IPersistencyMediator database;
    
    public KlantTest() {
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
            //Register a dummyUsers and a bank
            dummyKlant1 = admin.register("DummyUser", "DummyUser", "123456789");
            bank = admin.getBank(dummyKlant1);
            dummyKlant2 = admin.register("DummyUser2", "DummyUser2", "123456789");
            dummyKlant1.addBankAccount(bank);
            dummyKlant2.addBankAccount(bank);
        } catch (RegisterException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        try {
            //Remove the dummyUsers from the database
            admin.removeKlant("DummyUser", "DummyUser", "123456789");
            admin.removeKlant("DummyUser2", "DummyUser2", "123456789");
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void addBankAccountValidKlantTest() {
           /**
            * Adds a bank account on the server linked with this Client.
            * @param bank
            * @throws Exceptions.SessionExpiredException
            * @throws java.rmi.RemoteException
            */
        try {
            ArrayList<String> accounts = dummyKlant1.getBankAccounts(bank);
            //Test list-size before
            assertEquals("The list-size is 1", accounts.size(), 1);
            //Add a new account
            dummyKlant1.addBankAccount(bank);
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
    
    //Tests the method addBankAccount with logged-out user
    @Test
    public void addBankAccountNoSession() {
        try {
            admin.logout(dummyKlant1);
            dummyKlant1.addBankAccount(bank);
            fail();
        } catch (RemoteException | IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method removeBankAccount with a valid Klant and IBAN
    @Test
    public void removeBankAccountValidKlant() {
            /**
            * Removes a bank account on the server linked with this Client.
            * @param IBAN
            * @param bank
            * @return True if succesful, else false
            * @throws Exceptions.SessionExpiredException
            * @throws java.rmi.RemoteException
            */
        try{
            //Add a bank account
            dummyKlant1.addBankAccount(bank);
            //Get list of bank accounts (String-value)
            ArrayList<String> accounts = dummyKlant1.getBankAccounts(bank);
            //Get IBAN of both bank accounts
            String IBAN1 = accounts.get(0).split(";")[0];
            String IBAN2 = accounts.get(1).split(";")[0];
            //Test list-size before
            assertEquals("list-size before is 2", accounts.size(), 2);
            //Remove bank account
            dummyKlant1.removeBankAccount(IBAN2, bank);
            //Test list-size after
            accounts = dummyKlant1.getBankAccounts(bank);
            assertEquals("list-size after is 1", accounts.size(), 1);
            //Test correct IBAN is removed
            assertEquals("IBAN2 has been removed", IBAN1, accounts.get(0).split(";")[0]);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method removeBankAccount with an empty IBAN
    @Test
    public void removeBankAccountEmptyIBAN() {
        try {
            String IBAN = "";
            dummyKlant1.removeBankAccount(IBAN, bank);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method removeBankAccount with invalid session
    @Test
    public void removeBankAccountNoSession() {
        try {
            //Get list of bank accounts (String-value)
            ArrayList<String> accounts = dummyKlant1.getBankAccounts(bank);
            //Get IBAN of both bank accounts
            String IBAN = accounts.get(0).split(";")[0];
            //Remove account
            admin.logout(dummyKlant1);
            dummyKlant1.removeBankAccount(IBAN, bank);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method removeBankAccount with false IBAN property
    @Test
    public void removeBankAccountFalseProperty() {
        try {
            String IBAN = "NL98RABO047329105";
            dummyKlant1.removeBankAccount(IBAN, bank);
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the inspection method getUsername
    @Test
    public void getUsernameTest() {
        /**
        * Returns the username of this Client.
        * The username is a combination of this user's name and residence.
        * @return The user's username.
        */
        assertEquals("The username is 'DummyUserDummyUser'", "DummyUserDummyUser", dummyKlant1.getUsername());
    }
    
    //Tests the inspection method getName
    @Test
    public void getNameTest() {
        /**
        * Returns the name of this Client.
        * @return The user's name.
        */
        assertEquals("The username is 'DummyUser'", "DummyUser", dummyKlant1.getName());
    }
    
    //Tests the inspection method getResidence
    @Test
    public void getResidence() {
        /**
        * Returns the current residence of this client.
        * @return The user's current residence.
        */
        assertEquals("This user's residence is 'DummyUser'", "DummyUser", dummyKlant1.getResidence());
    }
    
    //Tests the inspection method getAccounts with a valid klant with a valid session
    @Test
    public void getBankAccountsValidKlantTest() {
        /**
        * Returns a list of Strings. Every String is
        * representing a bank account.
        * @param bank
        * @return A list of Strings representing a bank account.
        * @throws Exceptions.SessionExpiredException
        * @throws java.rmi.RemoteException
        */
        
        //IBAN is random generated, so it can't be tested
        try {
            ArrayList<String> bankAccounts = dummyKlant1.getBankAccounts(bank);
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
    
    //Tests the inspection method getAccounts with a logged-out klant
    @Test
    public void getBankAccountsNoSessionKlantTest() {
        try {
            admin.logout(dummyKlant1);
            dummyKlant1.getBankAccounts(bank);
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
        * @param IBAN
        * @param bank
        * @return A list of Strings representing a transaction.
        * @throws Exceptions.SessionExpiredException
        * @throws java.rmi.RemoteException
        */
        try {
            //Get IBAN2 from dummyKlant2
            String IBAN2 = dummyKlant2.getBankAccounts(bank).get(0).split(";")[0];
            //Get IBAN1 from dummyKlant1
            String IBAN1 = dummyKlant1.getBankAccounts(bank).get(0).split(";")[0];
            //Make a transaction
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1.0, "iets", bank);
            //Get transaction
            ArrayList<String> transactions = dummyKlant1.getTransactions(IBAN1, bank);
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
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the inspection method getTransactions with an empty IBAN
    @Test
    public void getTransactionsEmptyIBANTest() {
        try {
            String IBAN = "";
            dummyKlant1.getTransactions(IBAN, bank);
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
            dummyKlant1.getTransactions(IBAN, bank);
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
            String IBAN = "Invalid IBAN";
            dummyKlant1.getTransactions(IBAN, bank);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method startTransaction with a valid klant
    @Test
    public void startTransactionValidKlantTest() {
            /**
            * Starts a transaction between two bank accounts.
            * If session is over, SessionExpiredException.
            * @param IBAN1 representing the bank account, if empty throws IllegalArgumentException.
            * @param IBAN2 representing the bank account, if empty throws IllegalArgumentException.
            * @param value of the money to be transferred. Has to be greater than 0 or not empty, else IllegalArgumentException.
            * @param description
            * @param bank
            * @return True if succesful, else false.
            * @throws Exceptions.SessionExpiredException
            * @throws RemoteException
            * @throws Exceptions.LimitReachedException
            */
        try {
            //Get both IBANs
            ArrayList<String> accounts1 = dummyKlant1.getBankAccounts(bank);
            ArrayList<String> accounts2 = dummyKlant2.getBankAccounts(bank);
            String IBAN1 = accounts1.get(0).split(";")[0];
            String IBAN2 = accounts2.get(0).split(";")[0];
            //Transactions before
            ArrayList<String> transactions = dummyKlant1.getTransactions(IBAN1, bank);
            assertEquals("List size is 0", transactions.size(), 0);
            //Start transaction
            boolean value = dummyKlant1.startTransaction(IBAN1, IBAN2, 1, "iets", bank);
            assertTrue("Transaction successful", value);
            //Transactions after
            transactions = dummyKlant1.getTransactions(IBAN1, bank);
            assertEquals("List size is 1", transactions.size(), 1);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method startTransaction with a empty IBAN1
    @Test
    public void startTransactionEmptyIBAN1() {
        try {
            String IBAN1 = "";
            String IBAN2 = "1";
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1, "iets", bank);
            fail();
        } catch (SessionExpiredException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method startTransaction with a empty IBAN2
    @Test
    public void startTransactionEmptyIBAN2() {
        try {
            String IBAN1 = "1";
            String IBAN2 = "";
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1, "iets", bank);
            fail();
        } catch (SessionExpiredException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method startTransaction with no session
    @Test
    public void startTransactionNoSession() {
        try {
            //Get both IBANs
            ArrayList<String> accounts1 = dummyKlant1.getBankAccounts(bank);
            ArrayList<String> accounts2 = dummyKlant2.getBankAccounts(bank);
            String IBAN1 = accounts1.get(0).split(";")[0];
            String IBAN2 = accounts2.get(0).split(";")[0];
            //Start transaction
            admin.logout(dummyKlant1);
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1, "iets", bank);
            fail();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (IllegalArgumentException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method startTransaction with a false property
    @Test
    public void startTransactionFalseProperty() {
        try {
            //Get both IBANs
            ArrayList<String> accounts2 = dummyKlant1.getBankAccounts(bank);
            String IBAN1 = "NL11RABO011111111";
            String IBAN2 = accounts2.get(0).split(";")[0];
            //Start transaction
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1, "iets", bank);
            fail();
        } catch (SessionExpiredException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method startTransaction with non-existing IBAN
    @Test
    public void startTransactionNotExisting() {
        try {
            //Get both IBANs
            ArrayList<String> accounts1 = dummyKlant1.getBankAccounts(bank);
            String IBAN1 = accounts1.get(0).split(";")[0];
            String IBAN2 = "NL11RABO011111111";
            //Start transaction
            dummyKlant1.startTransaction(IBAN1, IBAN2, 1, "iets", bank);
            fail();
        } catch (SessionExpiredException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method startTransaction with a zero value
    @Test
    public void startTransactionZeroValue() {
        try {
            //Get both IBANs
            ArrayList<String> accounts1 = dummyKlant1.getBankAccounts(bank);
            ArrayList<String> accounts2 = dummyKlant2.getBankAccounts(bank);
            String IBAN1 = accounts1.get(0).split(";")[0];
            String IBAN2 = accounts2.get(0).split(";")[0];
            //Start transaction
            dummyKlant1.startTransaction(IBAN1, IBAN2, 0, "iets", bank);
            fail();
        } catch (SessionExpiredException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method startTransaction with a negative value
    @Test
    public void startTransactionNegativeValue() {
        try {
            //Get both IBANs
            ArrayList<String> accounts1 = dummyKlant1.getBankAccounts(bank);
            ArrayList<String> accounts2 = dummyKlant2.getBankAccounts(bank);
            String IBAN1 = accounts1.get(0).split(";")[0];
            String IBAN2 = accounts2.get(0).split(";")[0];
            //Start transaction
            dummyKlant1.startTransaction(IBAN1, IBAN2, -1, "iets", bank);
            fail();
        } catch (SessionExpiredException | RemoteException | LimitReachedException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
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
