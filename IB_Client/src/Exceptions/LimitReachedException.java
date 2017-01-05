package Exceptions;


/**
 *
 * @author David
 */
public class LimitReachedException extends Exception {
    
    public LimitReachedException() {
        super();
    }
    
    public LimitReachedException(String message) {
        super(message);
    }
}
