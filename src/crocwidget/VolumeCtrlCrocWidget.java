
/**
 * Class VolumeCtrlCrocWidget
 * Widget for setting the sound volume level
 * Creation: Jan, 19, 2016
 * @author Ludovic APVRILLE
 * @see
 */

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;


//import java.io.*;

//import myutil.*;

public  class VolumeCtrlCrocWidget extends CrocWidget implements Runnable, UseBackgroundImage { /*, MouseMotionListener*/

    public final static int ACTION_UP = 0;
    public final static int ACTION_DOWN = 1;

    private int action;
    private int direction;


    protected String label;
    protected boolean showLabel;

    protected String commandForVolumeUp;
    protected String commandForVolumeDown;
    protected String commandForVolumeSetting;
    protected String commandForVolumeValue;

    protected int volume = 100;

    protected Color textColor;
    protected Image backgroundImage;

    // Constructor
    public VolumeCtrlCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        /*addMouseMotionListener(this);*/

        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);

        int dec = 0;

        Color c = g.getColor();
        g.setColor(textColor);

        int w;

        if (showLabel) {
            g.setColor(textColor);
            w = g.getFontMetrics().stringWidth(label);
            g.drawString(label, w/2, 15);
            dec += 15;
        }


        int h = 5;
        g.fillRect(0, h, 5, height-h);
        g.fillRect(width-5, h, 5, height-h);

        int index = (volume*width/100)-1;
        g.fillRect(index, 10, 3, height-10);


        w = g.getFontMetrics().stringWidth(volume+"%");
        g.drawString(""+volume+"%", (width - w)/2, 15 + dec);
        g.setColor(c);


    }

    public void setVolume(int val) {
        execute(commandForVolumeSetting +  " " + val, false);
    }

    public void makeVolumeUp() {
        execute(commandForVolumeUp, false);
    }

    public void makeVolumeDown() {
        execute(commandForVolumeDown, false);
    }

    public void readVolumeValue() {
        if ((commandForVolumeValue != null) && (commandForVolumeValue.length() > 0)) {
            execute(commandForVolumeValue, true);
        }
    }

    /*public void run() {
        System.out.println("VOLUME");
    if (direction == 0) {
        makeVolumeDown();
    } else {
        makeVolumeUp();
    }
    readVolumeValue();
    repaint();
    System.out.println("Volume=" + volume);
    }

    public void mouseClicked(MouseEvent e) {
        //System.out.println("Mouse clicked in RetrievePictureAndDisplay widget");
        if (e.getButton() == MouseEvent.BUTTON1) {
            System.out.println("x = " + posx + " mouseX=" + e.getX() + " width=" + width);
        Thread t = new Thread(this);
        t.start();
        if (e.getX() < (posx + width/2)) {
    	direction = 0;
        } else {
    	direction = 1;

        }
        }

    }*/


    public void run() {
        System.out.println("VOLUME set to = " + direction);
        setVolume(direction);
        readVolumeValue();
        repaint();
        System.out.println("Volume=" + volume);
    }

    public void mouseClicked(MouseEvent e) {
        //System.out.println("Mouse clicked in RetrievePictureAndDisplay widget");
        if (e.getButton() == MouseEvent.BUTTON1) {
            System.out.println("x = " + posx + " mouseX=" + e.getX() + " width=" + width);
            int vol = (e.getX() * 100)/width;
            vol = Math.max(0, vol);
            vol = Math.min(100, vol);

            direction = vol;
            Thread t = new Thread(this);
            t.start();
        }

    }

    /*public void mouseDragged(MouseEvent e) {
    int vol = (e.getX() * 100)/width;
    vol = Math.max(0, vol);
    vol = Math.min(100, vol);

    direction = vol;
    Thread t = new Thread(this);
    t.start();

    }*/

    public void mouseMoved(MouseEvent e) {
    }


    public void execute(String command, boolean isVolume) {
        String result;
        String str;

        BufferedReader proc_in;
        DataInputStream dis;
        DataOutputStream dos;
        Process proc;
        String out0;
        int i;

        /*if (bg.getAlpha() < 255) {
          backgroundImage = loadBackgroundImage();
          }*/

        if ((command == null) || (command.length() == 0)) {
            return;
        }

        try {

            out0 = "";

            proc = Runtime.getRuntime().exec(command);
            dis = new DataInputStream(proc.getInputStream());

            while ((str = dis.readLine()) != null) {
                out0 = str;
                //System.out.println("read2:" + str);
            }
            if (isVolume) {
                volume = Integer.decode(out0).intValue();
            }


        } catch (IOException e) {
            System.out.println("IOException in VolumeCtrl: "+ e.getMessage());
        }
    }

    public void loadExtraParam(NodeList nl) {
        textColor = Color.red;
        commandForVolumeUp = "";
        commandForVolumeDown = "";
        commandForVolumeValue = "";
        commandForVolumeSetting = "";

        int wt;
        int nol;

        //System.out.println("*** load extra params *** ");
        //System.out.println(nl.toString());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String s;

            //System.out.println("Loading Synchronization gates");
            //System.out.println(nl.toString());

            for(int i=0; i<nl.getLength(); i++) {
                //System.out.println("i=" + i);
                n1 = nl.item(i);
                //System.out.println(n1);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        //System.out.println(n2);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;
                            //System.out.println(elt);


                            if (elt.getTagName().equals("CommandForVolumeUp")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    if (s.length() > 0) {
                                        System.out.println("Setting commandForVolumeUp to " + s);
                                        commandForVolumeUp = s;
                                    }
                                }
                            }

                            if (elt.getTagName().equals("CommandForVolumeDown")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    if (s.length() > 0) {
                                        System.out.println("Setting commandForVolumeDown to " + s);
                                        commandForVolumeDown = s;
                                    }
                                }
                            }

                            if (elt.getTagName().equals("CommandForVolumeSetting")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    if (s.length() > 0) {
                                        System.out.println("Setting commandForVolumeSetting to " + s);
                                        commandForVolumeSetting = s;
                                    }
                                }
                            }


                            if (elt.getTagName().equals("CommandForVolumeValue")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    if (s.length() > 0) {
                                        System.out.println("Setting commandForVolumeValue to " + s);
                                        commandForVolumeValue = s;
                                    }
                                }
                            }

                            if (elt.getTagName().equals("ShowLabel")) {
                                //System.out.println("ShowSeconds!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    s = s.trim().toUpperCase();
                                    if (s.equals("TRUE")) {
                                        showLabel = true;
                                        //System.out.println("Setting to true");
                                    }
                                    if (s.equals("FALSE")) {
                                        showLabel = false;
                                        //System.out.println("Setting to false showSeconds=" + showSeconds);
                                    }
                                }
                            }

                            if (elt.getTagName().equals("Label")) {
                                //System.out.println("ShowSeconds!");
                                s = elt.getAttribute("value");
                                //System.out.println("label=" +s );
                                if (s != null) {
                                    label = s;
                                }
                            }

                            if (elt.getTagName().equals("FontColor")) {
                                s = elt.getAttribute("value");
                                System.out.println("Value of color: " + s);
                                try {
                                    if (CrocBarWidgetLoader.isAColor(s)) {
                                        System.out.println("Setting color: " + s);
                                        textColor = CrocBarWidgetLoader.getColor(s);
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the waiting time: " + s);
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading extraparameters of Executor: " + e.getMessage());
        }
        //System.out.println("Analyzing seconds showSeconds=" + showSeconds);
    }

    protected String getAboutString() {
        String s = "VolumeCtrl Widget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "VolumeCtrl Widget options:\n";
        s += "* CommandForVolumeUp <string> (default = none)\n";
        s += "* CommandForVolumeDown <string> (default = none)\n";
        s += "* CommandForVolumeSetting <string> (default = none)\n";
        s += "* CommandForVolumeValue <string> (default = none)\n";
        s += "* ShowLabel <true/false> (default = true)\n";
        s += "* Label <string>\n";
        s += "* FontColor <Color in rgb format>\n";
        s += "* FontSize <int>\n";
        return s;
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }



} // End of class JCrocBarFrame
