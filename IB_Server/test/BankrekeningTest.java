import Models.Bankrekening;
import Shared_Client.Klant;
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
//GetKlant inspection method
//Add saldo to an account
//Remove saldo from an account
//toString inspection method

public class BankrekeningTest {
    
    private Bankrekening rekening;
    
    public BankrekeningTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        rekening = new Bankrekening("iets", new Klant("Dummy", "Dummy"));
    }
    
    @After
    public void tearDown() {
    }

    //Tests the inspection method getKlant
    @Test
    public void getKlantTest() {
        /**
        * Returns the owner of this bank account
        * @return klant
        */
        assertEquals("Username is DummyDummy", "DummyDummy", rekening.getKlant().getUsername());
    }
    
    //Tests the method addToBalance
    @Test
    public void addToBalanceTest() {
        /**
        * Adds a value of money to this bank account.
        * @param value of money.
        */
        rekening.addToBalance(12);
        String balance = rekening.toString().split(";")[1];
        assertEquals("Balance is 12.0", "12.0", balance);
    }
    
    //Tests the method removeFromBalance with a balance greater than the limit
    @Test
    public void removeFromBalanceValidTest() {
        /**
        * Removes a value of money from this bank account.
        * @param value of money
        * @return true if successful, else false.
        */
        //Check if successful
        assertTrue(rekening.removeFromBalance(5));
        String balance = rekening.toString().split(";")[1];
        assertEquals("Balance is -5.0", "-5.0", balance);
    }
    
    //Tests the method removeFromBalance with a balance smaller than the limit
    @Test
    public void removeFromBalanceUnvalidTest() {
        assertFalse(rekening.removeFromBalance(101));
        String balance = rekening.toString().split(";")[1];
        assertEquals("Balance is 0.0", "0.0", balance);
    }
    
    //Tests the toString method of class Bankrekening
    @Test
    public void toStringTest() {
        String IBAN = rekening.toString().split(";")[0];
        String balance = rekening.toString().split(";")[1];
        assertEquals("IBAN is iets", "iets", IBAN);
        assertEquals("Balance is 0.0", "0.0", balance);
    }
}
