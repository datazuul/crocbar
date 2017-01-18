
/**
* Class JCrocBarImagePanel
* Main panel for displaying an image in a Panel.
* Creation: May, 14, 2009
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

public class JCrocBarImagePanel extends JPanel  {

    protected JFrame frame;
    protected Color bg = Color.red;
    protected int mywidth, myheight;
    protected Image image;
    protected int posx, posy;
    protected MediaTracker mt;


    // Constructor
    public JCrocBarImagePanel(JFrame _frame, Image _image, MediaTracker _mt, Color _bg, int _posx, int _posy, int _mywidth, int _myheight) {
        super();
        bg = _bg;
        posx = _posx;
        posy = _posy;
        mywidth = _mywidth;
        myheight = _myheight;
        frame = _frame;
        image = _image;
        mt = _mt;
        setBackground(Color.red);
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        if (mt == null) {
            g.drawImage(image, posx, posy, frame);
        } else {
            if (mt.checkID(0)) {
                g.drawImage(image, posx, posy, frame);
            } else {
                g.setColor(bg);
                g.fillRect(posx, posy, mywidth, myheight);
                g.setColor(Color.red);
                g.drawString("Loading...", 10, myheight/2);
            }
        }
    }






} // End of class JCrocBarImagePanel

