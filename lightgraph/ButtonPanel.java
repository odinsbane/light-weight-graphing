package lightgraph;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

/**
 * 
 * User: mbs207
 * Date: Jul 14, 2010
 * Time: 7:41:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class ButtonPanel extends JPanel implements MouseListener {
    Graph GRAPH;
    Dimension SIZE = new Dimension(120,10);
    final int TAB = 20;
    int buttons = 0;
    final JFrame parent;
    ButtonPanel(JFrame parent){
        super();
        this.parent = parent;
        setOpaque(false);
        setSize(SIZE);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addButton("SVG");
        addButton("PNG");
        addButton("DATA");

        
    }

    public void setGraph(Graph g){
        GRAPH = g;
    }
    public Dimension getStaticSize(){
        return SIZE;
    }
    public void paintComponent(Graphics g){

        int w = SIZE.width - TAB;
        int h = SIZE.height - 10;
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(100,100,100,50));

        g2.setComposite(AlphaComposite.SrcAtop);
        g2.fillRect(0,0,w,h);
        g2.setClip(w,0,TAB,50);
        g2.fillRoundRect(w-10,0,TAB+9,49, 8, 8);

        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawRoundRect(w-10,0,TAB+9,49, 8, 8);

        g2.setClip(1,h,w,10);
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.fillRoundRect(-10,h-10,w+9,19, 8, 8);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.drawRoundRect(-10,h-10,w+9,19, 8, 8);

        g2.setClip(null);
        g2.drawLine(w-1,49,w-1,h);
        g2.setComposite(AlphaComposite.SrcAtop);
        super.paintComponents(g);
    }

    public GraphButton addButton(String label){

        GraphButton b = new GraphButton(label);
        add(b);
        buttons++;
        int h = buttons*b.getHeight() + 10;
        SIZE = SIZE.height>h?SIZE:new Dimension(SIZE.width, h);
        b.addMouseListener(this);
        return b;
    }

    public void mouseClicked(MouseEvent e) {
        AbstractButton ab = (AbstractButton)e.getSource();
        
        switch(GraphActions.valueOf(ab.getText())){
            case SVG:
                saveSvg();
                break;
            case PNG:
                savePng();
                break;
            case DATA:
                showData();
                break;
            default:
                System.out.println("not implemented");

        }

    }

    public void showData(){
        EventQueue.invokeLater(DataWindow.createDataWindow(GRAPH));
    }
    public void saveSvg(){
        FileDialog fd = new FileDialog(parent,"Save as SVG file...",FileDialog.SAVE);
        fd.setVisible(true);
        String fname = fd.getFile();
        String dir = fd.getDirectory();
        if (fname!=null) {
            File file = new File(dir,fname);
            SvgPainter painter = new SvgPainter(GRAPH.CHEIGHT, GRAPH.CWIDTH);
            GRAPH.resetGraph(painter);
            painter.finish(file);
        }




    }

    public void savePng(){
        FileDialog fd = new FileDialog(parent,"Save as PNG file...",FileDialog.SAVE);
        fd.setVisible(true);
        String fname = fd.getFile();
        String dir = fd.getDirectory();
        try {
            GRAPH.savePng(new File(dir,fname));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

class GraphButton extends JButton{
    final public static Font FONT = createFont();

    Dimension SIZE = new Dimension(90,35);
    ButtonModel model;
    GraphButton(String label){
        super(label);
        model=getModel();
        setSize(SIZE);
        setMinimumSize(SIZE);
        setMaximumSize(SIZE);
    }

    public void paint(Graphics g){
        g.setColor(Color.BLACK);
        g.drawRoundRect(5,5,SIZE.width-8,SIZE.height-8, 6, 6);
        g.setFont(FONT);
        g.drawString(getText(),5,25);
    }
    static public Font createFont(){

        Font font = new Font("SansSerif", Font.PLAIN, 20);

        return font;
    }
}

enum GraphActions{
    SVG,
    DATA,
    PNG;
}