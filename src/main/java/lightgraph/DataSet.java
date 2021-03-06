package lightgraph;

import lightgraph.elements.ErrorBars;

import java.awt.Color;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * This class will hold a set of data it will keep track of color, line types, point types,
 * and weight.
 * 
 * User: mbs207
 * Date: May 20, 2010
 * Time: 6:41:24 AM
 */
public class DataSet implements Iterable<Point2D>{
    public Color COLOR;
    public GraphPoints POINTS;
    public GraphLine LINE;
    ArrayList<Point2D> DATA;
    public ErrorBars ERRORS;

    public String label;
    public DataSet(double[] x, double[] y){

        POINTS = GraphPoints.hollowCircles();
        LINE = GraphLine.solidLine();
        
        COLOR = Color.BLUE;

        DATA = new ArrayList<Point2D>();
        for(int i = 0; i<x.length; i++)
            DATA.add(new Point2D.Double(x[i],y[i]));

    }

    /**
     * Add error bars in the y direction.
     *
     * @param errors
     */
    public void addYErrorBars(double[] errors){
        if(errors.length!=DATA.size()){
            System.err.println("Warning: the number of errors does not equal the number of points");
        }
        ERRORS = new ErrorBars(errors, ErrorBars.YAXIS);
    }

    /**
     * For adding error bars along the x-axis.
     *
     * @param errors
     */
    public void addXErrorBars(double[] errors){
        if(errors.length!=DATA.size()){
            System.err.println("Warning: the number of errors does not equal the number of points");
        }
        ERRORS = new ErrorBars(errors, ErrorBars.XAXIS);
    }

    /**
     * For adding error bars on both axis.
     *
     * @param xerr
     * @param yerr
     */
    public void addXYErrorBars(double[] xerr, double[] yerr){
        if(xerr.length!=DATA.size()||yerr.length!=DATA.size()){
            System.err.println("Warning: the number of errors does not equal the number of points");
        }
        ERRORS = new ErrorBars(xerr, yerr);
    }

    /**
     * remove the error bars.
     */
    public void removeErrorBars(){
        ERRORS = null;
    }

    public void setColor(Color c){

        COLOR = c;
    }

    public void setLine(GraphLine l){
        LINE = l;
    }

    public void setPoints(GraphPoints p){
        POINTS = p;
    }

    public void addPoint(double x, double y){
        DATA.add(new Point2D.Double(x,y));
    }


    public Iterator<Point2D> iterator(){
        return DATA.iterator();
    }

    public void setData(double[] x, double[] y){
        DATA.clear();
        for(int i = 0; i<x.length; i++)
            DATA.add(new Point2D.Double(x[i],y[i]));
    }

    /**
     * Sets the label, if there is a label on an active data set then
     * the key will be drawn.
     *
     * @param l @nullable data set label.  null will make this line be skipped.
     *
     */
    public void setLabel(String l){
        label = l;
    }

    public double getLineWidth(){
        if(LINE==null){
            return 0;
        }else{
            return LINE.getLineWidth();
        }
    }
    public void setLineWidth(double d){
        if(LINE==null) return;

        LINE.setLineWidth(d);
    }

    public double getPointSize(){
        if(POINTS==null){
            return 0;
        } else{
            return POINTS.SIZE;
        }
    }

    public void setPointSize(double d){
        if(POINTS==null) return;

        POINTS.setSize(d);
    }

    public double getPointWeight(){
        if(POINTS==null){
            return 0;
        } else{
            return POINTS.WEIGHT;
        }
    }

    public void setPointWeight(double d){
        if(POINTS==null) return;

        POINTS.setWeight(d);
    }

    public ErrorBars getErrorBars(){
        return ERRORS;
    }


}