package lightgraph;

import lightgraph.painters.GraphPainter;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * For drawing lines.
 * 
 * User: mbs207
 * Date: May 20, 2010
 * Time: 6:48:46 AM
 */
public abstract class GraphLine {
    double WIDTH = 1;
    abstract public void drawLine(ArrayList<Point2D> pts, GraphPainter painter);
    public double getLineWidth(){
        return WIDTH;
    }

    public void setLineWidth(double w){
        WIDTH = w;
    }


    /**
     * Default line implementation.
     *
     * @return solid line.
     */
    public static GraphLine solidLine(){
        return new GraphLine(){
            public void drawLine(ArrayList<Point2D> pts, GraphPainter painter){
                painter.setLineWidth(WIDTH);
                Path2D path = new Path2D.Double();
                if(pts.size()>0){
                    Point2D pt = pts.get(0);
                    path.moveTo(pt.getX(), pt.getY());
                    for(int i = 1; i<pts.size(); i++){
                        pt = pts.get(i);

                        path.lineTo(pt.getX(), pt.getY());
                    }
                    painter.drawPath(path);
                }

                painter.restoreLineWidth();
                

            }
        };

    }

    /**
     * Line that is drawn using dashes, according to the java2D BasicStroke.
     *
     * @param d array of dashes @see java.awt.BasicStroke
     * @return a line that will draw with dashes.
     */
    public static GraphLine dashes(float[] d){
        final float[] dashes = d;
        return new GraphLine(){
            public void drawLine(ArrayList<Point2D> pts, GraphPainter painter){
                painter.setLineWidth(WIDTH);
                painter.setDashes(dashes);
                Path2D path = new Path2D.Double();
                if(pts.size()>0){
                    Point2D pt = pts.get(0);
                    path.moveTo(pt.getX(), pt.getY());
                    for(int i = 1; i<pts.size(); i++){
                        pt = pts.get(i);

                        path.lineTo(pt.getX(), pt.getY());
                    }
                    painter.drawPath(path);
                }

                painter.setDashes(null);
                painter.restoreLineWidth();


            }
        };
    }

    /**
     * Preset dash configuration.
     * @return dashed line.
     */
    public static GraphLine shortDashes(){
        return dashes(new float[]{4,2});
    }

    /**
     * Preset dash configuration.
     * @return dashed line.
     */
    public static GraphLine longDashes(){
        return dashes(new float[]{10,2});
    }

    /**
     * Preset dash configuration.
     * @return dashed line.
     */
    public static GraphLine longShortDashes(){
        return dashes(new float[]{10,2,4,2});
    }

    /**
     * Preset dash configuration.
     * @return dashed line.
     */
    public static GraphLine longShortShortDashes(){
        return dashes(new float[]{10,2,4,2,4,2});
    }

    /**
     * Gets a set of default lines, primarily used for the GraphFormatWindow
     *
     * @return List containing default Graphline implementations.
     */
    public static ArrayList<GraphLine> getLines(){
        ArrayList<GraphLine> lines = new ArrayList<GraphLine>();
        lines.add(solidLine());
        lines.add(shortDashes());
        lines.add(longDashes());
        lines.add(longShortDashes());
        lines.add(longShortShortDashes());
        return lines;
    }


}
