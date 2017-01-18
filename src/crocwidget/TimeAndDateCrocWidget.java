
/**
* Class TimeAndDateCrocWidget
* Widget for displaying date and time
* Creation: May, 05, 2009
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

public  class TimeAndDateCrocWidget extends CrocWidget implements Runnable, UseBackgroundImage  {
    public static final String TIME_FORMAT_SECONDS = "HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "EEE, MMM d";
    public static final String DATE_FORMAT_YEAR = "EEE, MMM d, yyyy";

    protected SimpleDateFormat sdfTime, sdfDate;
    protected Calendar cal;

    protected Graphics myg;

    protected boolean showSeconds, showYear;
    protected int waitingTime;
    protected int tmpWaitingTime;
    protected String timeLabel;
    protected int timeOffset;

    protected boolean alarmSet;
    protected ArrayList<String> alarmValues;
    protected ArrayList<String> alarmLabels;
    protected int alarmIndex;
    protected long nextTime;
    protected boolean showLabel;
    protected String label;

    protected Thread t;
    protected String time, date;

    protected int fontSize;
    protected Color textColor;
    protected Image backgroundImage;

    // Constructor
    public TimeAndDateCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        if (showSeconds) {
            sdfTime = new SimpleDateFormat(TIME_FORMAT_SECONDS);
            waitingTime = 800;
        } else {
            sdfTime = new SimpleDateFormat(TIME_FORMAT);
            waitingTime = 58000;
        }

        if (showYear) {
            sdfDate = new SimpleDateFormat(DATE_FORMAT_YEAR);
        } else {
            sdfDate = new SimpleDateFormat(DATE_FORMAT);
        }

        t = new Thread(this);
        t.start();
    }

    protected int isAlarmTime(String t) {
        int cpt = 0;
        for(String s : alarmValues) {
            if (t.startsWith(s)) {
                return cpt;
            }
            cpt ++;
        }
        return -1;
    }

    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.fillRect(0, 0, width, height);
        Color c = g.getColor();
        g.setColor(textColor);
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, timeOffset);
        //System.out.println("time: "+sdfTime.format(cal.getTime()));
        //System.out.println("offset: "+timeOffset);
        time = sdfTime.format(cal.getTime());

        if (alarmSet) {
            int index;
            if ((index = isAlarmTime(time)) > -1) {
                if (alarmIndex == -1) {
                    alarmIndex = index;
                    tmpWaitingTime = waitingTime;
                    waitingTime = 150;
                    frame.ring();
                    t.interrupt();
                    nextTime = System.currentTimeMillis();
                    showLabel = true;
                    label = alarmLabels.get(alarmIndex);
                }
            } else {
                if (alarmIndex > -1) {
                    alarmIndex = -1;
                    showLabel = false;
                    waitingTime = tmpWaitingTime;
                }
            }
        }

        Font f = g.getFont();
        Font tmpf;
        if (alarmIndex != -1) {
            alarmIndex = (int)(((System.currentTimeMillis() - nextTime)/150)%10);
            tmpf = f.deriveFont(Font.BOLD, (float)(f.getSize() - 5 + alarmIndex));
            g.setFont(tmpf);
            if (alarmIndex == 0) {
                showLabel = !showLabel;
            }

            /*if (System.currentTimeMillis() > nextTime) {
              nextTime = System.currentTimeMillis() + 150;
              alarmIndex = (alarmIndex + 1)%10;
              }*/
        }

        int w, x;

        if ((alarmIndex != -1) && (showLabel)) {
            w = g.getFontMetrics().stringWidth(label);
            x = (width - w)/2;
            g.drawString(label, x, (height/2) - 2);

            g.setFont(f);
            f = g.getFont();
            g.setFont(f.deriveFont((float)fontSize));
            date = sdfDate.format(cal.getTime());
            w = g.getFontMetrics().stringWidth(date);
            x = (width - w)/2;
            g.drawString(date, x, (height/2) + 10);
            g.setColor(c);
        } else if (timeLabel != null) {
            f = g.getFont();
            g.setFont(f.deriveFont((float)fontSize));
            w = g.getFontMetrics().stringWidth(timeLabel);
            x = (width - w)/2;
            g.drawString(timeLabel, x, (height/3) - 2);

            w = g.getFontMetrics().stringWidth(time);
            x = (width - w)/2;
            g.drawString(time, x, (height/3) +12);

            date = sdfDate.format(cal.getTime());
            w = g.getFontMetrics().stringWidth(date);
            x = (width - w)/2;
            g.drawString(date, x, (height/3) + 24);
            g.setColor(c);
        } else {
            f = g.getFont();
            g.setFont(f.deriveFont((float)fontSize));
            w = g.getFontMetrics().stringWidth(time);
            x = (width - w)/2;
            g.drawString(time, x, (height/2) - 2);

            g.setFont(f);
            f = g.getFont();
            g.setFont(f.deriveFont((float)fontSize));
            date = sdfDate.format(cal.getTime());
            w = g.getFontMetrics().stringWidth(date);
            x = (width - w)/2;
            g.drawString(date, x, (height/2) + 10);
            g.setColor(c);
        }
    }

    /*protected void draw(Graphics g) {
      myg = g;
      g.fillRect(posx, posy, width, height);
      Color c = g.getColor();
      g.setColor(Color.red);
      cal = Calendar.getInstance();
      String info = sdfTime.format(cal.getTime());
      //System.out.println("Time:" + info);
      int w = g.getFontMetrics().stringWidth(info);
      int x = (width - w)/2;
      g.drawString(info, posx+x, posy+10);
      info = sdfDate.format(cal.getTime());
      w = g.getFontMetrics().stringWidth(info);
      x = (width - w)/2;
      g.drawString(info, posx+x, posy+25);
      g.setColor(c);

    }*/

    public void run() {
        if (bg.getAlpha() < 255) {
            backgroundImage = loadBackgroundImage();
        }
        while(true) {
            try {
                Thread.currentThread().sleep(waitingTime);
            } catch (InterruptedException ie) {
                //System.out.println("Interrupted");
            }

            repaint();
        }
    }

    public void loadExtraParam(NodeList nl) {
        showSeconds = true;
        showYear = false;
        alarmSet = false;
        alarmValues = new ArrayList<String>();
        alarmLabels = new ArrayList<String>();
        alarmIndex = -1;
        textColor = Color.red;
        fontSize =10;
        timeOffset = 0;
        timeLabel = null;

        System.out.println("*** load extra time and date params *** ");
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
                            if (elt.getTagName().equals("ShowSeconds")) {
                                //System.out.println("ShowSeconds!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    s = s.trim().toUpperCase();
                                    if (s.equals("TRUE")) {
                                        showSeconds = true;
                                        //System.out.println("Setting to true");
                                    }
                                    if (s.equals("FALSE")) {
                                        showSeconds = false;
                                        //System.out.println("Setting to false showSeconds=" + showSeconds);
                                    }
                                }
                            }

                            if (elt.getTagName().equals("ShowYear")) {
                                //System.out.println("ShowYear!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    s = s.trim().toUpperCase();
                                    if (s.equals("TRUE")) {
                                        showYear = true;
                                        //System.out.println("Setting to true");
                                    }
                                    if (s.equals("FALSE")) {
                                        showYear = false;
                                        //System.out.println("Setting to false showSeconds=" + showSeconds);
                                    }
                                }
                            }

                            if (elt.getTagName().equals("Alarm")) {
                                //System.out.println("ShowYear!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if ((s != null) && (s.length() > 0)) {
                                    alarmSet = true;
                                    alarmValues.add(s);
                                    //System.out.println("Alarm set to " + s);
                                    s = elt.getAttribute("label");
                                    if ((s != null) && (s.length() > 0)) {
                                        alarmLabels.add(s);
                                        //System.out.println("Label: " + s);
                                    } else {
                                        alarmLabels.add("Alarm!");
                                    }
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

                            if (elt.getTagName().equals("OffsetMinutes")) {
                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        fs = Integer.decode(s).intValue();
                                        System.out.println("Setting offset minutes to "+fs);
                                        timeOffset =  fs;
                                    }
                                } catch (Exception e) {
                                    System.err.println("Could not load the clock offset minutes: " + s);
                                }
                            } else {
                                System.out.println("OffsetMinutes not found: "+timeOffset);
                            }

                            if (elt.getTagName().equals("ClockLabel")) {
                                s = elt.getAttribute("value");
                                if ((s != null) && (s.length() > 0)) {
                                    timeLabel = s;
                                    System.out.println("ClockLabel: " + s);
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
        //System.out.println("Analyzing seconds showSeconds=" + showSeconds);
    }

    protected String getAboutString() {
        String s = "TimeAndDate Widget\nProgrammed by L. Apvrille";
        s += "\nPico has inserted a few bugs";
        return s;
    }

    protected String getHelpString() {
        String s = "TimeAndDate Widget options:\n";
        s += "* ShowSeconds <true/false> (default = true)\n";
        s += "* ShowYear <true/false> (default = false)\n";
        s += "* showDate <true/false> (default = true)\n";
        s += "* ShowTime <true/false> (default = true)\n";
        s += "* FontColor <Color in rgb format>\n";
        s += "* FontSize <int>\n";
        s += "* OffsetMinutes <int> (in minutes. This is the difference to system time: positive or negative)\n";
        s += "* ClockLabel <String value>\n";
        s += "* Alarm <String value of alarm (e.g. \"21:30\")> <String label> (default: no alarm)\n";
        return s;
    }


    // Menu
    protected void makeExtraMenu() {
        //System.out.println("Making menu");
        JMenu mymenu;
        JMenuItem jmi;

        // View alarms
        jmi = new JMenuItem("Manage alarms");
        jmi.setBackground(bg);
        jmi.setForeground(fg);
        jmi.addActionListener(menuAL);
        menu.add(jmi);

        menu.addSeparator();

    }

    protected void extraPopupAction(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.startsWith("Manage alarms")) {
            manageAlarms();
            return;
        }
    }


    public void manageAlarms() {
        TimeAndDateCrocWidgetJDialog dialog = new TimeAndDateCrocWidgetJDialog(this, alarmValues, alarmLabels, frame, "Managing alarms", "alarm", bg, fg);
        //dialog.setForeground(fg);
        //dialog.setBackground(bg);
        showDialog(dialog, 450, 300);
        frame.saveData();
    }

    public String getDataToSave() {
        String s = "";
        for (int i=0; i<alarmValues.size(); i++) {
            s +="<WidgetData>\n";
            s += "<Alarm time=\"" + alarmValues.get(i) + "\" label=\"" + alarmLabels.get(i) + "\" />\n" ;
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
            String time0;
            String label0;
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

                            if (elt.getTagName().equals("Alarm")) {
                                //System.out.println("http");
                                time0 = elt.getAttribute("time");
                                label0 = elt.getAttribute("label");
                                found = false;
                                for(int ii=0; ii<alarmValues.size(); ii++) {
                                    if (alarmValues.get(ii).equals(time0)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found) {
                                    alarmValues.add(time0);
                                    alarmLabels.add(label0);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading data of TimeAndDate: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    public void addAlarm(String _time, String _label) {
        alarmValues.add(_time);
        alarmLabels.add(_label);
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }




} // End of class JCrocBarFrame

