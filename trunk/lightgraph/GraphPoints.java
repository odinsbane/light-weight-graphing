/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lightgraph;

import lightgraph.painters.GraphPainter;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mbs207
 */
public abstract class GraphPoints {
    double SIZE = 6;
    double WEIGHT = 1;
    public abstract void drawPoint(Point2D pt, GraphPainter painter);

    /**
     * Sets the size of the graph points, the implementation needs to
     * implement this parameter.
     *
     * @param s
     */
    public void setSize(double s){
        SIZE = s;
    }

    public void setWeight(double w){
        WEIGHT = w;
    }

    public static GraphPoints hollowSquares(){
        return new GraphPoints(){
            Rectangle2D bounds = new Rectangle2D.Double(0,0,SIZE,SIZE);
            Point2D corner = new Point2D.Double(0,0);


            public void drawPoint(Point2D pt, GraphPainter painter){
                painter.setLineWidth(WEIGHT);
                double leg = SIZE/2;
                corner.setLocation(pt.getX() + leg, pt.getY() + leg);
                bounds.setFrameFromCenter(pt,corner);
                painter.drawPath(bounds);
                painter.restoreLineWidth();
            }

        };
    }

    public static GraphPoints crossX(){
        return new GraphPoints(){

            public void drawPoint(Point2D pt, GraphPainter painter){
                painter.setLineWidth(WEIGHT);
                double leg = SIZE/2;
                painter.drawLine(pt.getX()-leg, pt.getY()-leg, pt.getX()+leg, pt.getY()+leg);
                painter.drawLine(pt.getX()-leg, pt.getY()+leg, pt.getX()+leg, pt.getY()-leg);
                painter.restoreLineWidth();
            }

        };
    }

    public static GraphPoints crossPlus(){
        return new GraphPoints(){

            public void drawPoint(Point2D pt, GraphPainter painter){
                painter.setLineWidth(WEIGHT);
                double leg = SIZE/2;
                painter.drawLine(pt.getX()-leg, pt.getY(), pt.getX()+leg, pt.getY());
                painter.drawLine(pt.getX(), pt.getY()+leg, pt.getX(), pt.getY()-leg);
                painter.restoreLineWidth();
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
                double leg = SIZE/2;

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
                shape.setFrame(pt.getX()-1, pt.getY() - 1,2*WEIGHT, 2*WEIGHT);
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
                painter.setLineWidth(WEIGHT);
                painter.drawEllipse(shape);
                painter.restoreLineWidth();
            }

        };
        
        return gp;

    }

    public static GraphPoints filledCircles(){
        GraphPoints gp = new GraphPoints(){
            RectangularShape shape = new Ellipse2D.Double(0,0,SIZE, SIZE);
            public void drawPoint(Point2D pt, GraphPainter painter){
                shape.setFrame(pt.getX() - SIZE/2, pt.getY() - SIZE/2,SIZE, SIZE);
                painter.fill(shape);
            }

        };

        return gp;

    }

    public static GraphPoints outlinedTriangles(){

        GraphPoints gp = new GraphPoints(){
            GeneralPath shape;

            public void drawPoint(Point2D pt, GraphPainter painter){
                double x = pt.getX();
                double y = pt.getY();

                shape=new GeneralPath();
                shape.moveTo(x,y-SIZE);
                shape.lineTo(Math.sqrt(3)*0.5*SIZE + x, 0.5*SIZE + y);
                shape.lineTo(-Math.sqrt(3)*0.5*SIZE + x,  0.5*SIZE + y);
                shape.closePath();

                painter.fill(shape);

                //store color for undoing.
                Color c = painter.getColor();
                painter.setColor(Color.BLACK);

                painter.setLineWidth(WEIGHT);
                painter.drawPath(shape);
                painter.restoreLineWidth();
                //undo color change.
                painter.setColor(c);

                            }

        };

        return gp;


    }

    public static GraphPoints hollowTriangles(){

        GraphPoints gp = new GraphPoints(){
            GeneralPath shape;

            public void drawPoint(Point2D pt, GraphPainter painter){
                double x = pt.getX();
                double y = pt.getY();

                shape=new GeneralPath();
                shape.moveTo(x,y-SIZE);
                shape.lineTo(Math.sqrt(3)*0.5*SIZE + x, 0.5*SIZE + y);
                shape.lineTo(-Math.sqrt(3)*0.5*SIZE + x,  0.5*SIZE + y);
                shape.closePath();

                painter.setLineWidth(WEIGHT);
                painter.drawPath(shape);
                painter.restoreLineWidth();

            }

        };

        return gp;


    }

    public static GraphPoints filledTriangles(){

        GraphPoints gp = new GraphPoints(){
            GeneralPath shape;
            AffineTransform at;
            {

                at = new AffineTransform();
            }
            public void drawPoint(Point2D pt, GraphPainter painter){
                double x = pt.getX();
                double y = pt.getY();

                shape=new GeneralPath();
                shape.moveTo(x,y-SIZE);
                shape.lineTo(Math.sqrt(3)*0.5*SIZE + x, 0.5*SIZE + y);
                shape.lineTo(-Math.sqrt(3)*0.5*SIZE + x,  0.5*SIZE + y);
                shape.closePath();

                painter.fill(shape);


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
        points.add(filledCircles());
        points.add(hollowSquares());
        points.add(hollowDiamonds());
        points.add(filledSquares());
        points.add(outlinedTriangles());
        points.add(hollowTriangles());
        points.add(filledTriangles());
        return points;
    }

}



