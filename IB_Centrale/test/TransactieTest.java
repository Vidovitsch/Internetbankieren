import Models.Transactie;
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
public class TransactieTest {
    
    private final String IBANFrom = "NL87RABO0354987561";
    private final String IBANTo = "NL55RABO0333587111";
    private final String date = "6-1-2017";
    private final double amount = 112.43;
            
    private Transactie transactie;
    
    public TransactieTest() {
        transactie = new Transactie(IBANFrom, IBANTo, date, amount);
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

    //Tests the modification method setDescription
    @Test
    public void setDescriptionTest() {
        /**
        * Sets the description of a transaction.
        * @param description 
        */
        String desc = "M.b.t. uit eten";
        transactie.setDescription(desc);
        assertEquals("Description is 'M.b.t. uit eten'", desc, transactie.getDescription());
    }
    
    //Tests the inspection method getDescription
    @Test
    public void getDescriptionTest() {
        /**
        * Returns the description of this transaction.
        * @return the description of this transaction. 
        */
        String desc = "M.b.t. gisteravond";
        transactie.setDescription(desc);
        assertEquals("Description is 'M.b.t. gisteravond'", desc, transactie.getDescription());
    }
    
    //Tests the inpection method getDate
    @Test
    public void getDateTest() {
        /**
        * Returns the date of creation (in text) of this transaction.
        * @return the date as a String type. 
        */
        assertEquals("The date is '6-1-2017'", date, transactie.getDate());
    }
    
    //Tests the inspection method getAmount
    @Test
    public void getAmountTest() {
        /**
        * Returns the amount of money transferred in this transaction.
        * @return the amount of money. 
        */
        assertEquals("The amount of money transferred is 112.43", amount, transactie.getAmount(), 0);
    }
    
    //Tests the inspection method getIBANFrom
    @Test
    public void getIBANFrom() {
        /**
        * Returns the IBAN from the bank account who made the transaction.
        * @return IBAN 
        */
        assertEquals("IBANFrom is 'NL87RABO0354987561'", IBANFrom, transactie.getIBANFrom());
    }
    
    //Tests the inspection method getIBANTo
    @Test
    public void getIBANTo() {
        /**
        * Returns the IBAN from the bank account who received the transaction.
        * @return IBAN 
        */
        assertEquals("IBANTo is 'NL55RABO0333587111'", IBANTo, transactie.getIBANTo());
    }
}
