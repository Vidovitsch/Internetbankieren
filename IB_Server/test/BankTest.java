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
//

//Zet de centrale en de database aan voor deze tests
public class BankTest {
    
    private final String ipAddressDB = "localhost";
    private final String bindingName = "Database";
    private final int portNumber = 1088;
    private Klant dummyKlant1;
    private Administratie admin;
    private ICentrale centrale;
    private IBank bank;
    
    public BankTest() {
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
            //Register one dummyUsers and a bank
            dummyKlant1 = admin.register("DummyUser", "DummyUser", "123456789");
            bank = admin.getBank(dummyKlant1);
        } catch (RegisterException | IllegalArgumentException | RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        try {
            //Remove dummyUsers from the database
            admin.removeKlant("DummyUser", "DummyUser", "123456789");
        } catch (RemoteException ex) {
            Logger.getLogger(AdminTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Tests concerning the interface IBank, implemented by the class Bank
    
    @Test
    public void getNameTest() {
        
    }
    
    @Test
    public void getShortNameTest() {
        
    }
}
