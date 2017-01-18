
/**
* Class ShowRSSUpdateCrocWidget
* Widget for action on picture
* Creation: May, 20, 2009
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

public  class ShowRSSUpdateCrocWidget extends CrocWidget implements UseBackgroundImage  {
    static final private String BROWSER = "firefox"; // TODO: configurable ?

    protected String pathToNormalIcon, pathToUpdateIcon;
    protected Image imageN0, imageU0; // Image normal and image update ...
    protected BufferedImage imageN, imageU;
    protected Image backgroundImage;
    protected String RSSPath;
    protected String WebPath;
    protected int widthN, heightN, widthU, heightU;
    protected int border = 2;

    protected int updateTime;
    protected boolean update;
    protected MediaTracker media;
    protected int decX, decY;
    protected int hashCode;
    //protected int clicks;

    protected int state; //0: image not loaded


    // Constructor
    public ShowRSSUpdateCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the images
        update = false;

        //clicks = 0;
        startLoadImage();
        /*Thread t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY+3);
        t.start();*/
    }

    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        //g.setColor(bg);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);

        if (!update) {
            if (state == 2) {
                g.drawImage(imageN, (width-widthN)/2, (height-heightN)/2, frame);
            }
        } else {
            if (state == 2) {
                g.drawImage(imageU, (width-widthU)/2 + decX, (height-heightU)/2 + decY, frame);
            }
        }

    }

    public void startLoadImage() {
        state = 0;

        media = new MediaTracker(this);
        //System.out.println("pathToNormalIcon=" + pathToNormalIcon);
        imageN0 = getImage(pathToNormalIcon);
        //imageN = imageN0.getScaledInstance(width-2*border, height-2*border, Image.SCALE_SMOOTH);
        media.addImage(imageN0, 0);
        //System.out.println("pathToActiveIcon=" + pathToActiveIcon);
        imageU0 = getImage(pathToUpdateIcon);
        //imageU = imageU.getScaledInstance(width-2*border, height-2*border, Image.SCALE_SMOOTH);
        media.addImage(imageU0, 1);

        newTimer(500);
    }

    public void timerExpired() {
        ttcw = null;
        boolean b;


        //System.out.println("Timer expired state=" + state);
        //timer.cancel(this);

        if (state == 0) {
            b = media.checkAll();
            if (b) {
                if (imageN0== null) {
                    System.out.println("Could not load image " + pathToNormalIcon + ": aborting RSSUpdate");
                    return;
                }

                if (imageU0 == null) {
                    System.out.println("Could not load image " + pathToUpdateIcon + ": aborting RSSUpdate");
                    return;
                }

                //System.out.println("Making scaled instance width=" + (width-2*border) + " height=" + (height-2*border));

                imageN = toBufferedImage(imageN0);
                imageU = toBufferedImage(imageU0);
                imageN = getScaledInstance(imageN, width-2*border, height-2*border, RenderingHints.VALUE_INTERPOLATION_BICUBIC, false);
                imageU = getScaledInstance(imageU, width-2*border, height-2*border, RenderingHints.VALUE_INTERPOLATION_BICUBIC, false);

                widthU = imageU.getWidth(null);
                heightU = imageU.getHeight(null);
                widthN = imageN.getWidth(null);
                heightN = imageN.getHeight(null);

                if (bg.getAlpha() < 255) {
                    backgroundImage = loadBackgroundImage();
                }


                state = 2;
                repaint();
                updateRSS();

                /*media = new MediaTracker(this);
                System.out.println("pathToNormalIcon=" + pathToNormalIcon + " target width = "  + (width-2*border));
                //imageN = getImage(pathToNormalIcon);
                imageN = imageN0.getScaledInstance(width-2*border, height-2*border, Image.SCALE_FAST);
                media.addImage(imageN, 0, width-2*border, height-2*border);
                //System.out.println("pathToActiveIcon=" + pathToActiveIcon);
                //imageU = getImage(pathToUpdateIcon);
                imageU = imageU0.getScaledInstance(width-2*border, height-2*border, Image.SCALE_FAST);
                media.addImage(imageU, 1, width-2*border, height-2*border);

                try {
                media.waitForAll();
                } catch (Exception e) {
                System.out.println("Exception :" + e.getMessage());
                }*/

                //state = 2;
                //newTimer(1000);

            } else {
                newTimer(500);
            }

        } else {
            updateRSS();
        }

    }

    public void updateRSS() {
        String s;
        int hash;

        try {
            s = getHTTPDocument(RSSPath);
            hash = s.hashCode();
            if (hash != hashCode) {
                hashCode = hash;
                update = true;
                repaint();
            }

        } catch (Exception e) {
            System.out.println("Exception in ShowRSSUpdate e: " + e.getMessage());
        }

        newTimer(updateTime);
    }

    protected void goToWebsite() {
        Process proc;
        String executePath = BROWSER + " " + WebPath;
        System.out.println("ExecutePath: "+executePath);
        try {
            proc = Runtime.getRuntime().exec(executePath);
        } catch (IOException ie) {
            System.err.println("Error when starting new process " + executePath + ": " + ie.getMessage());
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (update) {
                frame.saveData();
            }
            update = false;
            //System.out.println("Action: " + executePath);
            //makeNewProcess();
            repaint();
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            goToWebsite();
        }

    }

    public void loadExtraParam(NodeList nl) {
        pathToNormalIcon = "";
        pathToUpdateIcon = "";
        RSSPath = "";
        WebPath = "";
        updateTime = 300; /* every five minutes */
        //System.out.println("*** load extra params : path *** ");
        //System.out.println(nl.toString());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String s;
            int wt;

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
                            if (elt.getTagName().equals("PathToNormalIcon")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    pathToNormalIcon = s;
                                }
                            }
                            if (elt.getTagName().equals("PathToUpdateIcon")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    pathToUpdateIcon = s;
                                }
                            }
                            if (elt.getTagName().equals("RSSPath")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    RSSPath = s;
                                }
                            }
                            if (elt.getTagName().equals("WebPath")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    WebPath = s;
                                }
                            }
                            if (elt.getTagName().equals("UpdateTime")) {
                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        wt = Integer.decode(s).intValue();
                                        if (wt > 0) {
                                            System.out.println("Setting update time to " + updateTime);
                                            updateTime = wt * 1000;
                                        }
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
            System.err.println("Error when loading extraparameters of ShowRSSUpdate: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    public void loadExtraData(NodeList nl) {
        //System.out.println("Loading data of RSS");
        //System.out.println(nl.toString());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String http, hash;
            int val;

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
                            //System.out.println("tag:" + elt.getTagName());

                            if (elt.getTagName().equals("RSSInfo")) {
                                //System.out.println("http");
                                http = elt.getAttribute("http");
                                if ((http!= null) && http.equals(RSSPath)) {
                                    hash = elt.getAttribute("hashCode");
                                    try {
                                        if (hash != null) {
                                            val = Integer.decode(hash).intValue();
                                            if (val != hashCode) {
                                                update = false;
                                                hashCode = val;
                                                System.out.println("*** Setting hashCode to " + val + " ***");
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Could not load the hash code " + hash);
                                    }
                                }

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading data of ShowRSSUpdate: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    protected String getAboutString() {
        String s = "ShowRSSUpdate CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "ShowRSSUpdate Widget options:\n";
        s += "* PathToNormalIcon <String> (default = none)\n";
        s += "* PathToUpdateIcon <String> (default = none)\n";
        s += "* RSSPath <String> (default = none)\n";
        s += "* WebPath <String> (default = none)\n";
        s += "* UpdateTime <int in s> (default = 300s)\n";
        return s;
    }

    public String getDataToSave() {
        String s = "<WidgetData>\n";
        s += "<RSSInfo http=\"" + makeXML(RSSPath) + "\" hashCode=\"" + hashCode + "\" />\n" ;
        s += "</WidgetData>\n";
        return s;
    }

    // Menu
    protected void makeExtraMenu() {
        //System.out.println("Making menu");
        JMenuItem jmi;

        // View alarms
        jmi = new JMenuItem("Go to website");
        jmi.setBackground(bg);
        jmi.setForeground(fg);
        jmi.addActionListener(menuAL);
        menu.add(jmi);

        menu.addSeparator();

        /*jmi = new JMenuItem("Hide / unhide");
        jmi.setBackground(bg);
        jmi.setForeground(fg);
        jmi.addActionListener(menuAL);
        menu.add(jmi);

        menu.addSeparator();*/


    }

    protected void extraPopupAction(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.startsWith("Go to website")) {
            goToWebsite();
            return;
        } /*else if (s.equals("Hide / unhide")) {
	    setVisible(!isVisible());
	    }*/
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }


} // End of class

