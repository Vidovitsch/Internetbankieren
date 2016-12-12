package Models;

import Shared_Centrale.ICentrale;


/**
 *
 * @author David
 */
public class Administratie {

    private ICentrale centrale;
    private Bank bank;
    
    public Administratie(ICentrale centrale, Bank bank) {
        this.centrale = centrale;
        this.bank = bank;
    }
}
