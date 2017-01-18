
/**
 * Class ExecutorCrocWidget
 * Widget for periodically executing a command and displaying its result
 * Creation: Jan, 14, 2016
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

public  class ExecutorCrocWidget extends CrocWidget implements Runnable, UseBackgroundImage  {

    protected String label;
    protected boolean showLabel;

    protected int waitingTime;
    protected String command;
    protected int nbOfLines;

    protected String [] text;


    protected Color textColor;
    protected Image backgroundImage;

    // Constructor
    public ExecutorCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        Thread t = new Thread(this);
        t.start();
    }

    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);

        int dec = 0;

        if (showLabel) {
            g.setColor(textColor);
            g.drawString(label, 5, 15);
            dec += 15;
        }

        if (text != null) {
            Color c = g.getColor();
            g.setColor(textColor);
            for(int i=0; i<text.length; i++) {
                if (text[i] != null) {
                    g.drawString(text[i], 2, i*10 + 15 + dec);
                }
            }
            g.setColor(c);
        }
    }


    public void run() {
        text = new String[nbOfLines];
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

                proc = Runtime.getRuntime().exec(command);
                dis = new DataInputStream(proc.getInputStream());
                //System.out.println("Proc in made");
                /*while ((str = dis.readLine()) != null) {
                    out0 += str + "\n";
                    //System.out.println("read1:" + str);
                }*/

                //proc = Runtime.getRuntime().exec(COMMAND1);
                //dis = new DataInputStream(proc.getInputStream());
                //dos = new DataOutputStream(proc.getOutputStream());
                //System.out.println("Proc in made");

                //dos.writeUTF(out0);
                //dos.close();

                while ((str = dis.readLine()) != null) {
                    //out0 += str + "\n";
                    //System.out.println("read2:" + str);
                    if ((cpt < (nbOfLines+1)) && (cpt > 0)) {
                        //System.out.println("Executor read:" + str);
                        text[cpt-1] = str;
                    }
                    cpt ++;
                }

                /*for(i=0; i<(nbOfProcesses-cpt); i++) {
                  text[i] = null;
                  }*/


                repaint();

                try {
                    Thread.currentThread().sleep(waitingTime * 1000);
                } catch (InterruptedException ie) {
                    System.out.println("Executor InterruptedException: "+ ie.getMessage());
                }


            }
        } catch (IOException e) {
            System.out.println("IOException: "+ e.getMessage());
        }
    }

    public void loadExtraParam(NodeList nl) {
        waitingTime = 1000;
        nbOfLines = 5;
        textColor = Color.red;
        command = "";

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
                            if (elt.getTagName().equals("UpdateTime")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        wt = Integer.decode(s).intValue();
                                        if (wt > 0) {
                                            System.out.println("Executor: Setting waiting time to " + wt);
                                            waitingTime = wt;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the waiting time: " + s);
                                }
                            }

                            if (elt.getTagName().equals("NbOfLines")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        nol = Integer.decode(s).intValue();
                                        if (nol > 0) {
                                            System.out.println("Setting nb of lines to " + nol);
                                            nbOfLines = nol;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the waiting time: " + s);
                                }
                            }

                            if (elt.getTagName().equals("Command")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {

                                        if (s.length() > 0) {
                                            System.out.println("Setting command  to " + s);
                                            command = s;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the command: " + s);
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
        String s = "Executor Widget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "Executor Widget options:\n";
        s += "* UpdateTime <int in ms> (default = 1000)\n";
        s += "* NbOfLines <int> (default = 5)\n";
        s += "* Command <string>\n";
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
