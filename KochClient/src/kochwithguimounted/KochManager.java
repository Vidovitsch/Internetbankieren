/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kochwithguimounted;

/**
 *
 * @author jsf3
 */
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import serializables.Edge;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author theun
 */
public class KochManager extends Observable
{
    SocketController sController;
    
    private KochWithGuiMounted application;
    private ArrayList<Edge> edges;
    private TimeStamp calculateTimeStamp;
    private TimeStamp drawTimeStamp;;

    public KochManager(KochWithGuiMounted application, int lvlInput)
    {
        application.clearKochPanel();
        
        this.application = application;
        edges = new ArrayList();
        sController = new SocketController(this);
        
        //requestEdgeList(lvlInput);
        requestEdgesSingle(lvlInput);
    }

    public void addEdge(Edge e)
    {
        edges.add(e);
    }
    
    public void drawEdge(Edge edge)
    {
        application.drawEdge(edge);
    }

    public void drawEdges(ArrayList<Edge> edges)
    {
        this.edges = edges;
        draw();
    }
    
    public void drawEdges()
    {
        draw();
    }      
    
    private void draw() {
        application.clearKochPanel();
        
        drawTimeStamp = new TimeStamp();
        drawTimeStamp.setBegin("Begin tekenen edges");
        for (Edge e : edges)
        {
            application.drawEdge(e);
        }
        drawTimeStamp.setEnd("Einde tekenen edges");
//        application.setTextCalc(calculateTimeStamp.toString() + ", " + drawTimeStamp.toString());
        application.setTextNrEdges(Integer.toString(edges.size()));
    }
    
    public void setEdges(ArrayList<Edge> edgieee)
    {
        this.edges = edgieee;
    }
    
    private void requestEdgeList(int lvlInput) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sController.requestEdgeList(lvlInput);
            }
        }).start();
    }
    
    private void requestEdgesSingle(int lvlInput) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sController.requestEdgesSingle(lvlInput);
            }
        }).start();
    }
}
