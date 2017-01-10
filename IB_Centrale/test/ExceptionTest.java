import Exceptions.LimitReachedException;
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
public class ExceptionTest {
    
    public ExceptionTest() {
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

    //Tests LimitReachedException on both Constructors
    @Test
    public void limitReachedExceptionTest() {
        try {
            throw new LimitReachedException();
        } catch (LimitReachedException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
        try {
            throw new LimitReachedException("message");
        } catch (LimitReachedException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("Message is 'message'", "message", ex.getMessage());
        }
    }
}
