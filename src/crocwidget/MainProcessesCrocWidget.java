
/**
 * Class MainProcessesCrocWidget
 * Widget for displaying main processes + their CPU time
 * Creation: May, 05, 2009
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

public  class MainProcessesCrocWidget extends CrocWidget implements Runnable, UseBackgroundImage  {
    public static final String COMMAND0 = "/bin/ps -eo pcpu,args";
    public static final String COMMAND1 = "sort -g -r";

    protected int waitingTime;
    protected int nbOfProcesses;

    protected String [] text;

    protected Color textColor;
    protected Image backgroundImage;

    // Constructor
    public MainProcessesCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);
        if (text != null) {
            Color c = g.getColor();
            g.setColor(textColor);
            for(int i=0; i<text.length; i++) {
                if (text[i] != null) {
                    g.drawString(text[i], 2, i*10 + 15);
                }
            }
            g.setColor(c);
        }
    }


    public void run() {
        text = new String[nbOfProcesses];
        String str;
        int cpt;
        BufferedReader proc_in;
        DataInputStream dis;
        DataOutputStream dos;
        Process proc;
        String out0;
        int i;

        if (bg.getAlpha() < 255) {
            backgroundImage = loadBackgroundImage();
        }

        try {
            while(true) {

                //System.out.println("Executing process np:" + nbOfProcesses + " on command " + command);
                cpt = 0;
                out0 = "";

                proc = Runtime.getRuntime().exec(COMMAND0);
                dis = new DataInputStream(proc.getInputStream());
                //System.out.println("Proc in made");
                while ((str = dis.readLine()) != null) {
                    out0 += str + "\n";
                    //System.out.println("read1:" + str);
                }

                proc = Runtime.getRuntime().exec(COMMAND1);
                dis = new DataInputStream(proc.getInputStream());
                dos = new DataOutputStream(proc.getOutputStream());
                //System.out.println("Proc in made");

                dos.writeUTF(out0);
                dos.close();

                while ((str = dis.readLine()) != null) {
                    //out0 += str + "\n";
                    //System.out.println("read2:" + str);
                    if ((cpt < (nbOfProcesses+1)) && (cpt > 0)) {
                        text[cpt-1] = str;
                    }
                    cpt ++;
                }

                /*for(i=0; i<(nbOfProcesses-cpt); i++) {
                  text[i] = null;
                  }*/

                repaint();

                try {
                    Thread.currentThread().sleep(waitingTime);
                } catch (InterruptedException ie) {
                    System.out.println("InterruptedException: "+ ie.getMessage());
                }


            }
        } catch (IOException e) {
            System.out.println("IOException: "+ e.getMessage());
        }
    }

    public void loadExtraParam(NodeList nl) {
        waitingTime = 1000;
        nbOfProcesses = 3;
        textColor = Color.red;

        int wt;
        int np;

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
                            if (elt.getTagName().equals("WaitingTime")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        wt = Integer.decode(s).intValue();
                                        if (wt > 0) {
                                            System.out.println("Setting waiting time to " + waitingTime);
                                            waitingTime = wt;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the waiting time: " + s);
                                }
                            }

                            if (elt.getTagName().equals("ShowNProcesses")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        np = Integer.decode(s).intValue();
                                        if (np > 0) {
                                            System.out.println("Setting waiting time to " + waitingTime);
                                            nbOfProcesses = np;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the waiting time: " + s);
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
            System.err.println("Error when loading extraparameters of MainProcesses: " + e.getMessage());
        }
        //System.out.println("Analyzing seconds showSeconds=" + showSeconds);
    }

    protected String getAboutString() {
        String s = "MainProcesses Widget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "MainProcesses Widget options:\n";
        s += "* WaitingTime <int in ms> (default = 1000)\n";
        s += "* ShowNProcesses <int> (default = 3)\n";
        s += "* FontColor <Color in rgb format>\n";
        s += "* FontSize <int>\n";
        s += "\nThis widget works only under *UNIX* platforms\n";
        return s;
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }



} // End of class JCrocBarFrame
