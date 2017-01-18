
/**
 * Class HDSizeCrocWidget
 * Widget for displaying avialable size on HD
 * Creation: June, 10, 2009
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

public class HDSizeCrocWidget extends CrocWidget implements UseBackgroundImage  {

    protected String path, label;
    protected File file;
    protected int updateTime;
    protected long totalValue;
    protected long currentValue;
    protected Color textColor;
    protected Image backgroundImage;
    protected int hText;
    protected String command;

    protected int xx, yy, ray=-1;


    // Constructor
    public HDSizeCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the image
        //Thread t = new Thread(this);
        //t.setPriority(Thread.MIN_PRIORITY+2);
        //t.start();


        //System.out.println("xx=" + xx + " yy=" + yy + "ray=" + ray + "width =" + width + "height=" + height);

        file = new File(path);
        newTimer(250);
    }

    public void paintComponent(Graphics g) {

        if (ray == -1) {
            int factor = 1;
            if (label.length() > 0) {
                factor = 2;
            }
            xx = (width - 2) / 2;
            yy = (height-(factor*g.getFontMetrics().getHeight())) / 2;
            if (xx < yy) {
                ray = xx;
            } else {
                ray = yy;
            }

            xx = xx-ray + 1;
            yy = yy-ray + (g.getFontMetrics().getHeight() * (factor - 1) + 3);

            ray = ray * 2;
        }

        int startArc, arc;
        double d;
        int index;
        String s0, s;
        int w;


        int tmp = (int)(((float)(totalValue - currentValue)/totalValue)*360);
        //System.out.println("tmp=" + tmp);
        startArc = -90 + (360-tmp)/2;
        arc = tmp;

        //System.out.println("StartArc = " + startArc + "arc=" + arc);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        g.setColor(fg);
        g.drawOval(xx, yy, ray, ray);
        g.fillArc(xx, yy, ray, ray, startArc, arc);

        g.setColor(textColor);
        d = (double)(currentValue) / 1048576 / 1024;
        s0 = "" +d;
        //System.out.println("d=" + d);
        index = s0.indexOf('.');
        if (index > -1) {
            if (d>10) {
                s0 = s0.substring(0, index);
            } else {
                s0 = s0.substring(0, index+2);
            }
        }


        s = s0 + " Go";
        w = g.getFontMetrics().stringWidth(s);
        if (w > (width-2)) {
            s = s0;
            w = g.getFontMetrics().stringWidth(s);
            if (w > (width-2)) {
                s = "";
            }
        }


        g.drawString(s, (width-w)/2, height - 1);

        if (label.length() > 0) {
            w = g.getFontMetrics().stringWidth(label);
            g.drawString(label, (width-w)/2, g.getFontMetrics().getHeight());
        }
    }

    public void timerExpired() {
        if (backgroundImage == null) {
            if (bg.getAlpha() < 255) {
                backgroundImage = loadBackgroundImage();
            }
        }
        try {

            if ((command == null) || (command.length() == 0)) {
                //System.out.println("HDsize " + path + ": usual value");
                totalValue = file.getTotalSpace();
                currentValue = file.getFreeSpace();
            } else {
                //System.out.println("HDSize going to run command: " + command);
                String s = runOneLineCommand(command);
                //System.out.println("HD command result=" + s);
                s = s.trim();
                long[] tmp = getTwoLongFromLine(s);
                //System.out.println("HDSize command");
                if (tmp != null) {
                    System.out.println("HDSize" + path + ": total=" + tmp[0] + " current=" + tmp[1]);
                    totalValue = tmp[0] * 1024;
                    currentValue = tmp[1] * 1024;
                }
            }
            System.out.println("HDSize Total: " + totalValue + " current: " + currentValue);
            //currentValue = currentValue;
            newTimer(updateTime);
            repaint();
        } catch (Exception e) {
            System.err.println("HD size of " + path + "could not be computed");
        }
    }



    public void loadExtraParam(NodeList nl) {
        path = "";
        label = "";
        updateTime = 60;
        textColor = Color.red;
        command = "";
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
                            if (elt.getTagName().equals("Path")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    path = s;
                                }
                            }

                            if (elt.getTagName().equals("Label")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    label = s;
                                }
                            }

                            if (elt.getTagName().equals("Command")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                System.out.println("Command value=" +s );
                                if (s != null) {
                                    command = s;
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
                        }

                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading extraparameters of SinglePicture: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    protected String getAboutString() {
        String s = "HDSizeCrocWidget CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "SinglePicture Widget options:\n";
        s += "* Path <String> (default = none) .. Path to partition\n";
        s += "* Command <String> (default = none, optional)\n";
        s += "* UpdateTime <int in s> (default = 60s)\n";
        return s;
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }


} // End of class SinglePictureCrocWidget
