package lightgraph;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Created by melkor on 2/15/16.
 */
public class CheckFonts {

    public static void main(String[] args){

        double[] x = new double[20];
        double[] y = new double[20];
        for(int i = 0; i<20; i++){

            x[i] = 0.1*i;
            y[i] = Math.exp(-x[i]);

        }
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(String font: fonts){
            System.out.println(font);
        }
        Graph a = new Graph();
        a.addData(x, y);
        a.setXLabel("no font x");
        a.setYLabel("no font y");
        a.setTitle("no font title");
        a.show(true);


        Graph b = new Graph();
        DataSet set = b.addData(x, y);
        set.setLineWidth(2.0);
        set.setPoints(GraphPoints.outlinedCircles());
        set.setColor(Color.BLUE);

        System.out.println(Font.getFont("Liberation Sans"));
        b.setTitleFont(new LGFont("Helvetica",Font.BOLD, 14));
        b.setLabelFont(new LGFont("Helvetica", Font.PLAIN, 12));
        b.setTicFont(new LGFont("Helvetica",Font.PLAIN, 10));
        b.setXLabel("Arial Labels");
        b.setYLabel("Arial Labels");
        b.setTitle("Helvetica Titles");

        b.show(true);
    }
}
