
/**
* Class JCrocBarTextFrame
* Frame for displaying a given set of String
* Creation: June, 03, 2009
* @author Ludovic APVRILLE
* @see
*/

package uicrocbar;

import crocwidget.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


//import java.io.*;

//import myutil.*;

public class JCrocBarTextFrame extends JFrame  {
    protected ArrayList<String> strings;
    protected int fontSize;
    protected Color fontColor;


    // Main frame position and size
    int posxf, posyf, widthf, heightf;

    //Panel position and size
    int middleposxp, middleposyp;


    protected JCrocBarTextPanel panel;

    // Frame size and position
    protected int posx = 5;
    protected int posy = 5;
    protected int width = 640;
    protected int height = 480;
    protected int sizeOfXBorder = 5;
    protected int sizeOfYBorder = 5;

    protected boolean rescale = true;

    // Attributes
    Color bg = Color.black;

    protected boolean lineNumber = false;


    // Constructor
    public JCrocBarTextFrame(int _middleposxp, int _middleposyp, int _posxf, int _posyf, int _widthf, int _heightf, Color _bg) {
        middleposxp = _middleposxp;
        middleposyp = _middleposyp;

        posxf = _posxf;
        posyf = _posyf;
        widthf = _widthf;
        heightf = _heightf;

        bg = _bg;
        setBackground(new Color(bg.getRGB()));
    }

    public void setLineNumber(boolean _lineNumber) {
        lineNumber = _lineNumber;
    }

    public void make(ArrayList<String> _strings, int _fontSize, Color _fontColor, int _maxWidth) {
        strings = _strings;
        fontSize = _fontSize;
        fontColor = _fontColor;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            setUndecorated(true);
        } catch (Exception e) {
            System.err.println("Exception: title bar could not be disabled");
        }


        int widthP = _maxWidth;
        int heightP = (fontSize + 2) * strings.size();

        // Determine size of frame
        width = widthP + 2*sizeOfXBorder;
        height = heightP + 2*sizeOfYBorder;

        // Determine the position of the frame
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        if (heightf > widthf) {
            // Vertical bar
            // Put window on the right?
            if (posxf + widthf + width < dim.getWidth()) {
                // Put window on right
                posx = posxf + widthf;
            } else {
                // Put window on left
                posx = posxf - width;
            }
            posy = posyf + middleposyp - (height/2);
            posy = Math.max(0, posy);
            int diff = (int)(dim.getHeight()) - (posy + height);
            if (diff < 0) {
                posy = posy + diff;
            }

        } else {
            // Horizontal bar
            if (posyf + heightf + height < dim.getHeight()) {
                // Put window under
                posy = posyf + heightf;
            } else {
                // Put window on top
                posy = posyf - height;
            }
            posx = posxf + middleposxp - (width/2);
            posx = Math.max(0, posx);
            int diff = (int)(dim.getWidth()) - (posx + width);
            if (diff < 0) {
                posx = posx + diff;
            }

        }
        if (panel!= null) {
            panel.setVisible(false);
            remove(panel);
        }
        panel = new JCrocBarTextPanel(this, strings, fontSize, fontColor, bg, sizeOfXBorder, sizeOfYBorder, widthP, heightP);
        panel.setLineNumber(lineNumber);
        panel.setBounds(sizeOfXBorder, sizeOfYBorder, widthP+sizeOfXBorder, heightP+sizeOfYBorder);
        add(panel);
        setBounds(posx, posy, width, height);
        //revalidate();
    }


} // End of class JCrocBarTextFrame

