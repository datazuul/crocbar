
/**
 * Class CPULoadCrocWidget
 * Widget for displaying cpu load
 * based on "top", usually installed in /usr/bin
 * Creation: May, 19, 2009
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

import java.lang.management.*;
import com.sun.management.OperatingSystemMXBean;

//import java.io.*;

//import myutil.*;

public  class CPULoadCrocWidget extends CrocWidget implements Runnable, UseBackgroundImage  {

    protected String label;
    protected boolean showLabel;
    protected int updateTime;

    protected Thread t;
    protected Image backgroundImage;

    protected Color textColor;
    protected int[] values;
    protected int[] uptimevalues;
    protected int head, tail;
    protected int uptimeDiviser = 1;
    protected int index_2 = -1;
    protected int index_1 = -1;

    // Constructor
    public CPULoadCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);
    }


    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);

        Color c = g.getColor();

        if (showLabel) {
            g.setColor(textColor);
            g.drawString(label, 5, 15);
        }

        if (tail != -1) {
            g.setColor(fg);
            int index = 0;
            for(int cpt = tail; cpt!=head; cpt = (cpt + 1)%values.length) {
                g.drawLine(index, height, index, values[cpt]);
                index ++;
            }
        }

        g.setColor(c);

        if (uptimeDiviser != 1) {
            int y;
            for(int i=1; i<uptimeDiviser; i++) {
                y = getYUptimeValue(i*100);
                g.drawLine(0, y, width, y);
            }

        }

    }


    public void run() {
        if (bg.getAlpha() < 255) {
            backgroundImage = loadBackgroundImage();
        }

        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        values = new int[width];
        uptimevalues = new int[width];
        tail = -1;
        head = 0;

        double value;

        int nbOfErrors = 0;


        while(nbOfErrors < 10) {
            try {
                Thread.currentThread().sleep(updateTime*1000);
            } catch (Exception e) {
            }
            value = bean.getSystemCpuLoad();
            //System.out.println("load value:" + value);
            if (value >= 0) {
                addUptimeValue(value*100);
                repaint();
            }
        }

        System.out.println("Aborting CPULoad");
    }



    // Loading params from the xml description
    public void loadExtraParam(NodeList nl) {

        textColor = Color.red;
        showLabel = false;
        label = "";
        updateTime = 5;
        tail = -1;


        //System.out.println("*** load extra params *** ");
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


                            if (elt.getTagName().equals("UpdateTime")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        wt = Integer.decode(s).intValue();
                                        if (wt > 0) {
                                            //System.out.println("Setting update time to " + updateTime);
                                            updateTime = wt;
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
            System.err.println("Error when loading extraparameters of CPULoadCrocWidget: " + e.getMessage());
        }

        // Starting thread;
        t = new Thread(this);
        t.start();
        //System.out.println("Analyzing seconds showSeconds=" + showSeconds);
    }


    // Value is provided in percentage of idle CPU
    public void addTopValue(double value) {
        int total = height - 1;
        int add = 0;
        if (showLabel) {
            total = total - 20;
            add = 20;
        }

        int y = (int)(((total * value)/100)) + add;

        //System.out.println("y=" + y);

        values[head] = y;
        if (tail == -1) {
            tail = head;
        }

        head = (head + 1) % values.length;
        if (head == tail) {
            tail = (tail + 1) % values.length;
        }

        //System.out.println("head=" + head + " tail=" + tail);
    }


    public void addUptimeValue(double value) {
        //System.out.println("Adding value " + value);
        /*int total = height - 1;
          int add = 0;
          if (showLabel) {
          total = total - 20;
          add = 20;
          }


          double tmpvalue = value / uptimeDiviser;
          int y = (int)(total - ((total * tmpvalue) / 100)) + add;*/

        if ((index_2 != -1) && (index_1 != -1)) {
            double tmpval = (value + uptimevalues[index_2])/2;
            uptimevalues[index_1] = (int)tmpval;
            int tmpy = getYUptimeValue(tmpval);
            values[index_1] = tmpy;
        }

        index_2 = index_1;
        index_1 = head;

        int y = getYUptimeValue(value);
        //System.out.println("y=" + y);

        uptimevalues[head] = (int)(value);
        values[head] = y;
        if (tail == -1) {
            tail = head;
        }

        head = (head + 1) % values.length;
        if (head == tail) {
            tail = (tail + 1) % values.length;
        }

        int max = getMaxValue();
        int val = Math.max(1, (int)(Math.ceil((max / 100.0))));
        //System.out.println("Uptime diviser =" + uptimeDiviser + " val=" + val);
        if (uptimeDiviser != val) {
            uptimeDiviser = val;
            updateUptimeValues();
        }
    }

    public int getYUptimeValue(double value) {
        int total = height - 1;
        int add = 0;
        if (showLabel) {
            total = total - 20;
            add = 20;
        }


        double tmpvalue = value / uptimeDiviser;
        int y = (int)(total - ((total * tmpvalue) / 100)) + add;
        return y;
    }

    public void updateUptimeValues() {
        int total = height - 1;
        int add = 0;
        if (showLabel) {
            total = total - 20;
            add = 20;
        }
        double tmpvalue;
        int y;

        for(int cpt = tail; cpt!=head; cpt = (cpt + 1)%uptimevalues.length) {
            tmpvalue = uptimevalues[cpt] / uptimeDiviser;
            y = (int)(total - ((total * tmpvalue) / 100)) + add;
            values[cpt] = y;
        }
    }

    public int getMaxValue() {
        int max = 0;
        for(int cpt = tail; cpt!=head; cpt = (cpt + 1)%uptimevalues.length) {
            max = Math.max(uptimevalues[cpt], max);
        }
        return max;
    }

    protected String getAboutString() {
        String s = "CPULoadCrocWidget Widget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "CPULoadCrocWidget Widget options:\n";
        s += "* UpdateTime <int in s> (default = 5)\n";
        s += "* ShowLabel <true/false> (default = true)\n";
        s += "* Label <String> (default = false)\n";
        s += "* FontColor <Color in rgb format>\n";
        s += "* FontSize <int>\n";
        return s;
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }

} // End of class CPULoadCrocWidget
