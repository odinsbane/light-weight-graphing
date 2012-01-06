package lightgraph.gui;

import lightgraph.Graph;
import lightgraph.gui.ButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * User: melkor
 * Date: May 30, 2010
 * Time: 12:29:43 PM
 *
 * */
public class GraphFrame extends JFrame implements MouseListener {
    JLayeredPane content;
    ButtonPanel button_panel;
    Animator anime;
    boolean show_buttons= true;
    public GraphFrame(String title){
        super(title);

        //content.addMouseListener( this);

    }

    public void setGraph(Graph gp){
        content = new JLayeredPane();
        content.add(gp.panel, new Integer(0));
        Dimension d = gp.panel.getPreferredSize();
        content.setPreferredSize(d);
        gp.panel.setBounds(0,0,d.width, d.height);

        button_panel = new ButtonPanel(this);
        button_panel.setGraph(gp);
        anime = new Animator(button_panel);
        anime.start();

        d = button_panel.getStaticSize();
        button_panel.setBounds(0,0,d.width, d.height);

        content.add(button_panel,new Integer(1));
        button_panel.addMouseListener(this);

        setContentPane(content);
        
    }
    synchronized public void showButtons(boolean t){
        anime.showPanel(t);

        
    }
    public void mouseClicked(MouseEvent e) {
        show_buttons = !show_buttons;
        showButtons(show_buttons);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}


class Animator extends Thread{
    final int STEPS = 20;
    boolean animating,show;
    int dex = STEPS;
    ButtonPanel panel;
    public Animator(ButtonPanel bp){
        panel = bp;
    }
    public void run(){
        boolean test = true;
        while(test){
            if(animating){
                takeStep();
            }else{
                test = waitMethod();
            }
        }

    }
    synchronized public boolean waitMethod(){
        try{
                    wait();
                    return true;
            } catch(Exception e){
                    return false;
        }
    }
    void takeStep(){
        Dimension d = panel.getStaticSize();

        int next = (panel.TAB-d.width)/STEPS;
        next = show?next*(STEPS - dex):next*dex;



        panel.setBounds(next,0,d.width, d.height);
        dex++;
        if(dex>STEPS){
            animating = false;
            dex--;
        }
        try{
            sleep(5);
        } catch(Exception e){
            //nada
        }
    }

    synchronized public void showPanel(boolean t){
        animating = true;
        show = t;
        dex = STEPS - dex;
        notify();

    }


}
