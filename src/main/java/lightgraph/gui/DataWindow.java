package lightgraph.gui;

import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.elements.ErrorBars;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * For displaying data from the graph..
 * User: mbs207
 * Date: Oct 26, 2010
 * Time: 11:31:41 AM
 */
public class DataWindow implements Runnable, ActionListener {
    JFrame frame;
    JTable table;
    DataModel model;



    public DataWindow(String title){
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DataModel();
        table = new JTable();

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(model);

        JScrollPane jsp = new JScrollPane(table);

        frame.add(jsp);
        frame.setSize(600,800);


        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("file");

        JMenuItem save = new JMenuItem("save vertical");
        save.setActionCommand("vertical");
        save.addActionListener(this);
        file.add(save);

        JMenuItem horizontal = new JMenuItem("save horizontal");
        horizontal.setActionCommand("horizontal");
        horizontal.addActionListener(this);
        file.add(horizontal);

        menubar.add(file);

        frame.setJMenuBar(menubar);



    }

    public void addColumn(String name,java.util.List<Double> column){
        model.addColumn(name,column);
    }
    
    static public DataWindow createDataWindow(Graph graph){

        DataWindow dw = new DataWindow("data");


        for(int i = 0; i<graph.dataSetCount();i++){
            DataSet ds = graph.getDataSet(i);

            ArrayList<Double> x = new ArrayList<Double>();
            ArrayList<Double> y = new ArrayList<Double>();

            for(Point2D point: ds){

                x.add(point.getX());
                y.add(point.getY());


            }

            String label = ds.label;
            String xlabel, ylabel;
            if(label==null||label.isEmpty()){
                label = "" + i;
                xlabel = "x" + i;
                ylabel = "y" + i;
            } else{
                xlabel = label + "-x";
                ylabel = label + "-y";
            }
            dw.addColumn(xlabel, x);
            dw.addColumn(ylabel, y);
            ErrorBars errors = ds.getErrorBars();
            if(errors!=null){
                if(errors.hasXData()){
                    List<Double> data = Arrays.stream(
                            errors.getErrorData(ErrorBars.XAXIS)
                    ).mapToObj(
                            d->d
                    ).collect(
                            Collectors.toList()
                    );
                    dw.addColumn(label + "-sigmax", data);
                }

                if(errors.hasYData()){
                    List<Double> data = Arrays.stream(
                            errors.getErrorData(ErrorBars.YAXIS)
                    ).mapToObj(
                            d->d
                    ).collect(
                            Collectors.toList()
                    );
                    dw.addColumn(label + "-sigmay", data);
                }
            }
        }

        return dw;



    }

    /**
     * Splits a line, the same way as it was written. Columns can contain empty strings, no trailing tab. Which means
     * "1\t2\t\n" would split to {"1", "2", ""} and "1\t2\n" splits to {"1", "2"}
     *
     * @param line
     * @return
     */
    public  static List<String> splitOnTabs(String line){
        List<String> ret = new ArrayList<>();
        int last=0;
        int next;
        while((next=line.indexOf('\t',last))>=0){
            ret.add(line.substring(last, next));
            last = next+1;
        }
        ret.add(line.substring(last, line.length()));
        return ret;
    }

    public void actionPerformed(ActionEvent event){
        if(event.getActionCommand().equals("vertical"))
            saveVertical();
        else
            saveHorizontal();

    }
    /**
     * The only action - save.
     */
    public void saveVertical() {
        FileDialog fd = new FileDialog(frame,"Save CSV File", FileDialog.SAVE);

        fd.setFile("data.csv");
        fd.setVisible(true);
        String fname = fd.getFile();
        if(fname==null)
            return;
        String dirname = fd.getDirectory();
        File output = new File(dirname,fname);
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            bw.write("#");
            for(int i=0;i<model.getColumnCount(); i++){
                bw.write(model.getColumnName(i));
                if(i<model.getColumnCount()-1)
                    bw.write('\t');

            }
            bw.write('\n');

            for(int j=0;j<model.getRowCount();j++){
                for(int i = 0; i<model.getColumnCount();i++){

                    bw.write(model.getValueAt(j,i).toString());
                    if(i<model.getColumnCount()-1)
                        bw.write('\t');

                }
                bw.write('\n');
            }

            bw.close();
        } catch(Exception except){
            except.printStackTrace();
            //whoops
        }

    }

     /**
     * The only action - save.
     */
    public void saveHorizontal() {
        FileDialog fd = new FileDialog(frame,"Save CSV File",FileDialog.SAVE);

        fd.setFile("data.csv");
        fd.setVisible(true);
        String fname = fd.getFile();
        if(fname==null)
            return;
        String dirname = fd.getDirectory();
        File output = new File(dirname,fname);
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            for(int i = 0; i<model.getColumnCount();i++){
                bw.write("#");
                bw.write(model.getColumnName(i));
                bw.write('\n');

                for(int j=0;j<model.getRowCount();j++){

                    bw.write(model.getValueAt(j,i).toString());
                    if(j<model.getRowCount()-1)
                        bw.write('\t');

                }
                bw.write('\n');
            }

            bw.close();
        } catch(Exception except){
            except.printStackTrace();
            //whoops
        }

    }

    /** for showing on event queue*/
    public void run() {
        table.createDefaultColumnsFromModel();
        frame.setVisible(true);
    }

    public void display(){

        java.awt.EventQueue.invokeLater(this);

    }
}

class DataModel extends AbstractTableModel {
    int rows = 0;
    int columns = 0;
    ArrayList<String> column_names;
    java.util.List<java.util.List<Double>> DATA = new ArrayList<java.util.List<Double>>();

    DataModel(){
        column_names=new ArrayList<String>();
    }
    public int getRowCount() {
        return rows;
    }

    public int getColumnCount() {
        return columns;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if(DATA.size()<=columnIndex||DATA.get(columnIndex).size()<=rowIndex) return "";
        return DATA.get(columnIndex).get(rowIndex);
    }

    public void addColumn(String name, java.util.List<Double> column){
        columns++;
        if(column.size()>rows) rows = column.size();
        DATA.add(column);
        column_names.add(name);
    }

    public String getColumnName(int i){
        return column_names.get(i);
    }

}

