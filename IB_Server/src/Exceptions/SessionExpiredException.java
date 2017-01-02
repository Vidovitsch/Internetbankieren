/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
