/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lightgraph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author mbs207
 */
public abstract class GraphPoints {
    int SIZE = 12;
    int WEIGHT = 1;
    abstract void drawPoint(Point2D pt, GraphPainter painter);
    public void setSize(int s){
        SIZE = s;
    }

    public void setWeight(int w){
        WEIGHT = w;
    }
    /*
    static GraphPoints hollowSquares(){
        return new GraphPoints(){

            void drawPoint(Graphics g){

            }

        };
    }

    static GraphPoints filledSquares(){
        return new GraphPoints(){

            void drawPoint(Graphics g){

            }

        };
    }

    static GraphPoints hollowDiamonds(){
        return new GraphPoints(){

            void drawPoint(Graphics g){

            }

        };
    }
    */
    public static GraphPoints dots(){
        GraphPoints gp = new GraphPoints(){
            RectangularShape shape = new Ellipse2D.Double(0,0,2, 2);
            void drawPoint(Point2D pt, GraphPainter painter){
                shape.setFrame(pt.getX()-1, pt.getY() - 1,2, 2);
                painter.fill(shape);
            }

        };
        return gp;
    }
    static GraphPoints hollowCircles(){
        GraphPoints gp = new GraphPoints(){
            RectangularShape shape = new Ellipse2D.Double(0,0,SIZE, SIZE);
            //Dimension d = new Dimension(12,12);
            void drawPoint(Point2D pt, GraphPainter painter){
                shape.setFrame(pt.getX() - SIZE/2, pt.getY() - SIZE/2,SIZE, SIZE);
                painter.drawEllipse(shape);
            }

        };
        
        return gp;

    }

}

