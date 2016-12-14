import Exceptions.SessionExpiredException;
import Models.Administratie;
import Models.Bank;
import Shared_Centrale.IBankTrans;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import java.rmi.RemoteException;
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
public class BankTest {
    
    private Bank bank;
    private Administratie admin;
    //Add Bankrekening b1 = new Bankrekening("TestIBAN001", 0, 0) to the database (linked with klant dummySession)
    //Add Bankrekening b1 = new Bankrekening("TestIBAN002", 0, 0) to the database (linked with klant dummySession)
    
    public BankTest() {
         try {
            //Aanmaken van een dummy centrale
            ICentrale centrale = new ICentrale() {
                @Override
                public void startTransaction(String IBAN1, String IBAN2, IBankTrans bank, double value) throws RemoteException { }
                @Override
                public void getTransactions(String IBAN) throws RemoteException { }
            };
            //Aanamken van eeen dummy administratie en bank
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
                assertEquals("dummySession has 1 bank account", bank.getAccounts(dummySession).size(), 2);
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
    
    //Tests if removing accounts is going succesful
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
                bank.removeAccount("iets", dummySession);           
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
                bank.removeAccount("TestIBAN001", null);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Removing an account from bank with a valid client and account
        try {
            bank.removeAccount("TestIBAN001", dummySession);
            assertEquals("dummySession has 0 bank accounts", bank.getAccounts(dummySession).size(), 1);
            assertEquals("No exception has been thrown", 0, 0);
        } catch (RemoteException | SessionExpiredException | IllegalArgumentException ex ) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
    }
    
    //Test for obtaining all the bank accounts of a user
    @Test
    public void testGetAccounts() {
        /**
        * Returns a list of Strings. Every String is
        * representing a bank account.
        * NoDataFounException when client has no bank accounts.
        * @param klant, if null throws IllegalArgumentException.
        * @return A list of Strings representing a bank account.
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
        Klant dummySession;
        try {
            dummySession = admin.login("TestDummy1", "TesetDummy1", "TestDummy1");
            bank.addAccount(dummySession);
            bank.addAccount(dummySession);
        } catch (IllegalArgumentException | RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Get accounts from an invalid client
        try {
            bank.getAccounts(null);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An expected exception has been thrown", 0, 0);
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Get accounts from a no session client        
        try {
            bank.getAccounts(dummyNoSession);
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An expected exception has been thrown", 0, 0);
        }
        
        //Get accounts from a valid user        
        try {
            assertEquals("This client has 2 bank accounts", bank.getAccounts(dummyNoSession).size(), 2);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Test for obtaining all the transaction of a bank account
    @Test
    public void testGetTransactions() {
        /**
        * Returns a list of Strings. Every String is
        * representing a transaction.
        * If session is over, SessionExpiredException.
        * NoDataFounException when client has no bank transactions.
        * @param IBAN, if empty throws IllegalArgumentException.
        * @param klant
        * @return A list of Strings representing a transaction.
        * @throws Exceptions.SessionExpiredException
        * @throws RemoteException
        */
        
        //Making a dummy client without no session
        Klant dummyNoSession = new Klant("Dummy1", "Dummy1");
       
        //Making a dummy client with a session
        Klant dummySession = null;
        try {
            dummySession = admin.login("TestDummy1", "TesetDummy1", "TestDummy1");
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Getting transactions from a bank account with an invalid IBAN
        try {
            try {
                bank.getTransactions("iets", dummySession);           
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            assertEquals("An unexpected exception has been thrown", 1, 0);
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Getting transactions from a bank account with an expired session
        try {
            try {
                bank.getTransactions("TestIBAN001", dummyNoSession);
            }
            catch (SessionExpiredException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Getting transactions from a bank account with an invalid client
        try {
            try {
                bank.getTransactions("TestIBAN001", null);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Getting transactions from a bank account with a valid client and account
        try {
            bank.startTransaction(dummySession, "TestIBAN001", "TestIBAN002", 10);
            assertEquals("dummySession has made 1 transaction", bank.getTransactions("TestIBAN001", dummySession).size(), 1);
            assertEquals("No exception has been thrown", 0, 0);
        } catch (RemoteException | SessionExpiredException | IllegalArgumentException ex ) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
    }
    
    //Tests the functionality starting a transaction
    @Test
    public void testStartTransaction() {
        /**
        * Starts a transaction between two bank accounts.
        * If session is over, SessionExpiredException.
        * @param klant not null, else throws IllegalArgumentException.
        * @param IBAN1 representing the bank account, if empty throws IllegalArgumentException.
        * @param IBAN2 representing the bank account, if empty throws IllegalArgumentException.
        * @param value of the money to be transferred. Has to be greater than 0 or not empty, else IllegalArgumentException.
        * @return True if succesful, else false.
        * @throws Exceptions.SessionExpiredException
        * @throws RemoteException
        */
        
        //Making a dummy client without no session
        Klant dummyNoSession = new Klant("Dummy1", "Dummy1");
        //Making a dummy client with a session
        Klant dummySession = null;
        try {
            dummySession = admin.login("TestDummy1", "TesetDummy1", "TestDummy1");
            bank.addAccount(dummySession);
        } catch (IllegalArgumentException | RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Starting a transactions from a bank account with an invalid IBAN1
        try {
            try {
                bank.startTransaction(dummySession, "iets", "TestIBAN002", 10);           
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            assertEquals("An unexpected exception has been thrown", 1, 0);
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Starting a transactions from a bank account with an invalid IBAN2
        try {
            try {
                bank.startTransaction(dummySession, "TestIBAN001", "iets", 10);           
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            assertEquals("An unexpected exception has been thrown", 1, 0);
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Starting a transactions from a bank account with an expired session
        try {
            try {
                bank.startTransaction(dummyNoSession, "TestIBAN001", "TestIBAN002", 10);
            }
            catch (SessionExpiredException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Starting a transactions from a bank account with an invalid client
        try {
            try {
                bank.startTransaction(null, "TestIBAN001", "TestIBAN002", 10);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Starting a transactions from a bank account with an invalid value
        try {
            try {
                bank.startTransaction(dummySession, "TestIBAN001", "TestIBAN002", -1);
            } catch (IllegalArgumentException ex) {
                assertEquals("An expected exception has been thrown", 0, 0);
            }
        } catch (RemoteException | SessionExpiredException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Strating a transactions from a bank account with a valid client, account and value
        try {
            bank.startTransaction(dummySession, "TestIBAN001", "TestIBAN002", 10);
            assertEquals("dummySession has made 1 transaction", bank.getTransactions("TestIBAN001", dummySession).size(), 1);
            assertEquals("No exception has been thrown", 0, 0);
        } catch (RemoteException | SessionExpiredException | IllegalArgumentException ex ) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
    }
    
    //Tests adding a value of money to a bank account
    @Test
    public void testAddSaldo() {
        /**
        * Adds a certain value of money to the bank account linked with
        * the IBAN parameter.
        * @param IBAN
        * @param value of money to be added, has to be greater than 0, else IllegalArgumentException.
        * @throws RemoteException 
        */
        
        //Making a dummy client with a session
        Klant dummySession = null;
        try {
            dummySession = admin.login("TestDummy1", "TesetDummy1", "TestDummy1");
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Add saldo with an invalid IBAN
        try {
            bank.addSaldo("", 10);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An expected exception has been thrown", 0, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Add saldo with an invalid value
        try {
            bank.addSaldo("TestIBAN001", -1);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An expected exception has been thrown", 0, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Add saldo with a valid IBAN and value        
        try {
            ArrayList<String> accounts = bank.getAccounts(dummySession);
            
            //Getting saldo of TestIBAN001
            double saldoOld = Double.parseDouble(accounts.get(0).split(";")[1]);
            bank.addSaldo("TestIBAN001", 10);
            double saldoNew = Double.parseDouble(accounts.get(0).split(";")[1]);
            assertEquals("TestIBAN001 has transferred 10 euros to TestIBAN002", saldoOld + 10, saldoNew); 
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests removing a value of money to a bank account
    @Test
    public void testRemoveSaldo() {
        /**
        * Removes a certain value of money from the bank account linked
        * with the IBAN parameter.
        * @param IBAN not empty, else IllegalArgumentException.
        * @param value of money to be removed, must be postive else IllegalArgumentException.
        * @throws RemoteException 
        */
        
        //Making a dummy client with a session
        Klant dummySession = null;
        try {
            dummySession = admin.login("TestDummy1", "TesetDummy1", "TestDummy1");
        } catch (IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Remove saldo with an invalid IBAN
        try {
            bank.removeSaldo("", 10);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An expected exception has been thrown", 0, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Remove saldo with an invalid value
        try {
            bank.removeSaldo("TestIBAN001", -1);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An expected exception has been thrown", 0, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("An unexpected exception has been thrown", 1, 0);
        }
        
        //Remove saldo with a valid IBAN and value        
        try {
            ArrayList<String> accounts = bank.getAccounts(dummySession);
            
            //Getting saldo of TestIBAN001
            double saldoOld = Double.parseDouble(accounts.get(0).split(";")[1]);
            bank.removeSaldo("TestIBAN001", 10);
            double saldoNew = Double.parseDouble(accounts.get(0).split(";")[1]);
            assertEquals("TestIBAN001 has transferred 10 euros to TestIBAN002", saldoOld - 10, saldoNew); 
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
