package lightgraph;

import lightgraph.gui.Paintable;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This will handle swing stuff related to this panel.
 * 
 * User: melkor
 * Date: May 9, 2010
 * Time: 8:39:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphPanel extends JPanel{
    BufferedImage img;
    CopyOnWriteArraySet<Paintable> paintables = new CopyOnWriteArraySet<Paintable>();
    public GraphPanel(BufferedImage img){
        this.img = img;
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }
    public void updateImage(BufferedImage img){
        this.img = img;
        repaint();
    }

    public void updateImageSize(Dimension d){
        setPreferredSize(d);
        setBounds(0,0,d.width, d.height);
        validate();
    }

    @Override
    public void paintComponent(Graphics g){
        //super.paintComponent(g);
        g.drawImage(img,
            0,0,img.getWidth(), img.getHeight(),
            Color.WHITE, this);
        for(Paintable p: paintables){

            p.paint((Graphics2D)g);

        }
   }

   public void addPaintable(Paintable p){
       paintables.add(p);
   }

   public void removePaintable(Paintable p){
       paintables.remove(p);
   }


}