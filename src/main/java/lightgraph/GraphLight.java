package lightgraph;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;

/**
 * This is the 'test package'
 * User: melkor
 * Date: May 9, 2010
 * Time: 8:56:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphLight {
    int COUNTER=0;
    Graph xy;
    DataSet b;
    GraphLight(Graph xy){
        this.xy = xy;
        b = xy.addData(new double[]{0}, new double[]{0});
    }
    boolean next(){
        b.DATA.get(0).setLocation(COUNTER*0.1, 0);
        switch(COUNTER){
            case 200:
                xy.setXRange(0,500);
                break;
            case 400:
                xy.autoScaleX();
                break;
            case 600:
                xy.setYRange(0,10);
                break;
            case 800:
                xy.setXRange(-100,100);
                xy.setYRange(-5,5);
                xy.resizeGraph(1000,1000);
                break;
        }
        xy.resetGraph();
        xy.repaint();
        COUNTER++;
        return COUNTER>1000;
    }
    public static void main(String[] args){
        double[] x = new double[1000];
        double[] y = new double[1000];
        double[] z = new double[1000];
        for(int i = 0; i<x.length; i++){

            x[i] = i*Math.PI*0.01;
            y[i] = Math.cos(x[i]);
            z[i] = Math.sin(x[i]);

        }
        final Graph xy = new Graph(x,y);
        xy.setContentSize(800, 600);
        xy.setXRange(1,11.5);
        xy.setYRange(-0.5,0.5);
        xy.appendPoint(0,0,0);

        DataSet ds = xy.getDataSet(0);
        ds.setLabel("cos");
        GraphPoints cross = GraphPoints.crossX();
        cross.setSize(6);
        ds.setPoints(cross);

        ds = xy.addData(x,z);
        ds.setLabel("sin");
        GraphPoints squares = GraphPoints.hollowSquares();
        squares.setSize(6);
        ds.setPoints(squares);

        xy.setXLabel("theta");
        xy.setYLabel("function");
        xy.setTitle("plot testing");
        
        SwingUtilities.invokeLater(

            new Runnable(){
                public void run(){
                    xy.show(true);
                }
            }
        );
        
    }
    public static void main2(String[] args){
        double[] x = new double[1000];
        double[] y = new double[1000];
        double[] y2 = new double[20];
        double[] x2 = new double[20];

        for(int i = 0; i<x.length; i++){

            x[i] = i - 500;

            y[i] = 0.001*Math.pow(x[i],3) -  0.05*Math.pow(x[i],2)+x[i]*0.8 - 1;
            
        }
        for(int i = 0; i<x2.length; i++){

            x2[i] = x[i*50];

            y2[i] = y[i*50]*(1.25 - 0.5*Math.random());

        }

        final Graph xy = new Graph(x,y);
        DataSet s = xy.getDataSet(0);
        s.setPoints(null);
        s = xy.addData(x2,y2);
        s.setLine(null);
        SwingUtilities.invokeLater(

            new Runnable(){
                public void run(){
                    xy.show();
                }
            }
        );

        GraphLight staple = new GraphLight(xy);
        Timer timmy = new Timer();
        timmy.scheduleAtFixedRate(new Proceed(staple),1000l,10l);
        

    }
}

class Proceed extends TimerTask {
    GraphLight app;
    Proceed(GraphLight app){
        this.app = app;
    }
    public void run(){
        if (app.next())
                cancel();
    }
    
}

