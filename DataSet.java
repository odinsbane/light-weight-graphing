package lightgraph;

import java.awt.Color;
import java.util.ArrayList;
import java.awt.geom.Point2D;

/**
 * This class will hold a set of data it will keep track of color, line types, point types,
 * and weight.
 * 
 * User: mbs207
 * Date: May 20, 2010
 * Time: 6:41:24 AM
 */
public class DataSet {
    Color COLOR;
    GraphPoints POINTS;
    GraphLine LINE;
    ArrayList<Point2D> DATA;
    public PlotType p;
    public DataSet(double[] x, double[] y){

        POINTS = GraphPoints.hollowCircles();
        LINE = GraphLine.solidLine();
        
        COLOR = Color.BLUE;

        DATA = new ArrayList<Point2D>();
        for(int i = 0; i<x.length; i++)
            DATA.add(new Point2D.Double(x[i],y[i]));

        p = PlotType.linespoints;
    }

    public void setColor(Color c){

        COLOR = c;
    }

    public void setLine(GraphLine l){
        LINE = l;
        setType();
    }

    public void setPoints(GraphPoints p){
        POINTS = p;
        setType();
    }

    public void addPoint(double x, double y){
        DATA.add(new Point2D.Double(x,y));
    }
    /**
     * Resets the current 'type' of this dataset for the appropriate
     * data contained w/in.
     */
    public void setType(){
        if(LINE!=null && POINTS==null){
            p = PlotType.lines;
        }else if(LINE==null){
            p = PlotType.points;
        } else
            p = PlotType.linespoints;
    }
}

enum PlotType{
    points,
    lines,
    linespoints;
}
