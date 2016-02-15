package lightgraph;

import javax.swing.UIManager;
import java.awt.Font;

/**
 * Class for handling fonts, and supplying the name/size even if the font is not
 * found in the java installation.
 *
 * Created by melkor on 2/15/16.
 */
public class LGFont {
    final static Font DEFAULT =  UIManager.getDefaults().getFont("Menu.font");
    Font awt;
    String name;
    String style;
    int size;
    final static String[] styles = {
        "Plain",
        "Bold",
        "Italic",
        "Bold Italic"
    };

    public LGFont(String name, int style,  int size){
        awt = new Font(name, style, size);
        this.size = size;
        this.name = name;
        this.style = styles[style];
    }

    public LGFont(String name, String style, int size){
        this.size = size;
        this.name = name;
        int s = getStyle(style);
        if(s>=0){
            this.style = styles[s];
            awt = new Font(name, s, size);
        } else{
            this.style = style;
            awt = new Font(name, 0, size);
        }

    }

    static int getStyle(String style){
        String lc = style.toLowerCase();
        for(int i = 0; i<styles.length; i++){
            if(lc.equals(styles[i].toLowerCase())){
                return i;
            }
        }
        return -1;
    }
    public String getStyle(){
        return style;
    }

    public LGFont(Font f){
        if(f==null) throw new IllegalArgumentException(
                "Null is not a valid Font to derive LGFont!"
        );

        this.size = f.getSize();
        this.name = f.getName();
        this.style = styles[f.getStyle()];
    }

    public Font getAwtFont(){
        return awt;
    }

    public String getName(){
        return name;
    }

    public float getSize(){
        return size;
    }
}
