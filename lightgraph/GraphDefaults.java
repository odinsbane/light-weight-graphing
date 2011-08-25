package lightgraph;

import java.awt.*;

/**
 * For adding lines to a graph and setting the default values.
 *
 * User: mbs207
 * Date: 8/25/11
 * Time: 9:16 AM
 */
public class GraphDefaults {
    static Color[] colors = new Color[]{

            Color.RED,
            Color.BLUE,
            Color.GRAY,
            Color.GREEN,
            new Color(0,0,50),
            new Color(50,0,0)

    };

    /**
     * Cycles through six possible default colors.
     *
     * @param i
     * @return
     */
    public static Color getDefaultColor(int i){
        while(i>=colors.length){
            i = i-colors.length;
        }
        return colors[i];
    }

}
