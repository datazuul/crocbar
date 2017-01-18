
/**
* Class ToDoCrocWidget
* To do list
* Creation: June, 03, 2009
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

public  class ToDoCrocWidget extends CrocWidget implements UseBackgroundImage  {

    protected int id;

    protected Graphics myg;

    protected ArrayList<String> todos;

    protected int fontSize;
    protected Color textColor;
    protected Image backgroundImage;
    protected JCrocBarTextFrame jcbtf;

    protected int dec = 4;
    protected int maxWidth = 640;

    // Constructor
    public ToDoCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces, int _id) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);
        id = _id;
        newTimer(250);
    }


    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        Color c = g.getColor();
        g.setColor(textColor);

        Font f = g.getFont();
        g.setFont(f.deriveFont((float)fontSize));
        int cpt = 1;
        maxWidth = 0;
        for(String s: todos) {
            g.drawString("" + cpt + ". " + s, 2, (fontSize+2)*cpt);
            cpt ++;
            if (((fontSize+2)*cpt) > height) {
                break;
            }
        }

        g.setFont(f.deriveFont((float)fontSize + 4));
        cpt = 0;
        for(String s: todos) {
            maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth("" + cpt + ". " + s));
            cpt ++;
        }
    }

    public void timerExpired() {
        if (backgroundImage == null) {
            if (bg.getAlpha() < 255) {
                backgroundImage = loadBackgroundImage();
            }
        }
    }

    public void loadExtraParam(NodeList nl) {

        todos = new ArrayList<String>();
        fontSize =10;
        textColor = Color.red;

        //System.out.println("*** load extra params *** ");
        //System.out.println(nl.toString());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            int fs;
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

                            if (elt.getTagName().equals("ToDo")) {
                                //System.out.println("ShowYear!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if ((s != null) && (s.length() > 0)) {
                                    todos.add(s);
                                }
                            }

                            if (elt.getTagName().equals("FontColor")) {
                                s = elt.getAttribute("value");
                                //System.out.println("Value of color: " + s);
                                try {
                                    if (CrocBarWidgetLoader.isAColor(s)) {
                                        //System.out.println("Setting color: " + s);
                                        textColor = CrocBarWidgetLoader.getColor(s);
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the waiting time: " + s);
                                }
                            }

                            if (elt.getTagName().equals("FontSize")) {
                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        fs = Integer.decode(s).intValue();
                                        if (fs > 0) {
                                            //System.out.println("Setting update time to " + updateTime);
                                            fontSize =  fs;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the font size: " + s);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error when loading extraparameters of TODO: " + e.getMessage());
        }
    }

    protected String getAboutString() {
        String s = "ToDO Widget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "ToDo Widget options:\n";
        s += "* FontColor <Color in rgb format>\n";
        s += "* FontSize <int>\n";
        s += "* ToDO <String value of to do>\n";
        return s;
    }


    // Menu
    protected void makeExtraMenu() {
        //System.out.println("Making menu");
        JMenu mymenu;
        JMenuItem jmi;

        // View alarms
        jmi = new JMenuItem("Manage todos");
        jmi.setBackground(bg);
        jmi.setForeground(fg);
        jmi.addActionListener(menuAL);
        menu.add(jmi);

        menu.addSeparator();

    }

    protected void extraPopupAction(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.startsWith("Manage todos")) {
            manageToDos();
            return;
        }
    }


    public void manageToDos() {
        ToDoCrocWidgetJDialog dialog = new ToDoCrocWidgetJDialog(this, todos, frame, "Managing todos", "todo", bg, fg);
        showDialog(dialog, 650, 400);
        frame.saveData();
        repaint();
    }

    public String getDataToSave() {
        String s = "";
        for (int i=0; i<todos.size(); i++) {
            s +="<WidgetData>\n";
            s += "<ToDo" + id + " value=\"" + todos.get(i) + "\" />\n" ;
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
            String todo;
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

                            if (elt.getTagName().equals("ToDo" + id)) {
                                //System.out.println("http");
                                todo = elt.getAttribute("value");
                                found = false;
                                for(int ii=0; ii<todos.size(); ii++) {
                                    if (todos.get(ii).equals(todo)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    todos.add(todo);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading data of TODO: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    public void addToDO(String _todo) {
        todos.add(_todo);
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (jcbtf != null) {
                if (jcbtf.isVisible()) {
                    jcbtf.setVisible(false);
                    return;
                } else {
                    jcbtf.make(todos, fontSize + 4, textColor, maxWidth);
                    jcbtf.setVisible(true);
                }
            } else {
                jcbtf = new JCrocBarTextFrame(posx+(width/2), posy+(height/2), frame.getPosx(), frame.getPosy(), frame.getWidth(), frame.getHeight(), bg);
                jcbtf.setLineNumber(true);
                jcbtf.make(todos, fontSize + dec, textColor, maxWidth);
                jcbtf.setVisible(true);
            }
        }

    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }





} // End of class

