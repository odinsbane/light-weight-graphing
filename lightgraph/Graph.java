/*
 * 
 */

package lightgraph;

import lightgraph.gui.GraphFrame;
import lightgraph.painters.GraphPainter;
import lightgraph.painters.PanelPainter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;


import javax.imageio.ImageIO;
import javax.swing.*;


/**
*  A lightweight graphing application with options for formating a graph.
*
* @author mbs207
*/
public class Graph {
    public boolean AUTOX;
    public boolean AUTOY;
    boolean XLABEL;
    boolean YLABEL;
    boolean GRID;
    boolean XTICS;
    boolean YTICS;
    boolean TITLE;
    public double MINX;
    public double MINY;
    public double MAXX;
    public double MAXY;
    double PADDING = 10;
    double YTICS_WIDTH = 30;
    double XTICS_HEIGHT = 20;
    double TITLE_HEIGHT=0;
    double FONT_HEIGHT = 20;

    double LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN;


    FontMetrics FONT_METRICS;
    /** indicates a pending scale */
    boolean SCALE;
    /**canvas height and width*/
    public int CHEIGHT;
    public int CWIDTH;


    Color AXIS_COLOR, BACKGROUND;
    BufferedImage img;
    public ArrayList<DataSet> DATASETS;

    String[] xtics, ytics;
    String xlabel, ylabel, title;

    public GraphPanel panel;
    GraphMutex IMAGE_LOCK = new GraphMutex();
    private JFrame frame;
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
        Graphics g = img.getGraphics();
        FONT_METRICS = g.getFontMetrics();
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
        d.setColor(GraphDefaults.getDefaultColor(DATASETS.size()));
        DATASETS.add(d);
        SCALE = true;
        return d;
    }

    /**
     * replaces the data set with a new data set consisting of the xy pairs.
     *
     * @param set
     * @param x
     * @param y
     * @return
     */
    public DataSet replaceData(int set, double[] x, double[] y){
        DataSet d = new DataSet(x,y);
        DataSet old = DATASETS.get(set);
        d.setColor(old.COLOR);
        d.setPoints(old.POINTS);
        d.setLine(old.LINE);
        d.setLabel(old.label);

        DATASETS.remove(set);
        if(set<DATASETS.size()){

            DATASETS.add(set,d);

        }else{
            DATASETS.add(d);
        }
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
        BufferedImage image = new BufferedImage(CWIDTH,CHEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        resetGraph(new PanelPainter(g));
        g.dispose();
        img = image;
        IMAGE_LOCK.release();
    }
    public void resetGraph(GraphPainter p){
        if(SCALE)
            autoScale();

        LEFT_MARGIN = PADDING;
        RIGHT_MARGIN = PADDING;
        TOP_MARGIN = PADDING;
        BOTTOM_MARGIN = PADDING;

        if(XTICS||YTICS){
            createTics();
        }


        
        if(XTICS){
            LEFT_MARGIN += YTICS_WIDTH;
        }

        if(YTICS){

            BOTTOM_MARGIN += XTICS_HEIGHT;

        }

        if(XLABEL){
            LEFT_MARGIN += FONT_HEIGHT;
        }
        if(YLABEL){
            BOTTOM_MARGIN += FONT_HEIGHT;
        }

        if(TITLE){

            TOP_MARGIN += FONT_HEIGHT;

        }

        p.setColor(BACKGROUND);
        p.fill(new Rectangle(0,0,CWIDTH,CHEIGHT));

        /*
        [ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
        [ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
        [ 1 ]   [   0    0    1   ] [ 1 ]   [         1         ]

        double m00, double m10, double m01, double m11, double m02, double m12
         */
        double width = (CWIDTH - LEFT_MARGIN - RIGHT_MARGIN)/CWIDTH;
        double height = (CHEIGHT - TOP_MARGIN - BOTTOM_MARGIN)/CHEIGHT;

        AffineTransform transform = new AffineTransform(width,0.0,0.0,-height,LEFT_MARGIN,CHEIGHT - BOTTOM_MARGIN);
        drawBorder(p,transform);

        width = (CWIDTH - LEFT_MARGIN - RIGHT_MARGIN)/(MAXX - MINX);
        height = (CHEIGHT - TOP_MARGIN - BOTTOM_MARGIN)/(MAXY - MINY);

        transform = new AffineTransform(width,0.0,0.0,-height,LEFT_MARGIN - MINX*width,CHEIGHT + MINY*height - BOTTOM_MARGIN);
        //p.setTransform( transform );

        drawYTics(p,transform);
        drawXTics(p,transform);

        p.setClip((int)LEFT_MARGIN,(int)TOP_MARGIN,(int)(CWIDTH-(LEFT_MARGIN + RIGHT_MARGIN)),(int)(CHEIGHT-(TOP_MARGIN+BOTTOM_MARGIN)));
        for(DataSet set: DATASETS)
            drawSet(set, p, transform);

        drawKey(p);
    }

    void drawKey(GraphPainter p){

        int count = 0;
        for(DataSet set: DATASETS){

            if(set.label!=null){
                p.setColor(AXIS_COLOR);
                p.drawString(set.label, CWIDTH - 100, 30 + count*15);
                p.setColor(set.COLOR);
                ArrayList<Point2D> pts = new ArrayList<Point2D>();
                pts.add(new Point2D.Double(CWIDTH - 140, 24 + count*15));
                pts.add(new Point2D.Double(CWIDTH - 110, 24 + count*15));

                Point2D middle = new Point2D.Double(CWIDTH - 125, 24 + count*15);
                switch(set.p){

                    case linespoints:
                        set.LINE.drawLine(pts, p);

                    case points:

                        set.POINTS.drawPoint(middle, p);

                        break;

                    case lines:
                        set.LINE.drawLine(pts, p);
                    break;

                }
                count++;
            }


        }


    }

    /**
     *  Takes the full y range and breaks it into 5 tics, uses the transform to place points.
     * @param p
     * @param t
     */
    public void drawYTics(GraphPainter p, AffineTransform t){
        double delta = (MAXY - MINY)/4;

        for(int i = 0; i<5; i++){
            double ynot = MINY + i*delta;
            double xnot = MINX;
            Point2D pt = new Point2D.Double(xnot,ynot);

            t.transform(pt,pt);

            int x0 = (int)pt.getX();
            int x1 = x0+5;
            int y = (int)pt.getY();

            p.drawLine(x0,y,x1,y);
            String value = MessageFormat.format("{0}", ynot);

            p.drawString(MessageFormat.format("{0}",ynot),x0-(int)YTICS_WIDTH,y + 5);

        }
    }

    /**
     *  Takes the full x range and breaks it into 7 tics, uses the transform to place points.
     * @param p
     * @param t
     */
    public void drawXTics(GraphPainter p, AffineTransform t){
        double delta = (MAXX - MINX)/6;

        for(int i = 0; i<7; i++){
            double ynot = MINY;
            double xnot = MINX + delta*i;
            Point2D pt = new Point2D.Double(xnot,ynot);

            t.transform(pt,pt);

            int x = (int)pt.getX();
            int y0 = (int)pt.getY();
            int y1 = y0-5;

            p.drawLine(x,y0,x,y1);

            p.drawString(xtics[i],x-3,y0 + 15);

        }
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
        final BufferedImage image = img;

        IMAGE_LOCK.get();

        ImageIO.write(image, "png",f);

        IMAGE_LOCK.release();

    }

    /**
     * Creates the strings for the width, and calculates their dimensions so that the graph
     * can be appropriately scaled.
     *
     */
    public void createTics(){

        if(XTICS){
            double delta = (MAXX - MINX)/6;
            xtics = new String[7];
            //add padding to the right side of the graph due to overflow of x-tic label.
            int x_overflow = 0;

            for(int i = 0; i<7; i++){
                double xnot = MINX + delta*i;

                String value = MessageFormat.format("{0}",xnot);
                int now_width = SwingUtilities.computeStringWidth(FONT_METRICS,value);
                x_overflow = now_width>x_overflow?now_width:x_overflow;

                xtics[i] = value;
            }

            RIGHT_MARGIN += x_overflow;
        }

        if(YTICS){
            double delta = (MAXY - MINY)/4;
            ytics = new String[5];
            int ytics_width = 0;
            for(int i = 0; i<5; i++){
                double ynot = MINY + i*delta;

                String value = MessageFormat.format("{0}", ynot);

                int now_width = SwingUtilities.computeStringWidth(FONT_METRICS,value);
                ytics_width = ytics_width>now_width?ytics_width:now_width;
                ytics[i] = value;
            }


            YTICS_WIDTH = ytics_width;
            //set in 'reset function.
            //LEFT_MARGIN += YTICS_WIDTH;
        }

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

            double mnx = Double.MAX_VALUE;
            double mny = Double.MAX_VALUE;
            double mxx = -Double.MAX_VALUE;
            double mxy = -Double.MAX_VALUE;

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
                //System.out.println("Warner: X-Range is zero rescaline");
                MAXX++;
                MINX--;
            }

            MAXY = AUTOY?mxy:MAXY;
            MINY = AUTOY?mny:MINY;

            if(AUTOY&&MAXY==MINY){
                //System.out.println("Warner: Y-Range is zero rescaline");
                MAXY++;
                MINY--;
            }

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

    public int dataSetCount(){

        return DATASETS.size();
        
    }

    /**
     * Append a data point to an existing data set.
     *
     * @param set
     * @param x
     * @param y
     */
    public void appendPoint(int set, double x, double y){

        DATASETS.get(set).addPoint(x,y);
        SCALE = true;

    }


    /**
     * Shows the graph in its own JFrame
     *
     */
    public void show(){
        show(true);

    }
    public void show(boolean exit_on_close, String window_title){
        if(frame!=null){

            frame.setVisible(true);
            return;

        }

        GraphFrame y = new GraphFrame(window_title);
        //y.setSize(640,480);
        if(exit_on_close)
            y.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        resetGraph();

        panel = new GraphPanel(img);

        y.setGraph(this);
        y.pack();
        y.setVisible(true);

        frame = y;

    }

    public void show(boolean exit_on_close){

        show(exit_on_close, "Graph Panel");

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

    /**
     * This does all of the reset/repaint functions.  Additionaly
     * it will request a rescale.
     *
     *
     * @param rescale if the auto scale is performed.
     */
    public void refresh(boolean rescale){
        SCALE = rescale||SCALE;
        resetGraph();
        repaint();

    }


 
}

class GraphMutex{
    boolean HELD = false;
    synchronized public void get(){
        while(HELD){
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

