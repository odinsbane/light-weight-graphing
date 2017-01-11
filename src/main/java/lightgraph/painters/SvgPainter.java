package lightgraph.painters;

import lightgraph.LGFont;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    double LINE_WIDTH=1;
    float[] DASHES;
    Rectangle clip;
    String FILL="none";
    Color BACKGROUND;
    FontMetrics metrics;
    boolean FINISHED=false;
    LGFont font = new LGFont("Arial", "Bold", 12);
    static final String DOCTYPE = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n" +
                "  \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n";
    static final String XML = "<?xml version=\"1.0\" standalone=\"no\"?>\n";
    static final String SVG_TAG = "<svg width=\"%.2fpx\" height=\"%.2fpx\" viewBox=\"0 0 %s %s\"\n"+
                "    xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n";

    /**
     * Creates a new svg painter,which will build a large string containing an svg file to be
     * written to file when completed.
     *
     * @param height of output graph in px
     * @param width of output graph in px
     * @param background background color, primarily used for filling hollow strokes.
     */
    public SvgPainter(int height, int width, Color background){

        OUTPUT = new StringBuilder();
        OUTPUT.append(XML);
        OUTPUT.append(DOCTYPE);
        String dec = String.format(SVG_TAG,width*1f,height*1f,width,height);
        OUTPUT.append(dec);
        BACKGROUND=background;
        this.metrics = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).getGraphics().getFontMetrics();
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

    /**
     * For drawing arbitrary shapes, This will fill with the background color if
     * fill is set to true.
     *
     * @param s shape to be drawn.
     */
    public void drawPath(Shape s) {
        Rectangle r = s.getBounds();

        if(CLIPPING&&!(r.intersects(clip)||clip.contains(r.getX(), r.getY())))
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
        OUTPUT.append(" stroke=\"" + svgColorString(COLOR)  + '"');
        if(DASHES!=null){
            String dash = "";
            int n = DASHES.length;
            for(int i = 0; i<n; i++){
                dash += DASHES[i];
                if(i<n-1){
                    dash += " ";
                }

            }
            OUTPUT.append(" stroke-dasharray=\"" + dash + "\" ");

        }
        OUTPUT.append(MessageFormat.format(" fill=\"{0}\"",FILL));
        OUTPUT.append(String.format(" stroke-width=\"%f\" />\n",LINE_WIDTH));

    }

    public void setColor(Color c) {
        COLOR = c;
    }

    /**
     * For drawing plain lines. Creates a line element.
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     */
    public void drawLine(double x0, double y0, double x1, double y1) {
        if(CLIPPING){
            double x,w;
            if(x0<x1){
                x=x0;
                w=x1-x0;
            } else{
                x=x1;
                w=x0-x1;
            }
            
            double y,h;

            if(y0<y1){
                y=y0;
                h=y1-y0;
            } else{
                y=y1;
                h=y0-y1;
            }

            //only draw lines that intersect somehow.
            if((!clip.contains(x0,y0))&&(!clip.contains(x1,y1))&&(!clip.intersects(new Rectangle2D.Double(x,y,w,h)))){
                return;
            }

        }
        OUTPUT.append(MessageFormat.format("<line x1=\"{0}\" y1=\"{1}\" x2=\"{2}\" y2=\"{3}\" ",x0,y0,x1,y1));
        OUTPUT.append(" stroke=\"" + svgColorString(COLOR)  + '"');
        OUTPUT.append(MessageFormat.format(" fill=\"{0}\"",FILL));
        OUTPUT.append(String.format(" stroke-width=\"%f\" />\n",LINE_WIDTH));
    }

    /**
     * Filling an arbitrary shape with the forground color.
     *
     * @param s
     */
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
                    OUTPUT.append(c);
                    OUTPUT.append("\n");
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
        OUTPUT.append("<defs>\n");
        OUTPUT.append("    <clipPath id=\"graphRegion\">\n");
        OUTPUT.append(MessageFormat.format("    <rect x=\"{0}\" y=\"{1}\" width=\"{2}\" height=\"{3}\"\n" +
                "        fill=\"none\" stroke=\"none\"/>", x, y, w, h));
        OUTPUT.append("    </clipPath>\n");
        OUTPUT.append("</defs>");
        OUTPUT.append("<g clip-path=\"url(#graphRegion)\">\n");

    }

    public void drawString(String s, double x, double y) {
        String tag = MessageFormat.format(
            "<text x=\"{0}\" y=\"{1}\" font-family=\"{3}\" font-size=\"{4}pt\" font-style=\"{5}\" fill=\"{6}\" xml:space=\"preserve\">{2}</text>\n",
            x, y, s, font.getName(), font.getSize(), font.getStyle(), svgColorString(COLOR)
        );
        OUTPUT.append(tag);
    }

    public void finish(File f){
        finish();

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(OUTPUT.toString());
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void finish(){
        if(FINISHED) return;

        FINISHED=true;
        if(CLIPPING)
            OUTPUT.append("</g>\n");
        OUTPUT.append("</svg>");
    }

    public String getOutput(){
        if(!FINISHED) finish();
        return OUTPUT.toString();
    }

    public Color getColor(){
        return COLOR;
    }

    public void setLineWidth(double d){
        LINE_WIDTH=d;

    }

    public void restoreLineWidth(){
        LINE_WIDTH=1;
    }

    /**
     *
     *
     * @param dashes length/offest combo's for formatting line dashes. set to null for none.
     */
    public void setDashes(float[] dashes){
        DASHES = dashes;
    }

    public void setFill(boolean fill){
        if(fill){
            FILL=svgColorString(BACKGROUND);
        } else{
            FILL="none";
        }
    }

    public void startGroup() {
        OUTPUT.append("<g>\n");
    }

    public void endGroup() {
        OUTPUT.append("</g>\n");
    }

    @Override
    public void setFont(LGFont font) {
        this.font = font;
    }

    @Override
    public int getStringWidth(String label) {
        String stripped = stripSvgTags(label);
        return metrics.stringWidth(stripped);
    }

    String stripSvgTags(String tagged){
        return tagged.replaceAll("<[^>]*>", "");
    }

    @Override
    public void drawVerticalString(String s, double x, double y) {

        String tag = MessageFormat.format(
            "<text x=\"{0}\" y=\"{1}\" font-family=\"{3}\" font-size=\"{4}pt\" font-style=\"{5}\" fill=\"{6}\" transform=\"rotate(-90 {0} {1})\" xml:space=\"preserve\">{2}</text>\n",
            x, y, s, font.getName(), font.getSize(), font.getStyle(), svgColorString(COLOR)
        );
        OUTPUT.append(tag);
    }


}