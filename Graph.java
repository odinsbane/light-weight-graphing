/*
 * 
 */

package lightgraph;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import java.util.Iterator;


import javax.imageio.ImageIO;
import javax.swing.JFrame;


/**
*  A lightweight graphing application with options for formating a graph.
*
* @author mbs207
*/
public class Graph {
    boolean AUTOX, AUTOY, XLABEL, YLABEL, GRID, XTICS, YTICS, TITLE;
    double MINX, MINY, MAXX, MAXY;
    double PADDING = 10;
    double TITLE_HEIGHT=0;
    /** indicates a pending scale */
    boolean SCALE;
    /**canvas height and width*/
    int CHEIGHT, CWIDTH;


    Color AXIS_COLOR, BACKGROUND;
    BufferedImage img;
    ArrayList<DataSet> DATASETS;

    String[] xtics, ytics;
    String xlabel, ylabel, title;

    GraphPanel panel;
    GraphMutex IMAGE_LOCK = new GraphMutex();
    public Graph(){
        MINX = Double.MAX_VALUE;
        MINY = Double.MAX_VALUE;
        MAXX = -Double.MAX_VALUE;
        MAXY = -Double.MAX_VALUE;

        AUTOX = true;
        AUTOY = true;
        
        CHEIGHT = 480;
        CWIDTH = 640;


        XTICS = true;
        YTICS = true;

        img = new BufferedImage(CWIDTH,CHEIGHT, BufferedImage.TYPE_INT_RGB);

        DATASETS = new ArrayList<DataSet>();

        SCALE=false;
        AXIS_COLOR = Color.BLACK;
        BACKGROUND = Color.WHITE;

    }
    public Graph(double[] x, double[] y){
        this();

        addData(x,y);

    }

    /**
     * Creates a new DataSite, with color, line and point information that
     * will be drawn.
     *
     * @param x array of x values
     * @param y array of y values
     * @return the dataset created
     */
    public DataSet addData(double[] x, double[] y){
        DataSet d = new DataSet(x,y);
        DATASETS.add(d);
        SCALE = true;
        return d;
    }

    public void resizeGraph(int x, int y){
        CHEIGHT = x;
        CWIDTH =  y;

        img = new BufferedImage(CWIDTH,CHEIGHT, BufferedImage.TYPE_INT_RGB);
        if(panel!=null)
            panel.updateImageSize(new Dimension(img.getWidth(), img.getHeight()));
        
    }

    /**
     * Resets the graph and paints it onto a buffered image
     *
     */
    public void resetGraph(){
        IMAGE_LOCK.get();
        Graphics2D g = img.createGraphics();
        resetGraph(new PanelPainter(g));
        g.dispose();
        IMAGE_LOCK.release();
    }
    public void resetGraph(GraphPainter p){
        if(SCALE)
            autoScale();
        double offset = PADDING;
        if(XTICS||YTICS){

            offset += 30;

        }

        if(XLABEL||YLABEL)
            offset += 20;
        

        p.setColor(BACKGROUND);
        p.fill(new Rectangle(0,0,CWIDTH,CHEIGHT));

        /*
        [ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
        [ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
        [ 1 ]   [   0    0    1   ] [ 1 ]   [         1         ]

        double m00, double m10, double m01, double m11, double m02, double m12
         */
        double width = (CWIDTH - PADDING - offset)/CWIDTH;
        double height = (CHEIGHT - PADDING - offset)/CHEIGHT;

        AffineTransform transform = new AffineTransform(width,0.0,0.0,-height,offset,CHEIGHT - offset);
        drawBorder(p,transform);

        width = (CWIDTH-offset - PADDING)/(MAXX - MINX);
        height = (CHEIGHT-offset - PADDING)/(MAXY - MINY);

        transform = new AffineTransform(width,0.0,0.0,-height,offset - MINX*width,CHEIGHT + MINY*height - offset);
        //p.setTransform( transform );
        p.setClip((int)offset,(int)PADDING,(int)(CWIDTH-(PADDING + offset)),(int)(CHEIGHT-(PADDING + offset)));
        for(DataSet set: DATASETS)
            drawSet(set, p, transform);
    }



    public void clearData(){
        DATASETS.clear();
    }
    public void drawSet(DataSet set, GraphPainter painter, AffineTransform transform){

        Point2D scaled;
        ArrayList<Point2D> pts = new ArrayList<Point2D>();
        for(Point2D pt: set.DATA){

            scaled = new Point2D.Double();
            pts.add(transform.transform(pt,scaled));
        }
        painter.setColor(set.COLOR);
        switch(set.p){

            case linespoints:
                set.LINE.drawLine(pts, painter);

            case points:
                for(Point2D pt: pts)
                    set.POINTS.drawPoint(pt, painter);

                break;

            case lines:
                set.LINE.drawLine(pts, painter);
                break;

        }
    }

    public void drawBorder(GraphPainter p, AffineTransform transform){
        Point2D[] border = new Point2D[4];
        p.setColor(AXIS_COLOR);
        border[0] = new Point2D.Double(0,0);
        border[1] = new Point2D.Double(CWIDTH,0);
        border[2] = new Point2D.Double(CWIDTH, CHEIGHT);
        border[3] = new Point2D.Double(0,CHEIGHT);

        Path2D path = new Path2D.Double();
        Point2D[] transformed = new Point2D[4];
        transform.transform(border,0,transformed,0,4);
        path.moveTo(transformed[3].getX(), transformed[3].getY());
        for(int i = 0; i<4; i++){
            path.lineTo(transformed[i].getX(), transformed[i].getY());
        }
        
        p.drawPath(path);





    }

    public void savePng(File f) throws IOException {

        ImageIO.write(img, "png",f);


    }

    
    public void createTics(){
        
        

    }
    /**
     * Sets the autoscale in the x directions.
     *
     */
    public void autoScaleX(){
        if(!AUTOX)
            SCALE=true;
        AUTOX = true;
    }

    /**
     * Sets the autoscale for the y-axis
     *
     */
    public void autoScaleY(){
        if(!AUTOY)
            SCALE=true;
        AUTOY = true;
    }

    /**
     * Goes through all of the current data and find the min/max for scaling the image.
     *
     */
    public void autoScale(){
        if(AUTOX||AUTOY){

            double mnx = 0;
            double mny = 0;
            double mxx = 0;
            double mxy = 0;

            for(DataSet set: DATASETS){
                for(Point2D pt: set.DATA){
                    if(checkX(pt.getX())&&checkY(pt.getY())){
                        mnx = pt.getX()<mnx?pt.getX():mnx;
                        mny = pt.getY()<mny?pt.getY():mny;
                        mxx = pt.getX()>mxx?pt.getX():mxx;
                        mxy = pt.getY()>mxy?pt.getY():mxy;
                    }
                }
            }

            MAXX = AUTOX?mxx:MAXX;
            MINX = AUTOX?mnx:MINX;
            if(AUTOX&&MAXX==MINX){
                System.out.println("Warner: X-Range is zero rescaline");
                MAXX++;
                MINX--;
            }
            if(AUTOY&&MAXY==MINY){
                System.out.println("Warner: Y-Range is zero rescaline");
                MAXY++;
                MINY--;
            }
            MAXY = AUTOY?mxy:MAXY;
            MINY = AUTOY?mny:MINY;
        }
        SCALE=false;
    }

    /**
     * Checks if x value is valid to use as a point for autoscaling.
     * @param x value along x axis
     * @return whether x is in range or it is autoscaling.
     */
    boolean checkX(double x){
        return AUTOX||(x>=MINX&&x<=MAXX);

    }

    /**
     * Checks if y value is value to use as a point for autoscaling
     *
     * @param y position
     * @return either y in range or it is autoscaling.
     */
    boolean checkY(double y){
        return AUTOY||(y>=MINY&&y<=MAXY);
    }
    /**
     * Set the xrange values the minimum must be less than the maximum.
     *
     * @param min min x value
     * @param max max y value
     */
    public void setXRange(double min, double max) throws IllegalArgumentException {
        if(min>=max)
            throw new IllegalArgumentException("the minimum must be less than the maximum");
        AUTOX = false;

        MINX = min;
        MAXX = max;

        SCALE=true;


    }

    /**
     * Set the xrange values the minimum must be less than the maximum.
     *
     * @param min min x value
     * @param max max y value
     */
    public void setYRange(double min, double max) throws IllegalArgumentException {
        if(min>=max)
            throw new IllegalArgumentException("the minimum must be less than the maximum");
        AUTOY = false;

        MAXY = max;
        MINY = min;
        
    }

    /**
     * Gets the data set at the corresponding index i.
     * 
     * @param i index of selected dataset
     * @return the line corresponding.
     */
    public DataSet getDataSet(int i){
        return DATASETS.get(i);
    }
    
    /**
     * Shows the graph in its own JFrame
     *
     */
    public void show(){
        GraphFrame y = new GraphFrame("Graph Panel");
        //y.setSize(640,480);
        y.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        resetGraph();
        
        panel = new GraphPanel(img);

        y.setGraph(this);
        y.pack();        
        y.setVisible(true);

    }

    public void repaint(){
        IMAGE_LOCK.get();
        try{
            panel.updateImage(img);
        } catch(NullPointerException e){
            System.out.println("unable to repaint");
            e.printStackTrace();
        }
        IMAGE_LOCK.release();
    }


}

class GraphMutex{
    boolean HELD = false;
    synchronized public void get(){
        if(HELD){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HELD = true;
    }
    synchronized public void release(){
        HELD=false;
        notify();
    }


}

