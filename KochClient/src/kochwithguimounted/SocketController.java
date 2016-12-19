package kochwithguimounted;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import serializables.Edge;

public class SocketController {
    
    private static final Logger LOG = Logger.getLogger(SocketController.class.getName());
    private KochManager manager;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public SocketController(KochManager manager) {
        try {
            this.manager = manager;

            //IP David_Fontys: 145.93.85.1 
            socket = new Socket("145.93.177.68", 8189);
        
            out = new ObjectOutputStream(socket.getOutputStream());
            
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println((String) in.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    public void requestEdgeList(int level) {
        try {
            //Send setting to the server
            out.writeObject("List");
            out.flush();
            //Send level to the server
            out.writeInt(level);
            out.flush();
            //Get serverMessage
            System.out.println((String) in.readObject());
            //Get list of edges
            ArrayList<Edge> edges = (ArrayList<Edge>) in.readObject();
            
            Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            manager.drawEdges(edges);
                        }
                    });
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void requestEdgesSingle(int level) {
        boolean finished = false;
        try {
            //Send setting to the server
            out.writeObject("Single");
            out.flush();
            //Send level to the server
            out.writeInt(level);
            out.flush();
            //Get serverMessage
            System.out.println((String) in.readObject());
            
            while (!finished) {
                Object inObject =in.readObject();
                if (inObject instanceof Edge) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            manager.drawEdge((Edge) inObject);
                        }
                    });
                } else {
                    finished = true;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void requestZoom(double zoom, double transX ,double transY) {
        try {
            out.writeInt(-1);
            out.flush();
            out.writeDouble(zoom);
            out.flush();
            out.writeDouble(transX);
            out.flush();
            out.writeDouble(transY);
            out.flush();
            
            ArrayList<Edge> edges = (ArrayList<Edge>) in.readObject();
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    manager.drawEdges(edges);
                }
            });
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
