/*
 * 
 */

package lightgraph;

import lightgraph.gui.GraphFrame;
import lightgraph.painters.GraphPainter;
import lightgraph.painters.PanelPainter;
import lightgraph.painters.SvgPainter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;

import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.FontMetrics;


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
    int XTIC_COUNT;
    boolean YTICS;
    int YTIC_COUNT;
    boolean TITLE;
    public double MINX;
    public double MINY;
    public double MAXX;
    public double MAXY;
    double PADDING = 10;
    double YTICS_WIDTH = 30;
    double XTICS_HEIGHT = 20;
    double TITLE_HEIGHT= 0;
    double FONT_HEIGHT = 12;

    double LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN;


    FontMetrics FONT_METRICS;
    /** indicates a pending scale */
    boolean SCALE;
    /**canvas height and width*/
    public int CHEIGHT;
    public int CWIDTH;

    double KEY_X, KEY_Y;
    boolean KEY_POSITION_SET=false;

    Color AXIS_COLOR, BACKGROUND;
    BufferedImage img;
    public ArrayList<DataSet> DATASETS;

    String[] xtics, ytics;
    String xlabel, ylabel, title;

    public GraphPanel panel;
    GraphMutex IMAGE_LOCK = new GraphMutex();
    private JFrame frame;
    private LGFont ticFont = new LGFont("Liberation", Font.PLAIN, 10);
    private LGFont titleFont = new LGFont("Liberation", Font.PLAIN, 14);
    private LGFont labelFont = new LGFont("Liberation", Font.PLAIN, 12);

    public Graph(){
        MINX = Double.MAX_VALUE;
        MINY = Double.MAX_VALUE;
        MAXX = -Double.MAX_VALUE;
        MAXY = -Double.MAX_VALUE;

        AUTOX = true;
        AUTOY = true;
        
        CHEIGHT = 480;
        CWIDTH = 640;

        KEY_X = 100;
        KEY_Y = 45;

        XTICS = true;
        XTIC_COUNT = 7;
        YTICS = true;
        YTIC_COUNT = 5;

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
        resetGraph(new PanelPainter(g, BACKGROUND));
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
            LEFT_MARGIN += FONT_HEIGHT*1.2;
        }
        if(YLABEL){
            BOTTOM_MARGIN += FONT_HEIGHT*1.2;
        }

        if(TITLE){

            TOP_MARGIN += FONT_HEIGHT*1.2;

        }

        p.setColor(BACKGROUND);
        p.fill(new Rectangle2D.Double(0,0,CWIDTH,CHEIGHT));

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
        drawLabels(p);

        p.setClip((int)LEFT_MARGIN,(int)TOP_MARGIN,(int)(CWIDTH-(LEFT_MARGIN + RIGHT_MARGIN)),(int)(CHEIGHT-(TOP_MARGIN+BOTTOM_MARGIN)));
        for(DataSet set: DATASETS)
            drawSet(set, p, transform);

        drawKey(p);


    }

    void drawLabels(GraphPainter p){
        if(XLABEL){
            double width = (CWIDTH - LEFT_MARGIN - RIGHT_MARGIN);
            int length = p.getStringWidth(xlabel);
            double offset = (width - length)/2 + LEFT_MARGIN;
            p.setFont(labelFont);
            p.drawString(xlabel,offset,CHEIGHT - 1.2*FONT_HEIGHT);



        }

        if(YLABEL){
            double height = (CHEIGHT - TOP_MARGIN - BOTTOM_MARGIN);

            int length = p.getStringWidth(ylabel);
            double offset = (height - length)/2 + TOP_MARGIN + length;
            p.drawVerticalString(ylabel,1.2*FONT_HEIGHT,offset);
        }

        if(TITLE){

            double width = (CWIDTH - LEFT_MARGIN - RIGHT_MARGIN);
            int length = p.getStringWidth(title);
            double offset = (width - length)/2 + LEFT_MARGIN;
            p.setFont(titleFont);
            p.drawString(title,offset,1.2*FONT_HEIGHT);

        }




    }

    void drawKey(GraphPainter p){

        int count = 0;
        p.startGroup();
        if(!KEY_POSITION_SET){
            int max = 0;
            for(DataSet set: DATASETS){

                if(set.label!=null){
                    int width = p.getStringWidth(set.label);
                    if(width>max) max = width;
                }


            }
            //8 for some space, 50 for the line, RIGHT_MARGIN to actually be in the graph.
            KEY_X = max + 8 + 50 + RIGHT_MARGIN;
        }
        for(DataSet set: DATASETS){

            if(set.label!=null){
                double top = KEY_Y + FONT_HEIGHT*1.4*count;
                double left = CWIDTH - KEY_X;
                p.setColor(AXIS_COLOR);
                p.setFont(labelFont);
                p.drawString(set.label, left + 50, top);
                p.setColor(set.COLOR);
                ArrayList<Point2D> pts = new ArrayList<Point2D>();

                double mark_y = top - FONT_HEIGHT*0.4;

                pts.add(new Point2D.Double(left + 5, mark_y));
                pts.add(new Point2D.Double(left + 45, mark_y));

                Point2D middle = new Point2D.Double(left+25, mark_y);

                if(set.LINE!=null){
                    set.LINE.drawLine(pts, p);
                }
                if(set.POINTS!=null){
                    set.POINTS.drawPoint(middle,p);
                }

                count++;
            }


        }
        p.endGroup();


    }

    /**
     *  Takes the full y range and breaks it into 5 tics, uses the transform to place points.
     * @param p
     * @param t
     */
    public void drawYTics(GraphPainter p, AffineTransform t){
        double delta = (MAXY - MINY)/(YTIC_COUNT-1);
        int max = 0;
        for(String tic: ytics){
            max = tic.length()>max?tic.length():max;
        }
        p.startGroup();
        for(int i = 0; i<YTIC_COUNT; i++){
            double ynot = MINY + i*delta;
            double xnot = MINX;
            Point2D pt = new Point2D.Double(xnot,ynot);

            t.transform(pt,pt);

            double x0 = pt.getX();
            double x1 = x0+5;
            double y = pt.getY();

            p.drawLine(x0,y,x1,y);

        }
        p.endGroup();

        String format = "%"+max+"s";
        int w = getSpaceWidth(ticFont);
        p.startGroup();
        for(int i = 0; i<YTIC_COUNT; i++){
            double ynot = MINY + i*delta;
            double xnot = MINX;
            Point2D pt = new Point2D.Double(xnot,ynot);

            t.transform(pt,pt);

            double x0 = pt.getX();
            double y = pt.getY();

            p.setFont(ticFont);

            String tic = String.format(format, ytics[i]);
            int empties = tic.lastIndexOf(' ');
            empties += 1;
            p.drawString(tic,x0-(int)YTICS_WIDTH + w*empties,y + 5);

            System.out.println(String.format(format, ytics[i]));
        }
        p.endGroup();
    }

    public int getSpaceWidth(LGFont font){
        Graphics g = img.getGraphics();
        font.getSize();
        Font f = font.getAwtFont();
        f.deriveFont(font.getSize());
        g.setFont(f);

        int value = SwingUtilities.computeStringWidth(g.getFontMetrics()," ");
        g.dispose();
        return value;
    }

    /**
     *  Takes the full x range and breaks it into 7 tics, uses the transform to place points.
     * @param p
     * @param t
     */
    public void drawXTics(GraphPainter p, AffineTransform t){
        double delta = (MAXX - MINX)/(XTIC_COUNT-1);

        int max = 0;

        //x position to be left-justified.
        p.startGroup();
        for(int i = 0; i<XTIC_COUNT; i++){
            double ynot = MINY;
            double xnot = MINX + delta*i;
            Point2D pt = new Point2D.Double(xnot,ynot);

            t.transform(pt,pt);

            double x = pt.getX();
            double y0 = pt.getY();
            double y1 = y0-5;

            p.drawLine(x,y0,x,y1);
            String tl = xtics[i];
            max = max>tl.length()?max:tl.length();

        }
        p.endGroup();

        String format = "%s";
        p.startGroup();
        for(int i = 0; i<XTIC_COUNT; i++){
            double ynot = MINY;
            double xnot = MINX + delta*i;
            Point2D pt = new Point2D.Double(xnot,ynot);

            t.transform(pt,pt);

            double x = pt.getX();
            double y0 = pt.getY();

            p.setFont(ticFont);
            p.drawString(String.format(format, xtics[i]),x-3,y0 + 15);

        }
        p.endGroup();
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
        if(set.LINE!=null){
            set.LINE.drawLine(pts, painter);
        }

        if(set.ERRORS!=null){
            if(set.POINTS!=null){
                set.ERRORS.setWeight(set.POINTS.WEIGHT);
                set.ERRORS.setSize(set.POINTS.SIZE);
            } else if(set.LINE!=null){
                set.ERRORS.setWeight(set.LINE.WIDTH);
            }
            painter.startGroup();
            double scale_x = transform.getScaleX();
            double scale_y = transform.getScaleY();
            set.ERRORS.setScale(scale_x, scale_y);
            for(int i = 0; i<pts.size(); i++){
                set.ERRORS.drawErrorAt(i, pts.get(i), painter);
            }
            painter.endGroup();
        }

        if(set.POINTS!=null){
            painter.startGroup();
            for(Point2D pt: pts)
                set.POINTS.drawPoint(pt, painter);
            painter.endGroup();
        }


    }

    public void setXLabel(String label){
        XLABEL = true;
        xlabel = label;

    }

    public void setYLabel(String label){
        YLABEL = true;
        ylabel = label;
    }

    public void setTitle(String label){
        TITLE = true;
        title = label;
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

    public void saveSvg(File f){
        SvgPainter painter = new SvgPainter(CHEIGHT, CWIDTH, getBackground());
        resetGraph(painter);
        painter.finish(f);
    }

    /**
     * Creates the strings for the width, and calculates their dimensions so that the graph
     * can be appropriately scaled.
     *
     */
    public void createTics(){

        if(XTICS){
            double delta = (MAXX - MINX)/(XTIC_COUNT-1);
            xtics = new String[XTIC_COUNT];
            //add padding to the right side of the graph due to overflow of x-tic label.
            int x_overflow = 0;

            for(int i = 0; i<XTIC_COUNT; i++){
                double xnot = MINX + delta*i;

                String value = MessageFormat.format("{0}",xnot);
                int now_width = SwingUtilities.computeStringWidth(FONT_METRICS,value);
                x_overflow = now_width>x_overflow?now_width:x_overflow;

                xtics[i] = value;
            }

            RIGHT_MARGIN += x_overflow;
        }

        if(YTICS){
            double delta = (MAXY - MINY)/(YTIC_COUNT-1);
            ytics = new String[YTIC_COUNT];
            int ytics_width = 0;
            for(int i = 0; i<YTIC_COUNT; i++){
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


    public void setContentSize(int width, int height){

        if(XTICS||YTICS){
            createTics();
        }

        if(XTICS){
            width += YTICS_WIDTH;
        }

        if(YTICS){

            height += XTICS_HEIGHT;

        }

        if(CWIDTH==width || CHEIGHT==height)
            return;

        CWIDTH = width;
        CHEIGHT = height;
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

    public void setYTicCount(int c){
        if(c==0){
            YTICS=false;
        } else{
            YTICS=true;
        }
        YTIC_COUNT = c;

    }

    public void setXTicCount(int c){
        XTIC_COUNT = c;
        if(c==0){
            XTICS=false;
        } else{
            XTICS=true;
        }
    }

    public int getXTicCount(){
        return XTIC_COUNT;
    }

    public int getYTicCount(){
        return YTIC_COUNT;
    }

    public void setBackground(Color c){
        BACKGROUND = c;
    }

    public void setAxisColor(Color c){
        AXIS_COLOR=c;
    }

    public Color getBackground(){
        return BACKGROUND;
    }

    public String getXLabel(){
        return xlabel;
    }

    public String getYLabel(){
        return ylabel;
    }

    public String getTitle(){
        return title;
    }
    /**
     * Sets the distance from the right edge of the graph for the top left
     * cornder of the key.
     *
     * @param x distance from right edge of graph.
     */
    public void setKeyX(double x){
        KEY_X = x;
        KEY_POSITION_SET=true;
    }

    /**
     * Sets the distance from the top of the graph to the top of the key.
     * @param y
     */
    public void setKeyY(double y){
        KEY_Y = y;
    }


    public double getKeyX(){
        return KEY_X;
    }

    public double getKeyY(){
        return KEY_Y;
    }

    public GraphPanel getGraphPanel() {
        if (panel == null) {
            panel=new GraphPanel(img);
        }
        return panel;
    }

    /**
     * Based on the coordinates of the panel, gets the coordinates in data space.
     *
     * @param panel_x x position on the panel.
     * @param panel_y y position on the panel.
     * @return the position in data space.
     */
    public double[] getDataCoordinates(double panel_x, double panel_y){
       //the current position of the click in data space scaled to pixels.
        double data_x = panel_x - LEFT_MARGIN;
        double data_y = CHEIGHT - BOTTOM_MARGIN - panel_y;
        double real_x = MINX + (MAXX - MINX)*data_x/(CWIDTH-LEFT_MARGIN-RIGHT_MARGIN);
        double real_y = MINY + (MAXY - MINY)*data_y/(CHEIGHT-BOTTOM_MARGIN-TOP_MARGIN);

        return new double[]{real_x, real_y};
    }

    /**
     * Takes a coordinate in the data/real space and returns a coodinate in image space.
     * @param real_x value of x in data space.
     * @param real_y value of y in data space.
     * @return {x,y} in px in image space. The image is the buffered image used to draw on a panel.
     */
    public double[] getImageCoordinates(double real_x, double real_y){

        double panel_x = LEFT_MARGIN + (real_x - MINX)*(CWIDTH-LEFT_MARGIN-RIGHT_MARGIN)/(MAXX - MINX);
        double panel_y = CHEIGHT - BOTTOM_MARGIN - (real_y - MINY)*(CHEIGHT - BOTTOM_MARGIN - TOP_MARGIN)/(MAXY - MINY);

        return new double[]{panel_x, panel_y};
    }

    public void setTitleFont(LGFont font) {
        titleFont = font;
    }

    public void setLabelFont(LGFont labelFont) {
        this.labelFont = labelFont;
    }

    public void setTicFont(LGFont ticFont) {
        this.ticFont = ticFont;
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
        notifyAll();
    }


}

