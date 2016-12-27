package Models;

import Shared_Client.Klant;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class Sessie {
    
    private Klant client;
    private Administratie admin;
    private Timer sessionTimer;
    
    private final int maxTicks = 60;
    private int ticks = 0;
    
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
    
    /**
     * Starts the session with a timer.
     * The max ticks is set in advance.
     */
    public void startSession() {
        sessionTimer = new Timer();
        sessionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (ticks == maxTicks) {
                    try {
                        admin.logout(client);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Sessie.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    ticks++;
                    System.out.println(client.getName() + "'s session: " + ticks);
                }
            }
        }, 1000, 1000);
    }
    
    /**
     * Refeshes the current session
     */
    public void refreshSession() {
        stopSession();
        ticks = 0;
        startSession();
    }
    
    /**
     * Stops the session timer.
     */
    public void stopSession() {
        sessionTimer.cancel();
    }
}
