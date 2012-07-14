package lightgraph.client;

import org.mozilla.javascript.Function;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * When addline is used one of these entries is created for plotting the line.
 * It has two text fields, that you need to click on to enable. After they have
 * been enabled an evaluatable function needs to be entered. If it is accepted
 * then the function will be available for creating a line on the plot.
 *
 *
 * User: melkor
 * Date: 6/5/12
 * Time: 7:04 PM
 */
public class LineEntryPanel extends JPanel{
    JEntryField xfield, yfield;
    ExpressionEvaluator evaluator;
    JButton remove;

    LineEntryPanel(ExpressionEvaluator eval){
        super();
        xfield = new JEntryField(eval);
        yfield = new JEntryField(eval);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        remove = new JButton("X");
        add(remove);

        add(new JLabel("x-column: "), 0.5);
        add(xfield);

        add(new JLabel("y-column: "), 0.5);
        add(yfield);

        setEvaluator(eval);

    }

    void setEvaluator(ExpressionEvaluator eval){
        evaluator = eval;
        xfield.setEvaluator(eval);
        yfield.setEvaluator(eval);
    }

    public boolean isReady(){
        if(xfield.isEnabled()||yfield.isEnabled()){
            return false;
        } else{
            return true;
        }
    }

    LGExpression getXExpression(){

        return xfield.createEvaluator();


    }

    LGExpression getYExpression(){

        return yfield.createEvaluator();


    }

    public void invalidateXField() {
        xfield.setBackground(xfield.invalid);
    }

    public void invalidateYField() {
        xfield.setBackground(yfield.invalid);
    }
}

class JEntryField extends JTextField{
    ExpressionEvaluator evaluator;
    LGExpression expression;
    Color valid;
    Color invalid;
    public JEntryField(ExpressionEvaluator eval){

        super();

        valid = getBackground();
        invalid = Color.RED;

        setEvaluator(eval);
        Dimension fixed = new Dimension(250, 30);

        setMinimumSize(fixed);
        setMaximumSize(fixed);
        setPreferredSize(fixed);

    }

    void setEvaluator(ExpressionEvaluator eval){
        evaluator = eval;
        setupAccessControl();
    }

    void setupAccessControl(){
        setEnabled(false);
        addMouseListener(new MouseListener()
        {

            @Override
            public void mouseClicked(MouseEvent e) {
                setEnabled(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });

        addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent evt){
                        if(testExpression()){
                            setBackground(valid);
                            setEnabled(false);
                        } else{
                            setBackground(invalid);
                        }
                    }
                }
        );

    }

    boolean testExpression(){
        String text = getText().trim();

        return evaluator.testExpression(text);

    }

    LGExpression createEvaluator(){
        String text = getText().trim();
        LGExpression lge = new LGExpression();
        String[] columns = evaluator.getColumns(text);
        lge.function = evaluator.createJSFunction(text,"xValues",columns);
        lge.args = new int[columns.length];

        for(int i = 0; i<columns.length; i++){
            System.out.println(columns[i]);
            String c = columns[i].substring(1,columns[i].length());
            lge.args[i] = Integer.parseInt(c);
        }

        return lge;
    }

    String[] getDataColumns(){

        String text = getText().trim();
        String[] columns = evaluator.getColumns(text);

        return columns;
    }



}