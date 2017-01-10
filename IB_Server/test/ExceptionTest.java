import Exceptions.LimitReachedException;
import Exceptions.LoginException;
import Exceptions.RegisterException;
import Exceptions.SessionExpiredException;
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
    
    //Tests LoginException on both Constructors
    @Test
    public void loginExceptionTest() {
        try {
            throw new LoginException();
        } catch (LoginException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
        try {
            throw new LoginException("message");
        } catch (LoginException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("Message is 'message'", "message", ex.getMessage());
        }
    }
    
    //Tests RegisterException on both Constructors
    @Test
    public void registerExceptionTest() {
        try {
            throw new RegisterException();
        } catch (RegisterException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
        try {
            throw new RegisterException("message");
        } catch (RegisterException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("Message is 'message'", "message", ex.getMessage());
        }
    }
    
    //Tests SessionExpired on both Constructors
    @Test
    public void sessionExpiredExceptionTest() {
        try {
            throw new SessionExpiredException();
        } catch (SessionExpiredException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(true);
        }
        try {
            throw new SessionExpiredException("message");
        } catch (SessionExpiredException ex) {
            Logger.getLogger(ExceptionTest.class.getName()).log(Level.SEVERE, null, ex);
            assertEquals("Message is 'message'", "message", ex.getMessage());
        }
    }
}
