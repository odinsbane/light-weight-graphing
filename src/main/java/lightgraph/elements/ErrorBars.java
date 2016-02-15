package lightgraph.elements;

import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.painters.GraphPainter;

import java.awt.geom.Point2D;

/**
 * Error bars can be added to a Dataset. The error bars are added by adding the values to
 * the dataset that the error bars should be applied to. They will take the point size
 * and line weight of the associated points, or if the dataset does not have a point
 * the line will be checked. If neither of these are set, then the values will remain the last
 * value set.
 *
 * Created by melkor on 23/02/14.
 */
public class ErrorBars {
    double SCALEX = 1;
    double SCALEY = 1;

    public final static int XAXIS = 0;
    public final static int YAXIS = 1;

    double[] x_errors;
    double[] y_errors;
    private double size = 4;
    private double weight = 1;

    /**
     * For applying Error bars along a single, x or y, axis.
     *
     * @param errors values that are applied at each point.
     * @param axis use the static fields to determine which axis.
     */
    public ErrorBars(double[] errors, int axis){

        if(axis == XAXIS){
            x_errors=errors;
        } else if (axis == YAXIS){
            y_errors = errors;
        } else{
            throw new IllegalArgumentException(String.format("Not a valid axis: %d", axis));
        }

    }

    public ErrorBars(double[] xerrors, double[] yerrors){
        x_errors = xerrors;
        y_errors = yerrors;
    }


    /**
     * For correctly scaling the error bars on the graph. This is handled during
     * the paint dataset routine.
     *
     * @param scale_x
     * @param scale_y
     */
    public void setScale(double scale_x, double scale_y) {
        SCALEX = scale_x;
        SCALEY = scale_y;
    }

    /**
     * This will draw a set of error bars using the supplied painter at the location
     * provided. The scale needs to have been set to have error bars that correctly reflect
     * the scale.
     *
     * The method is candidate for being made abstract, and different error bars could be used.
     *
     * @param i index of the correspoonding point.
     * @param pt scaled position that the point will be drawn.
     * @param painter object used to draw the error bars.
     */
    public void drawErrorAt(int i, Point2D pt, GraphPainter painter) {
        if(x_errors!=null && i<x_errors.length){
            //draw x error bars
            painter.setLineWidth(weight);
            double leg = x_errors[i]*SCALEX;
            double end = size/2;
            painter.drawLine(pt.getX()-leg, pt.getY(), pt.getX()+leg, pt.getY());
            painter.drawLine(pt.getX()-leg, pt.getY()+end, pt.getX()-leg, pt.getY()-end);
            painter.drawLine(pt.getX()+leg, pt.getY()+end, pt.getX()+leg, pt.getY()-end);
            painter.restoreLineWidth();
        }
        if(y_errors!=null&& i<y_errors.length){
            //draw y error bars
            painter.setLineWidth(weight);
            double leg = y_errors[i]*SCALEY;
            double end = size/2;
            painter.drawLine(pt.getX(), pt.getY()+leg, pt.getX(), pt.getY()-leg);
            painter.drawLine(pt.getX()-end, pt.getY()+leg, pt.getX()+end, pt.getY()+leg);
            painter.drawLine(pt.getX()-end, pt.getY()-leg, pt.getX()+end, pt.getY() - leg);
            painter.restoreLineWidth();
        }


    }

    /**
     * sets the line weight
     * @param v
     */
    public void setWeight(double v) {
        weight = v;
    }

    /**
     * Sets the size, for the current drawing method the size is the size of the end
     * caps.
     *
     * @param size
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     * Method for testing.
     *
     * @param args
     */
    public static void main(String[] args){

        int n = 20;
        double[] x = new double[n], y=new double[n], z= new double[n], y1=new double[n], y2=new double[n], z2=new double[n];
        for(int i = 0; i<n; i++){

            x[i] = 0.25*i;

            y[i] = 3.5 - x[i]*x[i];
            y1[i] = y[i] + 10;
            y2[i] = y[i] - 10;
            z[i] = Math.sqrt(x[i]);
            z2[i] = 0.1*z[i];
        }
        Graph g = new Graph();

        DataSet set = g.addData(x, y);
        set.addYErrorBars(z);

         set = g.addData(x, y1);
        set.addXErrorBars(z2);

         set = g.addData(x, y2);
        set.addXYErrorBars(z2, z);


        g.show(true, "Error Bar Test");
    }

}
