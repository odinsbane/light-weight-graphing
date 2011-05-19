package lightgraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * For displaying data from the graph..
 * User: mbs207
 * Date: Oct 26, 2010
 * Time: 11:31:41 AM
 */
public class DataWindow extends JFrame implements Runnable, ActionListener {
    String DATA;
    public DataWindow(String title, String data){

        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea jta = new JTextArea(data);
        jta.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp);
        setSize(600,800);

        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("file");
        JMenuItem save = new JMenuItem("save");
        save.setActionCommand("save");
        save.addActionListener(this);
        file.add(save);
        menubar.add(file);
        setJMenuBar(menubar);

        DATA = data;
    }

    static public DataWindow createDataWindow(Graph graph){
        StringBuffer buf = new StringBuffer();
        StringBuffer x = new StringBuffer();
        StringBuffer y = new StringBuffer();

        for(int i = 0; i<graph.dataSetCount();i++){
            DataSet ds = graph.getDataSet(i);

            for(Point2D point: ds){

                x.append(point.getX());
                x.append('\t');
                y.append(point.getY());
                y.append('\t');


            }

            buf.append(x);
            buf.append('\n');
            buf.append(y);
            buf.append('\n');


            x.delete(0, x.length());
            y.delete(0,y.length());

        }

        return new DataWindow("table of values", buf.toString());



    }

    /**
     * The only action - save.
     * @param event event passed from the man.
     */
    public void actionPerformed(ActionEvent event) {
        FileDialog fd = new FileDialog(this,"Save CSV File",FileDialog.SAVE);
        fd.setFile("data.csv");
        fd.setVisible(true);
        String fname = fd.getFile();
        if(fname==null)
            return;
        String dirname = fd.getDirectory();
        File output = new File(dirname,fname);
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            bw.write(DATA);
            bw.close();
        } catch(Exception except){
            except.printStackTrace();
            //whoops
        }

    }
    /** for showing on event queue*/
    public void run() {
        setVisible(true);
    }

    public void display(){

        java.awt.EventQueue.invokeLater(this);

    }
}
