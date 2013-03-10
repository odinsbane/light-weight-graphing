package lightgraph;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

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
                       
   }


}