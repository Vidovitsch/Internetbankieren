/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kochnoguimounted;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import serializables.Edge;

/**
 *
 * @author Michiel van Eijkeren
 */
public class ClientListener implements Runnable, Observer
{

    private static final Logger LOG = Logger.getLogger(ClientListener.class.getName());
    private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private KochFractal fractal = null;
    private int numberOfEdges;
    private int currentEdgeCount;
    Object inObject = null;
    ArrayList<Edge> calculatedEdges;
    private String setting;

    /**
     * Create object with a given socket.
     *
     * @param s Socket that is used to communicate with client
     */
    public ClientListener(Socket s, KochFractal fractal)
    {
        calculatedEdges = new ArrayList<Edge>();
        this.fractal = fractal;
        this.socket = s;
    }

    @Override
    public void run()
    {
        try
        {
            LOG.log(Level.INFO, "ClientListenerRunnable Started. Using port: {0}", socket.getLocalPort());

            // Bind input and outputstreams
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream()); 

            // Send random integer value to client
            String connectedString = "Server received listening client";
            out.writeObject(connectedString);
            out.flush();
            try
            {
                setting = (String) in.readObject();
            } catch (ClassNotFoundException ex)
            {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            LOG.log(Level.INFO, "String sent: {0}", connectedString);
            fractal.addObserver(this);

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    boolean done = false;
                    while (!done)
                    {
                        try
                        {
                            int lvl = in.readInt();
                            if (lvl != 0)
                            {
                                System.out.println("Lvl ontvangen: " + String.valueOf(lvl) + " vanuit client/" + socket.getLocalAddress());
                                fractal.setLevel(lvl);
                                numberOfEdges = fractal.getNrOfEdges();
                                currentEdgeCount = 0;
                                String confirmCalculateStart = ("Started Calculating Edges for lvl: " + String.valueOf(lvl) + ",with setting: " + setting);
                                out.writeObject(confirmCalculateStart);
                                out.flush();
                                calculatedEdges = new ArrayList<Edge>();
                                fractal.calculateEdges();
                            }
                        } catch (IOException ex)
                        {
                            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        } catch (IOException ex)
        {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        Edge e = (Edge) arg;
        calculatedEdges.add(e);
        currentEdgeCount++;
        if (setting.equals("Single"))
        {
            try
            {
                out.writeObject(e);
                out.flush();
                if (numberOfEdges == currentEdgeCount)
                {
                    try
                    {
                        out.writeObject("Finished");
                        out.flush();
                    } catch (IOException ex)
                    {
                        Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
//                }
            } catch (IOException ex)
            {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (setting.equals("List"))
        {
            if (numberOfEdges == currentEdgeCount)
            {
                try
                {
                    out.writeObject(calculatedEdges);
                    out.flush();
                } catch (IOException ex)
                {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
