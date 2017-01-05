/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Models.Administratie;
import Models.Sessie;
import Shared_Centrale.ICentrale;
import Shared_Client.Klant;
import fontyspublisher.RemotePublisher;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
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

//Test coverage:
//
//

//Zet de centrale en de database aan voor deze tests
//Set maxTicks (hardcode) at a value of 5 to run tests faster
public class SessieTest {
    
    private Administratie admin;
    private Sessie session;
    private int ticks = 0;
    
    public SessieTest() {
        try {
            Registry centraleRegistry = LocateRegistry.getRegistry("localhost", 1100);
            ICentrale centrale = (ICentrale) centraleRegistry.lookup("centrale");
            admin = new Administratie(centrale, new RemotePublisher());
            session = new Sessie(new Klant("Dummy", "Dummy"), admin);
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(SessieTest.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    //Tests the inspection method getClient
    @Test
    public void getClientTest() {
        /**
        * Returns the client linked with this session.
        * @return Client linked with this session.
        */
        assertEquals("Username is DummyDummy", "DummyDummy", session.getClient().getUsername());
    }
    
    //Tests the inspection method getTicks
    @Test
    public void getTicksTest() {
        //Timer is not running, so amount of ticks is zero
        assertEquals("Amount of ticks is 0 (default)", 0, session.getTicks());
    }
    
    //Tests the inspection method getMaxTicks
    @Test
    public void getMaxTicks() {
        //Max ticks is set on 5 for this tests
        assertEquals("Max ticks is set on 5 (for this tests)", 5, session.getMaxTicks());
    }
    
    //Tests the session timer
    @Test
    public void startSessionTest() {
        session.startSession();
        (new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                if (ticks == 3) {
                    assertEquals("Amount of ticks is 3", session.getTicks(), 3);
                }
                if (ticks == 5) {
                    this.cancel();
                }
                ticks++;         
            }
        }, 1000, 1000);
    }
    
}
