package lightgraph.graphfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lightgraph.DataSet;
import lightgraph.Graph;
import lightgraph.painters.SvgPainter;


/**
 * This is just a test to show a graph using javafx. It might actually be a better way to display graphs, but
 * then again if I am going to use javafx I should probably use Their charts.
 *
 * Created by msmith on 2/28/14.
 */
public class GraphFx extends Application {
    private Graph graph;
    WebView view;
    public void startGraph(){
        graph = new Graph();
    }
    @Override
    public void start(Stage stage) throws Exception {
        startGraph();
        BorderPane root = new BorderPane();
        view = new WebView();
        root.setCenter(view);
        stage.setScene(new Scene(root, 600, 600));
        stage.show();


        int n = 100;
        double[] x = new double[n];
        double[] y = new double[n];

        for(int i = 0; i<n; i++){
            x[i] = 5 - 0.1*i;
            y[i] = x[i]*x[i] - 2;
        }
        addData(x,y);

        refreshGraph();

    }

    public void refreshGraph(){
        SvgPainter painter = new SvgPainter(graph.CHEIGHT, graph.CWIDTH, graph.getBackground());
        graph.resetGraph(painter);
        String op = painter.getOutput();

        WebEngine webEngine = view.getEngine();
        webEngine.loadContent(String.format("<html>%s</html>",op));
        webEngine.loadContent(op);
        webEngine.reload();

    }

    public DataSet addData(double[] x, double[] y){
        return graph.addData(x, y);
    }

    public static void main(String[] args){

        launch(args);


    }

}
