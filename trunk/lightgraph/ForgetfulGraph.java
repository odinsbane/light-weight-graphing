package lightgraph;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * This class is similar to the Graph class, but it forgets all of the previous data,
 * and just continues to draw over the old graph.
 *
 * User: mbs207
 * Date: Dec 2, 2010
 * Time: 4:21:35 PM
 */
public class ForgetfulGraph extends Graph{
    boolean FIXED = false;

    /**
     * Causes the background to not be repainted and just keep painting over the top.
     */
    public void setFixed(){
        FIXED=true;
    }
    /**
     * Resets the graph and paints it onto a buffered image
     *
     */
    public void resetGraph(){
        IMAGE_LOCK.get();
        Graphics2D g = img.createGraphics();
        resetGraph(new PanelPainter(g));
        g.dispose();
        IMAGE_LOCK.release();
    }

    public void resetGraph(GraphPainter p){
        if(SCALE)
            autoScale();
        double offset = PADDING;
        if(XTICS||YTICS){

            offset += 5;

        }

        if(XLABEL||YLABEL)
            offset += 20;

        //only draw border and reset 1 time.
        if(!FIXED){

            p.setColor(BACKGROUND);
            p.fill(new Rectangle(0,0,CWIDTH,CHEIGHT));

            /*
            [ x']   [  m00  m01  m02  ] [ x ]   [ m00x + m01y + m02 ]
            [ y'] = [  m10  m11  m12  ] [ y ] = [ m10x + m11y + m12 ]
            [ 1 ]   [   0    0    1   ] [ 1 ]   [         1         ]

            double m00, double m10, double m01, double m11, double m02, double m12
             */
            double width = (CWIDTH - PADDING - offset)/CWIDTH;
            double height = (CHEIGHT - PADDING - offset)/CHEIGHT;

            AffineTransform transform = new AffineTransform(width,0.0,0.0,-height,offset,CHEIGHT - offset);
            drawBorder(p,transform);
        }
        double width = (CWIDTH-offset - PADDING)/(MAXX - MINX);
        double height = (CHEIGHT-offset - PADDING)/(MAXY - MINY);

        AffineTransform transform = new AffineTransform(width,0.0,0.0,-height,offset - MINX*width,CHEIGHT + MINY*height - offset);
        //p.setTransform( transform );
        p.setClip((int)offset,(int)offset - 5,(int)(CWIDTH-2*offset + 5),(int)(CHEIGHT-2*offset + 5));
        for(DataSet set: DATASETS){
            drawSet(set, p, transform);
            if(set.DATA.size()>1){
                Point2D pt = set.DATA.get(set.DATA.size()-1);
                set.DATA.clear();
                set.DATA.add(pt);
            }
        }
    }

}
