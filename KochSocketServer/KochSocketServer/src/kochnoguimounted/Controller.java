/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kochnoguimounted;

import serializables.Edge;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jsf3
 */
public class Controller implements Observer{
    
    KochFractal kochFractal;
    Writer writer;
    int numberOfEdges;
    int currentEdgeCount;
    FileOutputStream fos;
    ObjectOutputStream out;
    ArrayList<Edge> edges;
    boolean binary = false;
    private static final Logger LOG = Logger.getLogger(Controller.class.getName());
    
    public Controller() throws IOException
    {
            try {
            // Establish server socket
            ServerSocket serverSocket = new ServerSocket(8189);
            LOG.log(Level.INFO, "Server is running. Listening on port: {0}", serverSocket.getLocalPort());
            while (true) {
                try {
                    // Wait for client connection
                    Socket incomingSocket = serverSocket.accept();
                    LOG.log(Level.INFO, "New Client Connected: {0}", incomingSocket.getInetAddress());
                    // Handle client request in a new thread
                    Thread t = new Thread(new ClientListener(incomingSocket, new KochFractal()));
                    t.start();
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "IOException occurred: {0}", e.getMessage());
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
//        
//        edges = new ArrayList<>();
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        if(binary)
//        {
//         System.out.println("Calculate edges and write to binary file");           
//        }
//        else{
//          System.out.println("Calculate edges and write to textfile");             
//        }
//         System.out.println("Enter Level for fractal: ");  
//        try {
//            int i = Integer.parseInt(br.readLine());
//            if(binary)
//            {
//                SetAndCalculateBinary(i);   
//            }
//            else
//            {
//                SetAndCalculateText(i);
//            }   
//        } catch (NumberFormatException nfe) {
//            System.err.println("Invalid Format!");
//        }
    }
    
    private void SetAndCalculateText(int lvl) throws UnsupportedEncodingException, FileNotFoundException
    {
        setKochFractalStuff(lvl);
        writer = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream("/media/jsf3/d617a74d-0cda-4733-9c19-6ea788a10ecc/EdgesText.txt"), "utf-8"));
                try {
            writer.write("Edges voor een fractal met lvl: " + String.valueOf(lvl) + " \n");
        } catch (IOException ex) {
            Logger.getLogger(KochNoGuiMounted.class.getName()).log(Level.SEVERE, null, ex);
        } 
        kochFractal.calculateEdges();
    }
    
    private void SetAndCalculateBinary(int lvl) throws IOException {

        setKochFractalStuff(lvl);
        fos = new FileOutputStream("/media/jsf3/d617a74d-0cda-4733-9c19-6ea788a10ecc/EdgesBinary.txt");
        out = new ObjectOutputStream(fos);

        kochFractal.calculateEdges();
        
    }
  
    private void setKochFractalStuff(int lvl)
    {
        kochFractal = new KochFractal();
        kochFractal.setLevel(lvl);
        numberOfEdges = kochFractal.getNrOfEdges();
        currentEdgeCount = 0;
        kochFractal.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        currentEdgeCount++;
        Edge e = (Edge)arg;
        edges.add(e);
        try {
        if (binary) {            
            if(currentEdgeCount == numberOfEdges)
            { 
                out.writeObject(edges);
                out.close();
            }
        }
        else{
            writer.write(e.toString() + " \n");  
            if(currentEdgeCount == numberOfEdges)
            {
              writer.flush();              
            }
        }
        } catch (IOException ex) {
            Logger.getLogger(KochNoGuiMounted.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
