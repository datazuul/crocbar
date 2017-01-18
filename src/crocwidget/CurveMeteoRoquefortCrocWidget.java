
/**
* Class MeteoRoquefortCrocWidget
* Widget for displaying a picture
* Creation: May, 06, 2009
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

public  class CurveMeteoRoquefortCrocWidget  extends CroppedPictureWithUpdateCrocWidget  {



    // Constructor
    public CurveMeteoRoquefortCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);
    }


    protected int getCroppedX() {
        return 150;
    }

    protected int getCroppedY() {
        return 61;
    }

    protected int getCroppedWidth() {
        return 689;
    }

    protected int getCroppedHeight() {
        return 474;
    }

    protected String getImagePath() {
        return "http://www.meteo-roquefort-les-pins.com/meteo/aktuell.gif";
    }

    protected String getAboutString() {
        String s = "CurveMeteoRoquefort CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "CurveMeteoRoquefort Widget options:\n";
        s += "* UpdateRate <int in ms> (default = 60000)\n";
        return s;
    }





} // End of class

