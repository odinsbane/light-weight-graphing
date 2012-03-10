package lightgraph.painters;

import java.awt.*;

/**
 * New imagej plugin that ...
 * User: mbs207
 * Date: 1/6/12
 * Time: 10:26 AM
 */
public class PanelPainter implements GraphPainter{

    Graphics2D g;
    final Stroke default_stroke;

    public PanelPainter(Graphics2D g){
        this.g = g;
        default_stroke = g.getStroke();
    }
    public void drawEllipse(Shape s){
        g.draw(s);
    }

    public void drawPath(Shape s){

        g.draw(s);

    }

    public void setColor(Color c){
        g.setColor(c);
    }

    public Color getColor(){
        return g.getColor();
    }

    public void drawLine(int x0, int y0, int x1, int y1) {
        g.drawLine(x0,y0,x1,y1);
    }

    public void fill(Shape s){
        g.fill(s);
    }

    public void setClip(int x, int y, int w, int h){
        g.setClip(x,y,w,h);
    }

    public void drawString(String s, int x, int y) {
        g.drawString(s,x,y);
    }

    public void setLineWidth(double width){
        Stroke s = new BasicStroke((float)width);
        g.setStroke(s);

    }

    public void restoreLineWidth(){
        g.setStroke(default_stroke);
    }


}