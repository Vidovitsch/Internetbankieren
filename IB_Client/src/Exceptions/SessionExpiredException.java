package Exceptions;

/**
 *
 * @author David
 */
public class SessionExpiredException extends Exception {

    public SessionExpiredException() {
        super();
    }
    
    public SessionExpiredException(String message) {
        super(message);
    }
}
