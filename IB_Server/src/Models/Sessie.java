package Models;

import Shared_Client.Klant;

/**
 *
 * @author David
 */
public class Sessie {
    
    private Klant client;
    private Administratie admin;
    
    public Sessie(Klant client, Administratie admin) {
        this.client = client;
        this.admin = admin;
    }
    
    /**
     * Returns the client linked with this session.
     * @return Client linked with this session.
     */
    public Klant getClient() {
        return client;
    }
}
