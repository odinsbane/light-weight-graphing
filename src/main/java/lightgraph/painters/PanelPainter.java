package lightgraph.painters;

import lightgraph.LGFont;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * New imagej plugin that ...
 * User: mbs207
 * Date: 1/6/12
 * Time: 10:26 AM
 */
public class PanelPainter implements GraphPainter{

    Graphics2D g;
    final Stroke default_stroke;
    Color BACKGROUND;
    private boolean FILL = false;
    float dashes[];
    float width;
    LGFont font;
    public PanelPainter(Graphics2D g, Color background){
        this.g = g;
        default_stroke = g.getStroke();
        BACKGROUND = background;
    }

    public void drawPath(Shape s){
        if(FILL){
            Color fore = g.getColor();
            g.setColor(BACKGROUND);
            g.fill(s);
            g.setColor(fore);
        }
        g.draw(s);

    }

    public void setColor(Color c){
        g.setColor(c);
    }

    public Color getColor(){
        return g.getColor();
    }

    public void drawLine(double x0, double y0, double x1, double y1) {
        g.drawLine((int)x0,(int)y0,(int)x1,(int)y1);
    }

    public void fill(Shape s){
        g.fill(s);
    }

    public void setClip(int x, int y, int w, int h){
        g.setClip(x,y,w,h);
    }

    public void drawString(String s, double x, double y) {
        g.setFont(font.getAwtFont());

        //find the number of empty leading spaces.
        int empty = 0;
        int offset = 0;
        while(s.charAt(empty)==' '&&empty<=s.length()){
            empty++;
        }
        if(empty>0){
            offset = getStringWidth(" ")*empty;
        }
        g.drawString(s,(int)x + offset,(int)y);
    }

    public void setLineWidth(double width){
        this.width = (float)width;
        updateStroke();
    }

    /**
     * Not implemented yet
     * @param dashes
     */
    public void setDashes(float[] dashes){
        this.dashes = dashes;
        updateStroke();
    }

    void updateStroke(){
        if(dashes==null){
            Stroke s = new BasicStroke(width);
            g.setStroke(s);
        } else{
            Stroke s = new BasicStroke(
                    width,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,1f,
                    dashes, 0);
            g.setStroke(s);
        }


    }
    public void restoreLineWidth(){
        g.setStroke(default_stroke);
    }

    /**
     * For filling shapes with background color.
     */
    public void setFill(boolean fill){
        FILL = fill;
    }

    /**
     * No groups in java2D
     */
    public void startGroup() {
    }

    public void endGroup() {
    }

    @Override
    public void setFont(LGFont font) {
        this.font = font;
    }

    @Override
    public int getStringWidth(String label) {
        FontMetrics metrics = g.getFontMetrics();
        return metrics.stringWidth(label);
    }

    @Override
    public void drawVerticalString(String s, double x, double y) {
        g.setFont(font.getAwtFont());
        FontMetrics metrics = g.getFontMetrics();
        AffineTransform ft = AffineTransform.getRotateInstance(-0.5*Math.PI);

        Font f = g.getFont();
        Font transformed= f.deriveFont(ft);
        //g.setTransform(t);
        g.setFont(transformed);

        int start = (int)y;
        for(int i = 0; i<s.length(); i++){
            String ss = s.substring(i,i+1);
            int l = metrics.stringWidth(s.substring(i,i+1));
            g.drawString(ss,(int)x,start);
            start-=l;
        }


        //g.drawString(s,(int)x,(int)y);

        g.setFont(f);
    }


}