/**
* Class ToDoCrocWidget
* Post twitter message
* Creation: Feb 26, 2011
* @author Axelle APVRILLE
*/

package crocwidget;

import uicrocbar.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import org.w3c.dom.*;
import java.io.IOException;

public class TwitterCrocWidget extends CrocWidget {

    protected Image backgroundImage, imageN, imageA;
    protected String executePath;
    protected String pathToNormalIcon, pathToActiveIcon;
    protected boolean entered = false; // mouse over icon or not
    protected boolean loaded = false;
    protected MediaTracker media;
    protected int decX, decY;
    protected int widthA, heightA, widthN, heightN;

    public TwitterCrocWidget(JCrocBarFrame _frame,
                             JCrocBarPanels _panel,
                             int _posx, int _posy,
                             int _width, int _height,
                             Color _bg, Color _fg,
                             NodeList _nl,
                             NodeList _listData,
                             boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);
        startLoadImage();
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

    public void mouseEntered(MouseEvent e) {
        entered = true;
        repaint();
    }

    public void mouseExited(MouseEvent e) {
        entered = false;
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            System.out.println("Action: " + executePath);
            // TODO
        }
    }

    public void loadExtraParam(NodeList nl) {
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            String s;

            // parse each node of the list
            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);

                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;

                            if (elt.getTagName().equals("ExecutePath")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    executePath = s;
                                }
                            }

                            if (elt.getTagName().equals("PathToNormalIcon")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    pathToNormalIcon = s;
                                }
                            }

                            if (elt.getTagName().equals("PathToActiveIcon")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    pathToActiveIcon = s;
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception e) {
            System.err.println("Error when loading extraparameters of Twitter: " + e.getMessage());
        }

    }

    public void startLoadImage() {
        media = new MediaTracker(this);
        imageN = getImage(pathToNormalIcon);
        media.addImage(imageN, 0);
        imageA = getImage(pathToActiveIcon);
        media.addImage(imageA, 1);
        newTimer(250);
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

    public void timerExpired() {
        if (! loaded) {
            boolean b = media.checkAll();
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
                repaint();
            } else {
                newTimer(250);
            }
        }

    }

    protected String getAboutString() {
        String s = "Twitter CrocWidget\nProgrammed by A. and L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "Twitter Widget options:\n";
        s += "* ExecutePath <String> (default = none)\n";
        s += "* PathToNormalIcon  <String> (default = none)\n";
        s += "* PathToActiveIcon <String> (default = none)\n";
        return s;
    }

    protected void makeExtraMenu() {
        JMenuItem jmi;

        jmi = new JMenuItem("Tweet");
        jmi.setBackground(bg);
        jmi.setForeground(fg);
        jmi.addActionListener(menuAL);
        menu.add(jmi);
    }

    protected void extraPopupAction(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.startsWith("Tweet")) {
            setTweet();
            return ;
        }
    }

    protected void setTweet() {
        JDialogTweet dialog = new JDialogTweet(frame, "Tweet");
        dialog.setSize(250, 150);
        showDialog(dialog, 350, 150); // blocked until dialog has been closed

        if (dialog.tweetMsg.length() > 0) {
            try {
                String mycommand = executePath + " \"" + dialog.tweetMsg + "\"";
                System.out.println("My command: "+mycommand);

                ProcessBuilder pb = new ProcessBuilder(executePath, dialog.tweetMsg);
                Process proc = pb.start();
                // TODO: close process
            } catch(IOException ie) {
                System.err.println("Error when tweeting: "+ executePath+ " '" + dialog.tweetMsg + "'");
                System.err.println("Error is: "+ie.getMessage());
            }
        }
    }

}
