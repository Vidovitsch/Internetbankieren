package Models;

import Shared_Client.Klant;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author David
 */
public class Sessie {
    
    private Klant client;
    private Administratie admin;
    private Timer sessionTimer;
    
    private final int maxTicks = 5;
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
     * Test method for inspecting amount of ticks
     * @return amount of ticks
     */
    public int getTicks() {
        return ticks;
    }
    
    /**
     * Test method for inspecting the max amount of ticks
     * @return max amount of ticks
     */
    public int getMaxTicks() {
        return maxTicks;
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
                System.out.println(ticks);
                if (ticks == maxTicks) {
                    admin.removeSessionLocal(client);
                } else {
                    ticks++;
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
