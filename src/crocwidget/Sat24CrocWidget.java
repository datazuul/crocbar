
/**
* Class Sat24CrocWidget
* Widget for displaying a picture
* Creation: May, 09, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import org.w3c.dom.*;


//import java.io.*;

//import myutil.*;

public  class Sat24CrocWidget  extends CroppedPictureWithUpdateCrocWidget  {


    // Constructor
    public Sat24CrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

    }

    protected String getImagePath() {
        return "http://www.sat24.com/image.ashx?country=fr&type=slide&index=6&sat";
    }

    protected int getCroppedX() {
        return 5;
    }

    protected int getCroppedY() {
        return 5;
    }

    protected int getCroppedWidth() {
        return 630;
    }
    protected int getCroppedHeight() {
        return 470;
    }

    protected String getAboutString() {
        String s = "Sat24CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "Sat24 Widget options:\n";
        s += "* UpdateRate <int in ms> (default = 600000)\n";
        return s;
    }





} // End of class

