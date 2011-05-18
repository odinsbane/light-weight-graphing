package lightgraph;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * For drawing lines, trivial.
 * 
 * User: mbs207
 * Date: May 20, 2010
 * Time: 6:48:46 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GraphLine {
    int WEIGHT = 1;
    abstract void drawLine(ArrayList<Point2D> pts, GraphPainter painter);


    public void setWeight(int w){
        WEIGHT = w;
    }

    public static GraphLine solidLine(){
        return new GraphLine(){
            void drawLine(ArrayList<Point2D> pts, GraphPainter painter){
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
                

            }
        };

    }
}
