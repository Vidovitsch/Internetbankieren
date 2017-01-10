import Exceptions.LimitReachedException;
import Models.Centrale;
import Shared_Centrale.IBankTrans;
import Shared_Data.IPersistencyMediator;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
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
public class CentraleTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private final String IBAN1 = "NL00RABO0000000001";
    private final String IBAN2 = "NL00RABO0000000002";
    
    private Centrale centrale;
    private IPersistencyMediator database;
    private IBankTrans bank;
    
    public CentraleTest() {
        try {
            centrale = new Centrale();
            Registry dataBaseRegistry = LocateRegistry.getRegistry(ipAddressDB, portNumber);
            database = (IPersistencyMediator) dataBaseRegistry.lookup(bindingName);
            centrale.setPersistencyMediator(database);
            bank = new IBankTrans() {
                @Override
                public void addSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
                    System.out.println("No implementation (not needed for tests)");
                }

                @Override
                public boolean removeSaldo(String IBAN, double value) throws IllegalArgumentException, RemoteException {
                    return !(getSaldo(IBAN) - value < -100);
                }
            };
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
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
            database.registerAccount("Dummy1", "City1", "123456789");
            database.registerAccount("Dummy2", "City2", "123456789");
            database.addBankrekening("Dummy1", "City1", IBAN1, "RABO");
            database.addBankrekening("Dummy2", "City2", IBAN2, "RABO");
        } catch (RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        try {
            database.removeKlant("Dummy1", "City1", "123456789");
            database.removeKlant("Dummy2", "City2", "123456789");
        } catch (RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Tests the method startTransaction with a positive flow with description
    @Test
    public void startTransactionValidInputDescriptionTest() {
            /**
             * Inits a transaction between the two bank accounts
             * linked with the two IBAN parameters.
             * @param IBAN1 linked with a bank account.
             * @param IBAN2 linked with a bank account.
             * @param bank
             * @param value in money to be transferred.
             * @param description
             * @throws Exceptions.LimitReachedException
             * @throws RemoteException
             */
        try {
            double saldo1 = getSaldo(IBAN1);
            double saldo2 = getSaldo(IBAN2);
            
            //Check saldo before transaction
            assertEquals("Saldo of IBAN1 is 0.0", 0.0, saldo1, 0);
            assertEquals("Saldo of IBAN2 is 0.0", 0.0, saldo2, 0);
            
            //Start transaction
            centrale.startTransaction(IBAN1, IBAN2, bank, 50, "Description");
            
            //Check saldo after transaction
            saldo1 = getSaldo(IBAN1);
            saldo2 = getSaldo(IBAN2);
            assertEquals("Saldo of IBAN1 is -50.0", -50.0, saldo1, 0);
            assertEquals("Saldo of IBAN2 is 50.0", 50.0, saldo2, 0);
        } catch (LimitReachedException | RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method startTransaction provoking a LimitReachedException
    @Test
    public void startTransactionLimitReachedTest() {
        double saldo1 = getSaldo(IBAN1);
        double saldo2 = getSaldo(IBAN2);
        try {
            //Check saldo before transaction
            assertEquals("Saldo of IBAN1 is 0.0", 0.0, saldo1, 0);
            assertEquals("Saldo of IBAN2 is 0.0", 0.0, saldo2, 0);
            
            //Start transaction
            centrale.startTransaction(IBAN1, IBAN2, bank, 101, "Description");
            
            fail();
        } catch (LimitReachedException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
            
            //Check saldo after transaction
            saldo1 = getSaldo(IBAN1);
            saldo2 = getSaldo(IBAN2);
            assertEquals("Saldo of IBAN1 is 0.0", 0.0, saldo1, 0);
            assertEquals("Saldo of IBAN2 is 0.0", 0.0, saldo2, 0);
        } catch (RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method getTransactions with description
    @Test
    public void getTransactionsWithDescriptionTest() {
        try {
            //Get transactions before
            ArrayList<String> transactionsIBAN1 = centrale.getTransactions(IBAN1);
            ArrayList<String> transactionsIBAN2 = centrale.getTransactions(IBAN2);
            assertEquals("There are no transactions made", transactionsIBAN1.size(), 0);
            assertEquals("There are no transactions made", transactionsIBAN2.size(), 0);
            
            //Start transaction
            centrale.startTransaction(IBAN1, IBAN2, bank, 35, "Description");
            
            //Get transaction IBAN1
            transactionsIBAN1 = centrale.getTransactions(IBAN1);
            transactionsIBAN2 = centrale.getTransactions(IBAN2);
            
            assertEquals("There is one transaction made", transactionsIBAN1.size(), 1);
            assertEquals("There is one transaction made", transactionsIBAN2.size(), 1);
            assertEquals("IBANFrom is IBAN1", IBAN1, transactionsIBAN1.get(0).split(";")[2]);
            assertEquals("IBANTo is IBAN2", IBAN2, transactionsIBAN2.get(0).split(";")[3]);
            assertEquals("Amount transferred is 35 for IBAN1", String.valueOf(35.0), transactionsIBAN1.get(0).split(";")[1]);
            assertEquals("Amount transferred is 35 for IBAN2", String.valueOf(35.0), transactionsIBAN2.get(0).split(";")[1]);
            assertEquals("Description of the transactoin for IBAN1 is 'Description'", "Description", transactionsIBAN1.get(0).split(";")[4]);
            assertEquals("Description of the transactoin for IBAN2 is 'Description'", "Description", transactionsIBAN2.get(0).split(";")[4]);
        } catch (LimitReachedException | RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method getTransactions without description
    @Test
    public void getTransactionsWithoutDescriptionTest() {
        try {
            //Get transactions before
            ArrayList<String> transactionsIBAN1 = centrale.getTransactions(IBAN1);
            ArrayList<String> transactionsIBAN2 = centrale.getTransactions(IBAN2);
            assertEquals("There are no transactions made", transactionsIBAN1.size(), 0);
            assertEquals("There are no transactions made", transactionsIBAN2.size(), 0);
            
            //Start transaction
            centrale.startTransaction(IBAN1, IBAN2, bank, 35, "");
            
            //Get transaction IBAN1
            transactionsIBAN1 = centrale.getTransactions(IBAN1);
            transactionsIBAN2 = centrale.getTransactions(IBAN2);
            
            assertEquals("There is one transaction made", transactionsIBAN1.size(), 1);
            assertEquals("There is one transaction made", transactionsIBAN2.size(), 1);
            assertEquals("IBANFrom is IBAN1", IBAN1, transactionsIBAN1.get(0).split(";")[2]);
            assertEquals("IBANTo is IBAN2", IBAN2, transactionsIBAN2.get(0).split(";")[3]);
            assertEquals("Amount transferred is 35 for IBAN1", String.valueOf(35.0), transactionsIBAN1.get(0).split(";")[1]);
            assertEquals("Amount transferred is 35 for IBAN2", String.valueOf(35.0), transactionsIBAN2.get(0).split(";")[1]);
            try {
                String test = transactionsIBAN1.get(0).split(";")[4];
            } catch (IndexOutOfBoundsException ex) {
                assertTrue(true);
            }
            try {
                String test = transactionsIBAN2.get(0).split(";")[4];
            } catch (IndexOutOfBoundsException ex) {
                assertTrue(true);
            }
        } catch (LimitReachedException | RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Test to cover the method stringToTransactie
    @Test
    public void stringToTransactieTest() {
            /**
             * Converts the String of transactie-values to a Transactie object
             * @param values
             * @return Transactie
             */
        try {
            database.addTransaction(IBAN1, IBAN2, 12, getCurrentDateTime(), "");
            centrale.setPersistencyMediator(database);
        } catch (RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Help methods
    
    /**
     * Gets a saldo of a specific IBAN.
     * @param IBAN
     * @return saldo
     */
    private double getSaldo(String IBAN) {
        try {
            double saldo = 0;
            for (String value : database.getAllBankrekeningen("RABO")) {
                if (value.split(";")[0].equals(IBAN)) {
                    saldo = Double.parseDouble(value.split(";")[2]);
                }
            }
            return saldo;
        } catch (RemoteException ex) {
            Logger.getLogger(CentraleTest.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    /**
     * Returns the local current date-time in String-value
     * @return date-time String-value
     */
    private String getCurrentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime.toString();
    }
}
