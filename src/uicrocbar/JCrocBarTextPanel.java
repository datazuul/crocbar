
/**
* Class JCrocBarTextPanel
* Main panel for displaying text in a Panel.
* Creation: June, 3, 2009
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

public class JCrocBarTextPanel extends JPanel  {

    protected JFrame frame;
    protected Color bg = Color.red;
    protected int mywidth, myheight;
    protected ArrayList<String> strings;
    protected Color fontColor;
    protected int fontSize;
    protected int posx, posy;
    protected MediaTracker mt;

    protected boolean lineNumber = false;


    // Constructor
    public JCrocBarTextPanel(JFrame _frame, ArrayList<String> _strings, int _fontSize, Color _fontColor, Color _bg, int _posx, int _posy, int _mywidth, int _myheight) {
        super();
        bg = new Color(_bg.getRGB());
        posx = _posx;
        posy = _posy;
        mywidth = _mywidth;
        myheight = _myheight;
        frame = _frame;
        strings = _strings;
        fontSize = _fontSize;
        fontColor = _fontColor;
        setBackground(Color.red);
        setLayout(null);
    }

    public void setLineNumber(boolean _lineNumber) {
        lineNumber = _lineNumber;
    }

    public void paintComponent(Graphics g) {
        g.setFont(g.getFont().deriveFont((float)fontSize));
        g.setColor(bg);
        g.fillRect(posx, posy, mywidth, myheight);
        g.setColor(fontColor);
        int cpt = 0;
        for(String s: strings) {
            cpt ++;
            if (lineNumber) {
                g.drawString("" + cpt + "." + s, posx, posy+(fontSize+2)*cpt);
            } else {
                g.drawString(s, posx, posy+(fontSize+2)*cpt);
            }
        }
    }






} // End of class JCrocBarTextPanel

