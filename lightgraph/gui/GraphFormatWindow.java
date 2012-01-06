package lightgraph.gui;

import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.GraphPoints;
import lightgraph.painters.GraphPainter;
import lightgraph.painters.PanelPainter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * New imagej plugin that ...
 * User: mbs207
 * Date: 12/25/11
 * Time: 2:55 PM
 */
public class GraphFormatWindow{
    Graph graph;
    JCheckBox autox, autoy;

    JTextField xrange_low, xrange_high, yrange_low, yrange_high;
    JTextField width, height;
    final JFrame frame = new JFrame("graph format window");
    ArrayList<DataSetRow> datasets = new ArrayList<DataSetRow>();
    JPanel dataset_pane;
    public GraphFormatWindow(Graph graph){
        this.graph=graph;

        



    }
    public void initialize(){
        frame.setSize(600,600);

        ArrayList<Component> rowlist = new ArrayList<Component>();
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content,BoxLayout.PAGE_AXIS));

        rowlist.add(new JLabel("Width:"));
        width = new JTextField();
        sizeComponent(width,80,20);
        rowlist.add(width);
        rowlist.add(new JLabel("    Height: "));
        height = new JTextField();
        sizeComponent(height,80,20);
        rowlist.add(height);

        content.add(createRow(rowlist));

        
        createXRangeComponents(content);
        createYRangeComponents(content);


        dataset_pane = new JPanel();
        dataset_pane.setLayout(new BoxLayout(dataset_pane,BoxLayout.PAGE_AXIS));

        JScrollPane jsp = new JScrollPane(dataset_pane,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        content.add(jsp);

        content.add(Box.createVerticalGlue());

        JButton cancel = new JButton("cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });

        JButton accept = new JButton("accept");
        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeSettings();
            }
        });

        rowlist.add(Box.createHorizontalGlue());
        rowlist.add(cancel);
        rowlist.add(accept);
        content.add(createRow(rowlist));



        frame.add(content);
    }

    void createXRangeComponents(JPanel content){
        ArrayList<Component> rowlist = new ArrayList<Component>();
        JLabel autox_label = new JLabel("Autoscale x axis:");

        xrange_low = new JTextField();
        sizeComponent(xrange_low, 80, 20);

        xrange_high = new JTextField();
        sizeComponent(xrange_high, 80, 20);

        autox = new JCheckBox();
        autox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                boolean v = autox.isSelected();
                xrange_low.setEnabled(!v);
                xrange_high.setEnabled(!v);

            }
        });

        rowlist.add(autox_label);
        rowlist.add(autox);
        rowlist.add(autox_label);
        rowlist.add(new JLabel("   low: "));
        rowlist.add(xrange_low);
        rowlist.add(new JLabel("   high: "));
        rowlist.add(xrange_high);
        content.add(createRow(rowlist));

    }

    void createYRangeComponents(JPanel content){
        ArrayList<Component> rowlist = new ArrayList<Component>();
        JLabel autoy_label = new JLabel("Autoscale y axis:");

        yrange_low = new JTextField();
        sizeComponent(yrange_low, 80, 20);

        yrange_high = new JTextField();
        sizeComponent(yrange_high, 80, 20);

        autoy = new JCheckBox();
        autoy.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                boolean v = autoy.isSelected();
                yrange_low.setEnabled(!v);
                yrange_high.setEnabled(!v);

            }
        });

        rowlist.add(autoy_label);
        rowlist.add(autoy);
        rowlist.add(autoy_label);
        rowlist.add(new JLabel("   low: "));
        rowlist.add(yrange_low);
        rowlist.add(new JLabel("   high: "));
        rowlist.add(yrange_high);

        content.add(createRow(rowlist));

    }

    /**
     * Parse Settings and update graph;
     *
     */
    void changeSettings(){
        if(!validateInputs()){
            return;
        }
        frame.setVisible(false);

        if(autox.isSelected()){
            graph.AUTOX=true;
        } else{
            graph.setXRange(Double.parseDouble(xrange_low.getText()),Double.parseDouble(xrange_high.getText()));
        }

        if(autoy.isSelected()){
            graph.AUTOY=true;
        } else{
            graph.setYRange(Double.parseDouble(yrange_low.getText()),Double.parseDouble(yrange_high.getText()));
        }
        graph.CHEIGHT = (int)Double.parseDouble(height.getText());
        graph.CWIDTH = (int)Double.parseDouble(width.getText());
        graph.panel.setBounds(0,0,graph.CWIDTH, graph.CHEIGHT);

        for(DataSetRow row: datasets){

            row.updateSet();

        }


        graph.refresh(true);
    }

    boolean validateInputs(){
        return true;
    }
    void sizeComponent(Component c, int width, int height){

        Dimension d = new Dimension(width,height);
        c.setMinimumSize(d);
        c.setMaximumSize(d);
        c.setPreferredSize(d);


    }


    JPanel createRow(List<Component> components){
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row,BoxLayout.LINE_AXIS));
        for(Component c: components){
            row.add(c);
        }

        components.clear();
        return row;
    }

    public void display(){
        parseGraph();
        EventQueue.invokeLater(new Runnable(){
            public void run(){
                frame.setVisible(true);
            }
        });
    }

    public void parseGraph(){
        if(graph.AUTOX){
            autox.setSelected(true);
        } else{
            autox.setSelected(false);
        }
        xrange_low.setText("" + graph.MINX);
        xrange_high.setText("" + graph.MAXX);

        if(graph.AUTOY){
            autoy.setSelected(true);
        } else{
            autoy.setSelected(false);
        }
        yrange_low.setText("" + graph.MINY);
        yrange_high.setText("" + graph.MAXY);

        height.setText(String.valueOf(graph.CHEIGHT));
        width.setText(String.valueOf(graph.CWIDTH));

        createDataSetRows();

    }

    void createDataSetRows(){
        ArrayList<DataSet> sets = new ArrayList<DataSet>();
        sets.addAll(graph.DATASETS);
        for(DataSetRow dsr: datasets){
            if(sets.contains(dsr.getSet())){
                sets.remove(dsr.getSet());
            } else{
                dataset_pane.remove(dsr.panel);
            }

        }
        for(DataSet set: sets){

            DataSetRow row = new DataSetRow(set);
            dataset_pane.add(row.panel);
            datasets.add(row);

        }

    }
    
}

class DataSetRow{
    DataSet set;
    List<GraphPoints> points;
    JPanel panel;
    PointSelector point_selector;
    ColorSelector color_selector;
    public DataSetRow(DataSet set){
        points = GraphPoints.getGraphPoints();
        points.add(null);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.LINE_AXIS));
        this.set = set;
        point_selector = new PointSelector(set.POINTS, set.COLOR);
        panel.add(point_selector);
        point_selector.addMouseListener(new MouseListener(){
            int index = 0;
            public void mouseClicked(MouseEvent e) {
                index++;
                if(index>=points.size()){
                    index = 0;
                }
                point_selector.setPoints(points.get(index));
            }

            public void mousePressed(MouseEvent e) {            }
            public void mouseReleased(MouseEvent e) {            }
            public void mouseEntered(MouseEvent e) {            }
            public void mouseExited(MouseEvent e) {            }
        });

        color_selector = new ColorSelector(set.COLOR);
        panel.add(color_selector);
        color_selector.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent e) {
                final JDialog dialog = new JDialog(null, "Choose a Color", JDialog.ModalityType.APPLICATION_MODAL);
                final JColorChooser chooser = new JColorChooser(color_selector.getColor());
                Container cont = dialog.getContentPane();
                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row,BoxLayout.LINE_AXIS));
                JButton cancel = new JButton("cancel");
                row.add(cancel);
                cancel.addActionListener(new ActionListener(){

                    public void actionPerformed(ActionEvent e) {
                        dialog.setVisible(false);
                    }
                });

                JButton accept = new JButton("accept");
                accept.addActionListener(new ActionListener(){

                    public void actionPerformed(ActionEvent e) {
                        color_selector.setColor(chooser.getColor());
                        dialog.setVisible(false);
                    }
                });

                row.add(accept);
                cont.add(row,BorderLayout.SOUTH);

                cont.add(chooser, BorderLayout.CENTER);
                dialog.pack();
                dialog.setVisible(true);


            }
            public void mousePressed(MouseEvent e) {            }
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });




    }

    void updateSet(){
        set.setPoints(point_selector.getPoints());
        set.setColor(color_selector.getColor());
    }

    DataSet getSet(){
        return set;
    }
}

class PointSelector extends JPanel{
    Dimension d = new Dimension(40,40);
    Point2D center = new Point2D.Double(20,20);
    GraphPoints pts;
    Color color;
    PointSelector(GraphPoints pts, Color c){
        super();
        setMaximumSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        color = c;
        this.pts = pts;
    }

    public void paintComponent(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,(int)d.getWidth(), (int)d.getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0,0,(int)d.getWidth()-1, (int)d.getHeight()-1);
        g.setColor(color);
        GraphPainter painter = new PanelPainter((Graphics2D)g);
        if(pts!=null) pts.drawPoint(center, painter);

    }

    public void setPoints(GraphPoints p){
        pts = p;
        repaint();
    }

    public GraphPoints getPoints(){
        return pts;
    }


}

class ColorSelector extends JPanel{
    Dimension d = new Dimension(40,40);
    Color color;
    ColorSelector(Color c){
        super();
        setMaximumSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        color = c;
    }

    public void paintComponent(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, (int) d.getWidth() - 1, (int) d.getHeight() - 1);
        g.setColor(color);
        g.fillRect(10, 10, (int) d.getWidth() - 20, (int) d.getHeight() - 20);

    }

    public void setColor(Color c){
        color = c;
        repaint();
    }

    public Color getColor(){
        return color;
    }


}

