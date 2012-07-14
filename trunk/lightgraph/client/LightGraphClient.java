package lightgraph.client;

import lightgraph.Graph;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This is a 'client' for the light graph library. It will allow loading and
 * potting data files. Eventually I would like it to do a bit more but for now
 * it just gets data loaded.
 *
 *
 * User: matt
 * Date: 6/3/12
 * Time: 9:58 AM
 */
public class LightGraphClient {
    public static final String VERSION_NO = "0.1";

    ArrayList<double[]> values = new ArrayList<double[]>();
    JPanel line_panel;
    JTabbedPane tabbed_data_panel;

    ArrayList<LineEntryPanel> line_entries = new ArrayList<LineEntryPanel>();
    ExpressionEvaluator evaluator;
    JFrame house;

    LightGraphClient(){
        evaluator = new ExpressionEvaluator();
    }


    public void buildGui(){

        house = new JFrame("LightGraph data loader");
        JMenuBar menu_bar = new JMenuBar();

        JMenu file = new JMenu("file");
        menu_bar.add(file);

        JMenuItem load_columns = new JMenuItem("load columns");
        file.add(load_columns);
        load_columns.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadColumnData();
            }
        });

        JMenuItem save_data = new JMenuItem("save data");
        file.add(save_data);
        save_data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveData();
            }
        });

        JMenu plot = new JMenu("plot");
        menu_bar.add(plot);

        JMenuItem plot_graph = new JMenuItem("plot graph");
        plot.add(plot_graph);

        plot_graph.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                plotGraph();
            }
        });

        JMenuItem add_line = new JMenuItem("add line");
        plot.add(add_line);

        add_line.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                addLine();
            }
        });

        JMenu help = new JMenu("help");
        menu_bar.add(help);

        JMenuItem about = new JMenuItem("about");
        help.add(about);

        about.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                showVersionInformation();
            }
        });


        house.setJMenuBar(menu_bar);

        Container content = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        content.add(createTabbedDataPane(), JSplitPane.TOP);



        line_panel = new JPanel();
        line_panel.setLayout(new BoxLayout(line_panel, BoxLayout.PAGE_AXIS));

        content.add(line_panel, JSplitPane.BOTTOM);

        house.setContentPane(content);
        house.setSize(600,800);
        house.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        house.setVisible(true);

    }

    /**
     * Creates a new ExpressionEvaluator, just in case.
     */
    void resetEvaluator(){
        evaluator = new ExpressionEvaluator();
    }

    private JComponent createTabbedDataPane(){
        tabbed_data_panel = new JTabbedPane();
        Dimension d = new Dimension(600,600);
        //panel.setMinimumSize(d);
        tabbed_data_panel.setPreferredSize(d);
        //panel.setMaximumSize(d);

        return tabbed_data_panel;
    }



    private void addLine(){
        final LineEntryPanel lep = new LineEntryPanel(evaluator);
        line_panel.add(lep);

        line_entries.add(lep);

        line_panel.invalidate();
        house.validate();

        lep.remove.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                line_panel.remove(lep);
                line_entries.remove(lep);
                line_panel.invalidate();
                house.validate();
                house.repaint();
            }
        });

    }

    /**
     * Use the current lines and their references to
     */
    private void plotGraph() {
        Graph graph = new Graph();

        for(LineEntryPanel lep: line_entries){
            double[][] data = createLineData(lep);
            if(data!=null)
                graph.addData(data[0], data[1]);
        }

        graph.show(false, "LightGraphClient - Graph");
    }

    /**
     * Creates the data, the set of x-y values that will be plotted. The number of
     * points will be the length of the the shortest column being used.
     *
     * Returns null on failure, which occurs if the column number exceeds the
     * number of columns.
     *
     *
     * @param lep
     * @return double[2][n] where the first index is the x/y indicator and the
     *                      second  index is data point. The length is the length of
     *                      the shortest data column in use.
     */
    private  double[][] createLineData(LineEntryPanel lep){
        double[][] evaluatedValues = new double[2][];
        evaluator.startEvaluations();
        LGExpression x_expression = lep.getXExpression();

        double[] x_values = calculateColumn(x_expression);

        if(x_values==null){
            lep.invalidateXField();
            return null;
        }
        LGExpression y_expression = lep.getYExpression();

        double[] y_values = calculateColumn(y_expression);

        if(y_values==null){
            lep.invalidateYField();
            return null;
        }

        int len = x_values.length<y_values.length?x_values.length:y_values.length;

        if(x_values.length==len){
            evaluatedValues[0] = x_values;
        } else{
            evaluatedValues[0] = new double[len];
            System.arraycopy(x_values,0,evaluatedValues[0],0,len);
        }

        if(y_values.length==len){
            evaluatedValues[1] = y_values;
        } else{
            evaluatedValues[1] = new double[len];
            System.arraycopy(y_values,0,evaluatedValues[1],0,len);
        }

        return evaluatedValues;

    }

    double[] calculateColumn( LGExpression expression ){
        evaluator.startColumn();
        int min = Integer.MAX_VALUE;
        for(int i: expression.args){
            if(i>values.size()-1){
                return null;
            }
            int l = values.get(i).length;
            min = min>l?l:min;
        }

        if(expression.args.length==0){
            min=100;
        }

        int n = expression.args.length;

        double[] ret = new double[min];
        Double[] arguments = new Double[n];

        for(int i = 0; i<min; i++){
            for(int j = 0; j<n; j++){
                arguments[j] = values.get(expression.args[j])[i];
            }
            ret[i] = evaluator.evaluate(arguments, expression);
        }
        evaluator.finishEvaluations();
        return ret;


    }

    /**
     * For saving all of the column data. Not sure how to organize this atm.
     * Might be removed, since the graphs can save the data.
     */
    private void saveData()  {
    }



    public void loadColumnData(){
        FileDialog fd = new FileDialog(house,"Load Column Organized Data File");
        fd.setMode(FileDialog.LOAD);

        fd.setVisible(true);

        String f = fd.getFile();
        if(f==null){
            return;
        }

        File data = new File(fd.getDirectory(), f);


        try{
            readColumnData(data);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * After file has been selected this method is used to
     * read the data and then create a new panel.
     *
     * @param f
     * @throws IOException
     */
    public void readColumnData(File f) throws IOException {

        Charset c = Charset.forName("UTF-8");
        Path p = f.toPath();
        ArrayList<double[]> new_values = new ArrayList<double[]>();

        //keeps track of how many items are in each column
        int[] counts = new int[0];
        int columns = -1;

        try(BufferedReader br = Files.newBufferedReader(p,c)){
            boolean[] finished = new boolean[0];
            String s;
            while((s = br.readLine())!=null){
                s = s.trim();

                if(s.charAt(0)=='#'){
                    continue;
                }



                if(s.length()==0){
                    continue;
                }

                //split using lines or tabs.
                String[] row = s.split("[ \\t]");

                //first time through - first row needs to have an entry for all columns
                if(columns<0){
                    columns = row.length;
                    counts = new int[columns];
                    finished = new boolean[columns];

                    for(int i = 0; i<columns; i++){
                        new_values.add(new double[10]);
                    }
                }

                //get numerical values from row
                for(int i = 0; i<columns; i++){
                    double d = 0;
                    try{
                        d = Double.parseDouble(row[i]);
                    } catch(NumberFormatException|ArrayIndexOutOfBoundsException exc){
                        /*If it is not a number (empty string) or there aren't enough
                        columns, this finishes that column.*/
                        finished[i] = true;
                    }

                    //finished previously.
                    if(finished[i]){
                        continue;
                    }

                    //set the values for the new column
                    double[] col = new_values.get(i);
                    int dex = counts[i];

                    //doubles the length of the backing array if nescessary.
                    if(dex>=col.length){
                        double[] new_col = new double[col.length*2];
                        System.arraycopy(col,0,new_col,0,col.length);
                        new_values.remove(i);
                        new_values.add(i, new_col);
                        col=new_col;

                    }

                    col[dex] = d;
                    counts[i] = dex + 1;

                }


            }
        }

        //finished loading columns now clean up arrays.
        for(int i = 0; i<columns; i++){
            double[] d = new_values.get(i);

            if(d.length>counts[i]){
                double[] to_prune = d;
                d = new double[counts[i]];
                System.arraycopy(to_prune,0,d,0,counts[i]);
                new_values.remove(i);
                new_values.add(i,d);
            }



        }

        createNewColumnPane(f.getName(),new_values);
        
    }

    /**
     * After a column organized file has been loaded this
     * adds the new values to the panel and adds the new values
     * to the global variable.
     *
     * @param name - name of tab for file.
     * @param new_values -
     */
    void createNewColumnPane(String name, ArrayList<double[]> new_values){
        int max = 0;

        //length of the longest column is the number of rows.
        for(double[] d: new_values){
            max = d.length>max?d.length:max;
        }
        JTable t = new JTable(max, new_values.size());

        for(int i = 0; i<new_values.size(); i++){
            double[] d = new_values.get(i);
            for(int j = 0; j<d.length; j++){

                t.setValueAt(Double.toString(d[j]), j, i);

            }

        }

        JScrollPane pane = new JScrollPane(t);
        tabbed_data_panel.add(pane,name);
        TableColumnModel column_model = t.getColumnModel();
        for(int i = 0; i<new_values.size(); i++){

            String ident = "c" + values.size();
            column_model.getColumn(i).setHeaderValue(ident);
            values.add(new_values.get(i));

        }

        house.validate();

    }



    public static void main(String[] args){
        final LightGraphClient lgc = new LightGraphClient();
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                lgc.buildGui();
            }
        });
    }

    public void showVersionInformation(){
        String li = "/lightgraph/client/LicenseInformation.html";
        InputStream about_stream = LightGraphClient.class.getResourceAsStream(li);

        boolean checking=true;
        try{

            BufferedReader br = new BufferedReader(new InputStreamReader(about_stream));
            StringBuilder s = new StringBuilder();
            String line = br.readLine();
            while(line!=null){
                if(checking&&line.contains("%%NUMBER%%")){
                        line = line.replace("%%NUMBER%%",VERSION_NO);
                        checking=false;
                }
                s.append(line);
                s.append("\n");
                line = br.readLine();
            }
                br.close();
                final JFrame shower = new JFrame("Light-Graph Client ABOUT");
                JEditorPane helper = new JEditorPane("text/html",s.toString());

            helper.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                    if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        System.out.println(hyperlinkEvent.getURL());

                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            try{
                                desktop.browse(new URI(hyperlinkEvent.getURL().toExternalForm()));
                            } catch(Exception ex){
                                //this is my own mistake
                            }
                        }
                    }

                }
            });

                shower.setSize(400,400);
                helper.setEditable(false);

                //helper.addHyperlinkListener(new HelpMessages());
                shower.add(helper);

                shower.setVisible(true);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

}

