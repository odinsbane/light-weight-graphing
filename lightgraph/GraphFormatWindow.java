package lightgraph;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public GraphFormatWindow(Graph graph){
        this.graph=graph;

        



    }
    void initialize(){
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

    }
    
}
