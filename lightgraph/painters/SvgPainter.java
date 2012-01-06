package lightgraph.painters;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;

/**
 * New imagej plugin that ...
 * User: mbs207
 * Date: 1/6/12
 * Time: 10:27 AM
 */
public class SvgPainter implements GraphPainter{
    StringBuilder OUTPUT;
    Color COLOR;
    boolean CLIPPING = false;
    Rectangle clip;
    static final String DOCTYPE = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n" +
                "  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n";
    static final String XML = "<?xml version=\"1.0\" standalone=\"no\"?>\n";
    static final String SVG_TAG = "<svg width=\"%.2fin\" height=\"%.2fin\" viewBox=\"0 0 %s %s\"\n"+
                "    xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n";
    public SvgPainter(int height, int width){
        double aspect = 1.0/width*height;
        double page_width = 6;
        double page_height = page_width*aspect;
        OUTPUT = new StringBuilder();
        OUTPUT.append(XML);
        OUTPUT.append(DOCTYPE);
        String dec = String.format(SVG_TAG,page_width,page_height,width,height);
        OUTPUT.append(dec);

    }

    public void drawEllipse(Shape s) {

        Rectangle r = s.getBounds();
        if(CLIPPING&&!r.intersects(clip))
            return;
        OUTPUT.append("    <ellipse");
        OUTPUT.append(" cx=\"" + (r.getX() + r.getWidth()/2) + '"');
        OUTPUT.append(" cy=\"" + (r.getY() + r.getHeight()/2) + '"');
        OUTPUT.append(" rx=\"" + r.getWidth()/2 + '"');
        OUTPUT.append(" ry=\"" + r.getHeight()/2 + '"');
        String red = Integer.toString(COLOR.getRed(),16);
        if(red.length()==1)
            red = "0" + red;

        String green = Integer.toString(COLOR.getGreen(),16);
        if(green.length()==1)
            green = "0" + green;

        String blue = Integer.toString(COLOR.getBlue(),16);
        if(blue.length()==1)
            blue = "0" + blue;


        OUTPUT.append(" stroke=\"#" + red + green + blue +'"');
        OUTPUT.append(" fill=\"none\"");
        OUTPUT.append(" stroke-width=\"1\" />\n");
    }
    public static String svgColorString(Color c){
        String red = Integer.toString(c.getRed(),16);
        if(red.length()==1)
            red = "0" + red;

        String green = Integer.toString(c.getGreen(),16);
        if(green.length()==1)
            green = "0" + green;

        String blue = Integer.toString(c.getBlue(),16);
        if(blue.length()==1)
            blue = "0" + blue;
        return MessageFormat.format("#{0}{1}{2}", red, green, blue);
    }

    public void drawPath(Shape s) {
        Rectangle r = s.getBounds();

        if(CLIPPING&&!(r.intersects(clip)||clip.contains(r.getX(), r.getY())))
            return;

        OUTPUT.append("<path d=\"\n");
        PathIterator pit = s.getPathIterator(null);
        double[] p = new double[2];
        while(!pit.isDone()){
            char c;
            int t = pit.currentSegment(p);
            switch(t){
                case PathIterator.SEG_MOVETO:
                    c = 'M';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + "\n");
                    break;
                case PathIterator.SEG_LINETO:
                    c = 'L';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + "\n");
                    break;
                case PathIterator.SEG_CLOSE:
                    c = 'Z';
                    OUTPUT.append(c + "\n");
                    break;
                case PathIterator.SEG_CUBICTO:
                    c = 'C';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + ' ' +
                                            p[2] + "," + p[3] + ' ' +
                                            p[4] + "," + p[5] + "\n");
                    break;
                default:
                    c = 'L';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + "\n");
                    break;
            }
            pit.next();

        }
        OUTPUT.append("\"");
        OUTPUT.append(" stroke=\"" + svgColorString(COLOR)  + '"');
        OUTPUT.append(" fill=\"none\"");
        OUTPUT.append(" stroke-width=\"1\" />\n");

    }

    public void setColor(Color c) {
        COLOR = c;
    }

    public void drawLine(int x0, int y0, int x1, int y1) {
        OUTPUT.append(MessageFormat.format("<line x1=\"{0}\" y1=\"{1}\" x2=\"{2}\" y2=\"{3}\" ",x0,y0,x1,y1));
        OUTPUT.append(" stroke=\"" + svgColorString(COLOR)  + '"');
        OUTPUT.append(" fill=\"none\"");
        OUTPUT.append(" stroke-width=\"1\" />\n");
    }

    public void fill(Shape s) {
        Rectangle r = s.getBounds();

        if(CLIPPING&&!r.intersects(clip))
            return;

        OUTPUT.append("<path d=\"\n");
        PathIterator pit = s.getPathIterator(null);
        double[] p = new double[6];
        while(!pit.isDone()){
            char c;
            int t = pit.currentSegment(p);
            switch(t){
                case PathIterator.SEG_MOVETO:
                    c = 'M';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + "\n");
                    break;
                case PathIterator.SEG_LINETO:
                    c = 'L';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + "\n");
                    break;
                case PathIterator.SEG_CLOSE:
                    c = 'Z';
                    OUTPUT.append(c + "\n");
                    break;
                case PathIterator.SEG_CUBICTO:
                    c = 'C';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + ' ' +
                                            p[2] + "," + p[3] + ' ' +
                                            p[4] + "," + p[5] + "\n");
                    break;
                default:
                    c = 'L';
                    OUTPUT.append(c + " " + p[0] + "," + p[1] + "\n");
                    break;
            }
            pit.next();

        }
        OUTPUT.append("\"");
        OUTPUT.append(" stroke=\"none\"");
        OUTPUT.append(" fill=\"" + svgColorString(COLOR)  + '"' + "/>\n");
    }
    public void setClip(int x, int y, int w, int h){

        CLIPPING = true;
        clip = new Rectangle(x,y,w,h);
        OUTPUT.append("<clipPath id=\"graphRegion\">\n");
        OUTPUT.append(MessageFormat.format("<rect x=\"{0}\" y=\"{1}\" width=\"{2}\" height=\"{3}\"\n" +
                "        fill=\"none\" stroke=\"none\"/>", x, y, w, h));
        OUTPUT.append("</clipPath>\n");
        OUTPUT.append("<g clip-path=\"url(#graphRegion)\">\n");

    }

    public void drawString(String s, int x, int y) {
        String tag = MessageFormat.format("<text x=\"{0}\" y=\"{1}\">{2}</text>", x, y, s);
        OUTPUT.append(tag);
    }

    public void finish(File f){
        System.out.println("finsihed");
        if(CLIPPING)
            OUTPUT.append("</g>\n");
        OUTPUT.append("</svg>");

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(OUTPUT.toString());
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}