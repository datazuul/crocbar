
/**
 * Class ActionIconCrocWidget
 * Widget for action on picture
 * Creation: May, 11, 2009 - Modified the 15/01/2016
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

public  class ActionIconCrocWidget extends CrocWidget implements UseBackgroundImage {

    protected String pathToNormalIcon, pathToActiveIcon;
    protected Image imageN, imageA;
    protected Image backgroundImage;
    protected String executePath;
    protected boolean animate;

    protected int widthA, heightA, widthN,heightN;

    protected boolean loaded;
    protected boolean entered;
    protected boolean reset;
    protected MediaTracker media;
    protected int decX, decY;
    protected int clicks;
    protected int cpt;

    protected int state; //0: image not loaded



    // Constructor
    public ActionIconCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the images
        loaded = false;
        entered = false;
        clicks = 0;

        startLoadImage();

        //aitt = new ActionIconTimerTask(this, mainPanel, timer, executePath, pathToNormalIcon, pathToActiveIcon);
        //aitt.startLoadImage();
        //t.setPriority(Thread.MIN_PRIORITY+3);
        //t.start();
    }

    public void setImages(Image _imageA, Image _imageN) {
        imageA = _imageA;
        imageN = _imageN;
        widthA = imageA.getWidth(null);
        heightA = imageA.getHeight(null);
        widthN = imageN.getWidth(null);
        heightN = imageN.getHeight(null);
        if (bg.getAlpha() < 255) {
            backgroundImage = loadBackgroundImage();
        }
        loaded = true;

    }

    public void setDecY(int _decY) {
        decY = _decY;
    }


    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        //g.setColor(bg);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);
        if (loaded) {
            if (entered) {
                g.drawImage(imageA, (width-widthA)/2 + decX, (height-heightA)/2 + decY, frame);
            } else {
                g.drawImage(imageN, (width-widthN)/2 + decX, (height-heightN)/2 + decY, frame);
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        //System.out.println("Mouse entered");
        entered = true;

        repaint();
    }

    public void mouseExited(MouseEvent e) {
        //System.out.println("Mouse exited");
        entered = false;
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            System.out.println("Action: " + executePath);
            if (state == 1) {
                boolean b = startProcess();
                if ((clicks == 0) && (b)) {
                    clicks = 1;
                    if (animate) {
                        newPeriodicTimer(50);
                    }
                }
            }
        }
    }

    public void loadExtraParam(NodeList nl) {
        pathToNormalIcon = "";
        pathToActiveIcon = "";
        executePath = "";
        animate = true;
        //System.out.println("*** load extra params : path *** ");
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
                            if (elt.getTagName().equals("PathToNormalIcon")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    pathToNormalIcon = s;
                                }
                            }
                            if (elt.getTagName().equals("Animate")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s.compareTo("false") != 0) {
                                    animate = false;
                                } else {
                                    animate = true;
                                }
                            }
                            if (elt.getTagName().equals("PathToActiveIcon")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    pathToActiveIcon = s;
                                }
                            }
                            if (elt.getTagName().equals("ExecutePath")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    executePath = s;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading extraparameters of ActionIcon: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    protected String getAboutString() {
        String s = "ActionIcon CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "ActionIcon Widget options:\n";
        s += "* Animate <boolean> (default = true)\n";
        s += "* PathToNormalIcon <String> (default = none)\n";
        s += "* PathToActiveIcon <String> (default = none)\n";
        s += "* ExecutePath <String> (default = none)\n";
        return s;
    }

    public void startLoadImage() {
        state = 0;
        media = new MediaTracker(this);
        //System.out.println("pathToNormalIcon=" + pathToNormalIcon);
        imageN = getImage(pathToNormalIcon);
        media.addImage(imageN, 0);
        //System.out.println("pathToActiveIcon=" + pathToActiveIcon);
        imageA = getImage(pathToActiveIcon);
        media.addImage(imageA, 1);

        //System.out.println("First schedule");
        newTimer(250);
    }



    public boolean startProcess() {
        Process proc;
        if (executePath.toUpperCase().equals("NEXTFACE")) {
            mainPanel.nextFace();
        } else if (executePath.toUpperCase().equals("PREVIOUSFACE")) {
            mainPanel.previousFace();
        } else {
            try {
                proc = Runtime.getRuntime().exec(executePath);
                cpt = 0;
                return true;
            } catch (IOException ie) {
                System.err.println("Error when starting new process " + executePath + ": " + ie.getMessage());
            }
        }
        return false;

    }

    public void timerExpired() {
        ttcw = null;
        boolean b;


        //System.out.println("Running state=" + state);
        //timer.cancel(this);

        if (state == 0) {
            b = media.checkAll();
            if (b) {
                if (imageA == null) {
                    System.out.println("Could not load image " + pathToActiveIcon + ": aborting ActionIcon");
                    return;
                }

                if (imageN == null) {
                    System.out.println("Could not load image " + pathToNormalIcon + ": aborting ActionIcon");
                    return;
                }

                setImages(imageA, imageN);
                state = 1;
                repaint();
            } else {
                newTimer(250);
            }
        }

    }

    public void newPeriod() {
        cpt ++;

        if (cpt > 50) {
            clicks = 0;
            decY = 0;
            ttcw.cancel();
        } else {
            decY = decY - 4 + (cpt%5 * 2);
        }

        repaint();
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }





} // End of class
