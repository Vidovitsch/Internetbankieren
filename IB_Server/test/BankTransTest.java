/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
import Models.Administratie;
import Models.Bank;
import Models.BankTrans;
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
//Add saldo to an account
//Remove saldo from an account

//Zet de centrale en de database aan voor deze tests
public class BankTransTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private Klant dummyKlant1;
    private Klant dummyKlant2;
    private Administratie admin;
    private ICentrale centrale;
    private Bank bank;
    private BankTrans bankTrans;
    private IPersistencyMediator database;
    
    public BankTransTest() {
        try {
            Registry centraleRegistry = LocateRegistry.getRegistry("localhost", 1100);
            centrale = (ICentrale) centraleRegistry.lookup("centrale");
            Registry dataBaseRegistry = LocateRegistry.getRegistry(ipAddressDB, portNumber);
            database = (IPersistencyMediator) dataBaseRegistry.lookup(bindingName);
            admin = new Administratie(centrale, new RemotePublisher());
            admin.setPersistencyMediator(database);
            
            bank = new Bank("DummyBank", "RABO", admin, centrale);
            bank.setPersistencyMediator(database);
            bankTrans = new BankTrans(bank);
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
    
    //Tests concerning the interface IBankTrans, implemented by the class BankTrans

    //Tests the method addSaldo with a valid IBAN and value
    @Test
    public void addSaldoValidIBAN_Value() {
            /**
             * Adds a certain value of money to the bank account linked with
             * the IBAN parameter.
             * @param IBAN not empty, else IllegalArgumentException.
             * @param value of money to be added, has to be greater than 0, else IllegalArgumentException.
             * @throws RemoteException
             */
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Add saldo
            bankTrans.addSaldo(IBAN, 12);
            //Check saldo
            accounts = bank.getAccounts(dummyKlant1);
            String newSaldo = accounts.get(0).split(";")[1];
            assertEquals("New saldo is 12.0", "12.0", newSaldo);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method addSaldo with an empty IBAN and valid value
    @Test
    public void addSaldoEmptyIBAN() {
        try {
            String IBAN = "";
            bankTrans.addSaldo(IBAN, 12);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method addSaldo with a valid IBAN and a zero value
    @Test
    public void addSaldoZeroValue() {
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Add saldo
            bankTrans.addSaldo(IBAN, 0);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method addSaldo with a valid IBAN and a negative value
    @Test
    public void addSaldoNegativeValue() {
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Add saldo
            bankTrans.addSaldo(IBAN, -1);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method addSaldo with an unvalid IBAN and a valid value
    @Test
    public void addSaldoUnvalidIBAN() {
        try {
            String IBAN = "iets";
            bankTrans.addSaldo(IBAN, 12);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method removeSaldo with a valid IBAN and value
    @Test
    public void removeSaldoValidIBAN_Value() {
            /**
            * Removes a certain value of money from the bank account linked
            * with the IBAN parameter.
            * @param IBAN not empty, else IllegalArgumentException.
            * @param value of money to be removed, must be postive else IllegalArgumentException.
            * @return true if successful, else false.
            * @throws RemoteException 
            */
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Remove saldo
            bankTrans.removeSaldo(IBAN, 12);
            //Check saldo
            accounts = bank.getAccounts(dummyKlant1);
            String newSaldo = accounts.get(0).split(";")[1];
            assertEquals("New saldo is -12.0", "-12.0", newSaldo);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests the method removeSaldo with an empty IBAN and valid value
    @Test
    public void removeSaldoEmptyIBAN() {
        try {
            String IBAN = "";
            bankTrans.removeSaldo(IBAN, 12);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method removeSaldo with a valid IBAN and a zero value
    @Test
    public void removeSaldoZeroValue() {
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Remove saldo
            bankTrans.removeSaldo(IBAN, 0);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method removeSaldo with a valid IBAN and a negative value
    @Test
    public void removeSaldoNegativeValue() {
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Remove saldo
            bankTrans.removeSaldo(IBAN, -1);
            fail();
        } catch (SessionExpiredException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
    }
    
    //Tests the method removeSaldo with an unvalid IBAN and a valid value
    @Test
    public void removeSaldoUnvalidIBAN() {
        try {
            String IBAN = "iets";
            bankTrans.removeSaldo(IBAN, 12);
            fail();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        } catch (RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
    
    //Tests the method removeSaldo with a balance lower than credit limit
    @Test
    public void removeSaldoCreditLimit() {
        try {
            //Get IBAN and Saldo
            dummyKlant1.getBankAccounts((IBank) bank);
            ArrayList<String> accounts = bank.getAccounts(dummyKlant1);
            String IBAN = accounts.get(0).split(";")[0];
            //Remove saldo
            boolean value = bankTrans.removeSaldo(IBAN, 101);
            assertFalse(value);
        } catch (SessionExpiredException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(BankTransTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
