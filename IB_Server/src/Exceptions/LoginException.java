package Exceptions;

/**
 *
 * @author David
 */
public class LoginException extends Exception {

    public LoginException() {
        super();
    }
    
    public LoginException(String message) {
        super(message);
    }
}
