package lightgraph.painters;

import java.awt.*;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;

/**
 * For painting a graph, the implementations either use a graphics object
 * or paint to an svg file.
 *
 * @author mbs207
 */
public interface GraphPainter {
    public void drawEllipse(Shape s);
    public void drawPath(Shape s);
    public void setColor(Color c);
    public Color getColor();
    public void drawLine(int x0, int y0, int x1, int y1);
    public void fill(Shape s);
    public void setClip(int x, int y, int w, int h);
    public void drawString(String s, int x, int y);
}