
/**
* Class ShortcutCrocWidget
* Widget for performing actions on files
* Creation: Jan, 13, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;


//import java.io.*;

//import myutil.*;

public  class ShortcutCrocWidget extends CrocWidget implements UseBackgroundImage {


    protected String pathToNormalIcon, pathToActiveIcon;
    protected Image imageN, imageA;
    protected Image backgroundImage;
    protected int widthA, heightA, widthN,heightN;

    protected boolean loaded;
    protected boolean entered;
    protected boolean reset;
    protected MediaTracker media;
    protected int decX, decY;
    protected int clicks;
    protected int cpt;

    protected int state; //0: image not loaded

    protected ArrayList<String> extensions, actions;
    protected ArrayList<String> files;

    //protected Graphics myg;

    // Constructor
    public ShortcutCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the images
        loaded = false;
        entered = false;
        clicks = 0;

        startLoadImage();
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

    public void paintComponent(Graphics g) {
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
            System.out.println("Opening action window");
            if (state == 1) {
                /*boolean b = startProcess();
                if ((clicks == 0) && (b)) {
                	clicks = 1;
                	newPeriodicTimer(50);
                }*/
            }
        }
    }


    public void loadExtraParam(NodeList nl) {
        extensions = new ArrayList<String>();
        actions = new ArrayList<String>();
        files = new ArrayList<String>();
        pathToNormalIcon = "";
        pathToActiveIcon = "";

        System.out.println("*** load actions, extensions and files *** ");
        //System.out.println(nl.toString());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String s;
            int fs;

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
                            if (elt.getTagName().equals("Extension")) {
                                //System.out.println("ShowSeconds!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    extensions.add(s.trim());
                                }
                            }

                            if (elt.getTagName().equals("Action")) {
                                //System.out.println("ShowSeconds!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    actions.add(s.trim());
                                }
                            }

                            if (elt.getTagName().equals("File")) {
                                //System.out.println("ShowSeconds!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    files.add(s.trim());
                                }
                            }
                            if (elt.getTagName().equals("PathToNormalIcon")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    pathToNormalIcon = s;
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

                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading extraparameters of SinglePictures: " + e.getMessage());
        }
        if (actions.size() != extensions.size()) {
            System.out.println("Warning: number of extensions and actions is different");
        }
        //System.out.println("Analyzing seconds showSeconds=" + showSeconds);
    }

    protected String getAboutString() {
        String s = "Shortcut Widget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "Shortcut Widget options:\n";
        s += "* PathToNormalIcon <String> (default = none)\n";
        s += "* PathToActiveIcon <String> (default = none)\n";
        s += "* Extension <String>\n";
        s += "* Action <String>\n";
        s += "* File <String>\n";
        return s;
    }


    // Menu
    protected void makeExtraMenu() {
        //System.out.println("Making menu");
        JMenu mymenu;
        JMenuItem jmi;

        // View alarms
        jmi = new JMenuItem("Manage files");
        jmi.setBackground(bg);
        jmi.setForeground(fg);
        jmi.addActionListener(menuAL);
        menu.add(jmi);

        menu.addSeparator();

    }

    protected void extraPopupAction(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.startsWith("Manage files")) {
            manageFiles();
            return;
        }
    }


    public void manageFiles() {
        //TimeAndDateCrocWidgetJDialog dialog = new TimeAndDateCrocWidgetJDialog(this, alarmValues, alarmLabels, frame, "Managing alarms", "alarm", bg, fg);
        //dialog.setForeground(fg);
        //dialog.setBackground(bg);
        //showDialog(dialog, 450, 300);
        frame.saveData();
    }

    public String getDataToSave() {
        String s = "";
        for (int i=0; i<files.size(); i++) {
            s +="<WidgetData>\n";
            s += "<File data=\"" + files.get(i) + "\" />\n" ;
            s += "</WidgetData>\n";
        }
        return s;
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
            String file;
            boolean found;

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

                            if (elt.getTagName().equals("File")) {
                                //System.out.println("http");
                                file = elt.getAttribute("time");
                                found = false;
                                for(int ii=0; ii<files.size(); ii++) {
                                    if (files.get(ii).equals(file)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    files.add(file);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading data of Shortcut Widget: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }

} // End of class ShortcutCrocWidget

