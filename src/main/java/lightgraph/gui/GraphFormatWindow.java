package lightgraph.gui;

import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.GraphLine;
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
    JTextField font_size, x_tics, y_tics;
    JTextField key_x, key_y;
    JTextField xlabel,ylabel,title;

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

        rowlist.add(new JLabel("X-TICS: "));
        x_tics = new JTextField("" + graph.getXTicCount());
        sizeComponent(x_tics,40,20);
        rowlist.add(x_tics);

        rowlist.add(new JLabel("Y-TICS: "));
        y_tics = new JTextField("" + graph.getYTicCount());
        sizeComponent(y_tics,40,20);
        rowlist.add(y_tics);

        content.add(createRow(rowlist));


        rowlist.add(new JLabel("Font Size:"));
        font_size = new JTextField();
        font_size.setText("not impl.");
        font_size.setEnabled(false);
        rowlist.add(font_size);
        sizeComponent(font_size,80,20);

        rowlist.add(new JLabel("Key X"));
        key_x = new JTextField();
        rowlist.add(key_x);
        sizeComponent(key_x,80,20);

        rowlist.add(new JLabel("Key Y"));
        key_y = new JTextField();
        rowlist.add(key_y);
        sizeComponent(key_y,80,20);

        content.add(createRow(rowlist));
        
        createXRangeComponents(content);
        createYRangeComponents(content);

        createLabelComponents(content);

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

    void createLabelComponents(JPanel content){
        JLabel xlabel_label = new JLabel("x-axis label: ");
        JLabel ylabel_label = new JLabel("y-axis label: ");
        JLabel title_label = new JLabel("title: ");

        xlabel = new JTextField();
        ylabel = new JTextField();
        title = new JTextField();
        sizeComponent(xlabel, 300, 20);
        sizeComponent(ylabel, 300, 20);
        sizeComponent(title, 300, 20);
        JPanel row = new JPanel();
        BoxLayout lay = new BoxLayout(row, BoxLayout.LINE_AXIS);
        row.setLayout(lay);
        row.add(xlabel_label);
        row.add(xlabel);
        content.add(row);

        row = new JPanel();
        lay = new BoxLayout(row, BoxLayout.LINE_AXIS);
        row.setLayout(lay);
        row.add(ylabel_label);
        row.add(ylabel);
        content.add(row);

        row = new JPanel();
        lay = new BoxLayout(row, BoxLayout.LINE_AXIS);
        row.setLayout(lay);
        row.add(title_label);
        row.add(title);
        content.add(row);

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


        int i = Integer.parseInt(x_tics.getText());
        graph.setXTicCount(i);

        i = Integer.parseInt(y_tics.getText());
        graph.setYTicCount(i);

        double d = Double.parseDouble(key_x.getText());
        graph.setKeyX(d);

        d = Double.parseDouble(key_y.getText());
        graph.setKeyY(d);

        for(DataSetRow row: datasets){

            row.updateSet();

        }

        graph.setTitle(title.getText());
        graph.setXLabel(xlabel.getText());
        graph.setYLabel(ylabel.getText());


        graph.refresh(true);
    }

    boolean validateInputs(){
        boolean r = validateDoubleField(xrange_high);
        r &= validateDoubleField(yrange_low);
        r &= validateDoubleField(yrange_high);
        r &= validateDoubleField(xrange_low);
        r &= validateDoubleField(width, 0);
        r &= validateDoubleField(height, 0);
        r &= validateDoubleField(key_x, 0);
        r &= validateDoubleField(key_y, 0);
        r &= validateIntField(x_tics);
        r &= validateIntField(y_tics);

        for(DataSetRow s: datasets){
            r&=validateDoubleField(s.line_width, 0);
            r&=validateDoubleField(s.point_size, 0);
            r&=validateDoubleField(s.point_weight, 0);
        }


        return r;
    }

    boolean validateDoubleField(JTextField t){
        if(!t.isEnabled()){
            return true;
        }
        try{

            Double.parseDouble(t.getText());
            t.setBackground(Color.WHITE);

        } catch(NumberFormatException exc){
            exc.printStackTrace();
            t.setBackground(Color.RED);
            return false;

        }

        return true;
    }

    boolean validateDoubleField(JTextField t, double min){
        if(!t.isEnabled()){
            return true;
        }
        try{

            double d = Double.parseDouble(t.getText());
            if(d<min) throw new NumberFormatException(String.format("values less than %f are not acceptable.",min));
            t.setBackground(Color.WHITE);

        } catch(NumberFormatException exc){
            exc.printStackTrace();
            t.setBackground(Color.RED);
            return false;

        }

        return true;
    }

    boolean validateIntField(JTextField t){
        if(!t.isEnabled()){
            return true;
        }
        try{

            int d = Integer.parseInt(t.getText());
            if(d<0) throw new NumberFormatException("values less than zero are not acceptable.");
            t.setBackground(Color.WHITE);

        } catch(NumberFormatException exc){
            exc.printStackTrace();
            t.setBackground(Color.RED);
            return false;

        }

        return true;
    }

    public static void sizeComponent(Component c, int width, int height){

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

        key_x.setText(String.format("%.2f",graph.getKeyX()));
        key_y.setText(String.format("%.2f",graph.getKeyY()));

        title.setText(graph.getTitle());
        xlabel.setText(graph.getXLabel());
        ylabel.setText(graph.getYLabel());

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

            DataSetRow row = new DataSetRow(set, graph);
            dataset_pane.add(row.panel);
            datasets.add(row);

        }

    }
    
}

class DataSetRow{
    DataSet set;
    List<GraphPoints> points;
    List<GraphLine> lines;
    JPanel panel;
    PointSelector point_selector;
    LineSelector line_selector;
    ColorSelector color_selector;
    JTextField label;
    JTextField line_width;
    JTextField point_size;
    JTextField point_weight;

    public DataSetRow(DataSet set, Graph graph){
        points = GraphPoints.getGraphPoints();
        points.add(null);

        lines = GraphLine.getLines();
        lines.add(null);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.LINE_AXIS));

        String t = set.label==null?"":set.label;
        label = new JTextField(t);
        GraphFormatWindow.sizeComponent(label, 100, 25);

        panel.add(label);

        JLabel lw_label = new JLabel("Line Width: ");
        panel.add(lw_label);
        line_width = new JTextField(String.format("%2.1f",set.getLineWidth()));
        GraphFormatWindow.sizeComponent(line_width,50,25);
        panel.add(line_width);

        this.set = set;

        line_selector = new LineSelector(set.LINE, set.COLOR, graph.getBackground());
        panel.add(line_selector);
        line_selector.addMouseListener(new MouseListener(){
            int index = 0;
            public void mouseClicked(MouseEvent e) {
                index++;
                if(index>=lines.size()){
                    index = 0;
                }
                line_selector.setLine(lines.get(index));
            }

            public void mousePressed(MouseEvent e) {            }
            public void mouseReleased(MouseEvent e) {            }
            public void mouseEntered(MouseEvent e) {            }
            public void mouseExited(MouseEvent e) {            }
        });

        point_selector = new PointSelector(set.POINTS, set.COLOR, graph.getBackground());
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



        JLabel ps_label = new JLabel("Point Size: ");
        panel.add(ps_label);

        point_size = new JTextField(String.format("%2.1f",set.getPointSize()));
        GraphFormatWindow.sizeComponent(point_size, 50, 25);
        panel.add(point_size);

        JLabel pw_label = new JLabel("Point Weight: ");
        panel.add(pw_label);
        point_weight = new JTextField(String.format("%2.1f",set.getPointWeight()));
        GraphFormatWindow.sizeComponent(point_weight, 50, 25);
        panel.add(point_weight);

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
                        point_selector.setColor(chooser.getColor());
                        line_selector.setColor(chooser.getColor());
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


        double d = Double.parseDouble(point_size.getText());
        set.setPointSize(d);


        d = Double.parseDouble(point_weight.getText());
        set.setPointWeight(d);




        set.setColor(color_selector.getColor());


        String l = label.getText().trim();
        if(l.isEmpty()){
            set.label=null;
        }else{
            set.label = l;
        }


        set.setLine(line_selector.getLine());

        d = Double.parseDouble(line_width.getText());
        if(d==0||line_selector.getLine()==null){
            //turn off line
            set.setLine(null);
        } else {
            //update existing line
            set.setLineWidth(d);
        }


    }



    DataSet getSet(){
        return set;
    }
}

class PointSelector extends JPanel{
    Dimension d = new Dimension(40,40);
    Point2D center = new Point2D.Double(20,20);
    GraphPoints pts;
    Color color,background;
    PointSelector(GraphPoints pts, Color fore, Color back){
        super();
        setMaximumSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        color = fore;
        background = back;
        this.pts = pts;
    }

    public void paintComponent(Graphics g){
        g.setColor(background);
        g.fillRect(0,0,(int)d.getWidth(), (int)d.getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0,0,(int)d.getWidth()-1, (int)d.getHeight()-1);
        g.setColor(color);
        GraphPainter painter = new PanelPainter((Graphics2D)g, background);
        if(pts!=null) pts.drawPoint(center, painter);

    }

    public void setPoints(GraphPoints p){
        pts = p;
        repaint();
    }

    public GraphPoints getPoints(){
        return pts;
    }

    public void setColor(Color c){
        color=c;
        repaint();
    }


}

class LineSelector extends JPanel{
    Dimension d = new Dimension(80,40);
    ArrayList<Point2D> sample;
    Point2D start = new Point2D.Double(20,20);
    Point2D end = new Point2D.Double(60,20);
    GraphLine line;
    Color color,background;
    LineSelector(GraphLine l, Color fore, Color back){
        super();
        setMaximumSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        color = fore;
        background = back;
        this.line = l;
        sample = new ArrayList<Point2D>();
        sample.add(start);
        sample.add(end);
    }

    public void paintComponent(Graphics g){
        g.setColor(background);
        g.fillRect(0,0,(int)d.getWidth(), (int)d.getHeight());
        g.setColor(Color.BLACK);
        g.drawRect(0,0,(int)d.getWidth()-1, (int)d.getHeight()-1);
        g.setColor(color);
        GraphPainter painter = new PanelPainter((Graphics2D)g, background);
        if(line!=null) {
            line.drawLine(sample,painter);
        }

    }

    public void setLine(GraphLine l){
        line = l;
        repaint();
    }

    public GraphLine getLine(){
        return line;
    }

    public void setColor(Color c){
        color=c;
        repaint();
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

