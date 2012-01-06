/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lightgraph;

import lightgraph.painters.GraphPainter;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mbs207
 */
public abstract class GraphPoints {
    int SIZE = 6;
    int WEIGHT = 1;
    public abstract void drawPoint(Point2D pt, GraphPainter painter);

    /**
     * Sets the size of the graph points, the implementation needs to
     * implement this parameter.
     *
     * @param s
     */
    public void setSize(int s){
        SIZE = s;
    }

    public void setWeight(int w){
        WEIGHT = w;
    }

    public static GraphPoints hollowSquares(){
        return new GraphPoints(){
            Rectangle2D bounds = new Rectangle2D.Double(0,0,SIZE,SIZE);
            Point2D corner = new Point2D.Double(0,0);


            public void drawPoint(Point2D pt, GraphPainter painter){
                double leg = SIZE/2;
                corner.setLocation(pt.getX() + leg, pt.getY() + leg);
                bounds.setFrameFromCenter(pt,corner);
                painter.drawPath(bounds);
            }

        };
    }

    public static GraphPoints crossX(){
        return new GraphPoints(){

            public void drawPoint(Point2D pt, GraphPainter painter){
                int leg = SIZE/2;
                painter.drawLine((int)pt.getX()-leg, (int)pt.getY()-leg, (int)pt.getX()+leg, (int)pt.getY()+leg);
                painter.drawLine((int)pt.getX()-leg, (int)pt.getY()+leg, (int)pt.getX()+leg, (int)pt.getY()-leg);
            }

        };
    }

    public static GraphPoints crossPlus(){
        return new GraphPoints(){

            public void drawPoint(Point2D pt, GraphPainter painter){
                int leg = SIZE/2;
                painter.drawLine((int)pt.getX()-leg, (int)pt.getY(), (int)pt.getX()+leg, (int)pt.getY());
                painter.drawLine((int)pt.getX(), (int)pt.getY()+leg, (int)pt.getX(), (int)pt.getY()-leg);
            }

        };
    }

    static GraphPoints filledSquares(){
        return new GraphPoints(){

            Rectangle2D bounds = new Rectangle2D.Double(0,0,SIZE,SIZE);
            Point2D corner = new Point2D.Double(0,0);


            public void drawPoint(Point2D pt, GraphPainter painter){
                double leg = SIZE/2;
                corner.setLocation(pt.getX() + leg, pt.getY() + leg);
                bounds.setFrameFromCenter(pt,corner);
                painter.fill(bounds);
            }


        };
    }

    static GraphPoints hollowDiamonds(){
        return new GraphPoints(){

            public void drawPoint(Point2D pt, GraphPainter painter){
                int leg = SIZE/2;

                painter.drawLine((int)pt.getX()-leg, (int)pt.getY(), (int)pt.getX(), (int)pt.getY()+leg);
                painter.drawLine((int)pt.getX(), (int)pt.getY()+leg, (int)pt.getX()+leg, (int)pt.getY());
                painter.drawLine((int)pt.getX()+leg, (int)pt.getY(), (int)pt.getX(), (int)pt.getY()-leg);
                painter.drawLine((int)pt.getX(), (int)pt.getY()-leg, (int)pt.getX()-leg, (int)pt.getY());

            }

        };
    }

    public static GraphPoints dots(){
        GraphPoints gp = new GraphPoints(){
            RectangularShape shape = new Ellipse2D.Double(0,0,2, 2);
            public void drawPoint(Point2D pt, GraphPainter painter){
                shape.setFrame(pt.getX()-1, pt.getY() - 1,2, 2);
                painter.fill(shape);
            }

        };
        return gp;
    }
    public static GraphPoints hollowCircles(){
        GraphPoints gp = new GraphPoints(){
            RectangularShape shape = new Ellipse2D.Double(0,0,SIZE, SIZE);
            public void drawPoint(Point2D pt, GraphPainter painter){
                shape.setFrame(pt.getX() - SIZE/2, pt.getY() - SIZE/2,SIZE, SIZE);
                painter.drawEllipse(shape);
            }

        };
        
        return gp;

    }

    static public List<GraphPoints> getGraphPoints(){
        ArrayList<GraphPoints> points  = new ArrayList<GraphPoints>();
        points.add(crossPlus());
        points.add(crossX());
        points.add(dots());
        points.add(hollowCircles());
        points.add(hollowSquares());
        points.add(hollowDiamonds());
        points.add(filledSquares());

        return points;
    }

}



