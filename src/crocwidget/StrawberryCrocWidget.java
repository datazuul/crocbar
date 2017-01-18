
/**
* Class StrawberryCrocWidget
* Widget for displaying a picture
* Creation: May, 09, 2009
* @author Axelle APVRILLE, Ludovic APVRILLE
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

public  class StrawberryCrocWidget extends CrocWidget implements UseBackgroundImage {

    protected ArrayList<String> paths;
    protected BufferedImage [] images;
    protected Image [] imagestmp;
    protected int nbOfImages;
    protected int currentIndex;

    protected boolean newStrawberry;
    protected Image backgroundImage;

    protected int state = 0;
    protected MediaTracker mt;


    // Constructor
    public StrawberryCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        nbOfImages = paths.size();
        imagestmp = new Image[nbOfImages];

        // To load the image
        newStrawberry = true;
        /*Thread t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY+2);
        t.start();*/
        startLoadingImages();
    }

    public void paintComponent(Graphics g) {
        if ((images != null) && (currentIndex < nbOfImages && images[currentIndex] != null)) {
            //if (currentIndex == 0) {
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, frame);
            }
            g.fillRect(0, 0, width, height);
            //}
            g.drawImage(images[currentIndex], 0, 0, frame);
        }
        if (currentIndex >= nbOfImages) {
            if (newStrawberry) {
                generateNewStrawberry();
            }
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, frame);
            }
            g.fillRect(0, 0, width, height);
            Color c = g.getColor();
            g.setColor(Color.red);
            String st1 = "No more";
            int w = g.getFontMetrics().stringWidth(st1);
            int x = (width - w)/2;
            g.drawString(st1, x, (height/2) - 2);
            String st2 = "strawberries";
            w = g.getFontMetrics().stringWidth(st2);
            x = (width - w)/2;
            g.drawString(st2, x, (height/2) + 10);
            g.setColor(c);
        }
    }

    public void startLoadingImages() {


        System.out.println("Strawberry is running");
        mt = new MediaTracker(this);
        for (int i=0; i<nbOfImages; i++) {
            //images[i] = getImage(paths.get(i)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imagestmp[i] = getImage(paths.get(i));
            mt.addImage(imagestmp[i], i);
        }

        newTimer(500);
    }

    public void timerExpired() {
        if (state == 0) {
            boolean b = mt.checkAll();
            if (b) {
                if (nbOfImages > 1) {
                    currentIndex = 0;
                    repaint();
                }
                images = new BufferedImage[nbOfImages];
                for (int i=0; i<nbOfImages; i++) {
                    //images[i] = getImage(paths.get(i)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    images[i] = toBufferedImage(imagestmp[i]);
                    images[i] = getScaledInstance(images[i], width, height, RenderingHints.VALUE_INTERPOLATION_BICUBIC, false);
                }
                imagestmp = null;
                mt = null;

                state = 1;
                newStrawberry = true;
                if (bg.getAlpha() < 255) {
                    backgroundImage = loadBackgroundImage(5);
                }
            } else {
                newTimer(500);
            }
        } else {
            // New strawberry!
            currentIndex = 0;
            newStrawberry = true;
            repaint();
        }
    }


    /*public void run() {
    	if (bg.getAlpha() < 255) {
    		//System.out.println("Strawberry loading bg image");
    		backgroundImage = loadBackgroundImage(5);
    		//System.out.println("bg image loaded");
    	}

    	System.out.println("Strawberry is running");
    	MediaTracker mt = new MediaTracker(this);
    	for (int i=0; i<nbOfImages; i++) {
    		images[i] = getImage(paths.get(i)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    		mt.addImage(images[i], i);
    	}

    	try {
    		mt.waitForAll();
    	} catch (InterruptedException ie) {
    	}

    	System.out.println("Strawberry: Ready to print "+nbOfImages + " strawberries");

    	if (nbOfImages > 1) {
    		currentIndex = 0;
    		repaint();
    	}

    	int sleepTime;

    	while(true) {
    		isNewStrawberry();

    		sleepTime = (int)(Math.random()*500000);

    		try {
    			Thread.currentThread().sleep(sleepTime);
    		} catch (InterruptedException ie) {
    		}

    		currentIndex = 0;
    		repaint();
    	}
    }*/

    /*public synchronized void isNewStrawberry() {
    	try {
    		wait();
    	} catch (InterruptedException ie) {
    	}

    	newStrawberry = true;
    }*/

    public void generateNewStrawberry() {
        newTimer((int)(Math.random()*500000));
        newStrawberry = false;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (currentIndex < nbOfImages) {
                currentIndex++;
                repaint();
            }
        }
    }


    public void loadExtraParam(NodeList nl) {
        paths = new ArrayList<String>();
        try {
            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            String s;

            for(int i=0; i<nl.getLength(); i++) {
                n1 = nl.item(i);
                if (n1.getNodeType() == Node.ELEMENT_NODE) {
                    nli = n1.getChildNodes();
                    for(int j=0; j<nli.getLength(); j++) {
                        n2 = nli.item(j);
                        if (n2.getNodeType() == Node.ELEMENT_NODE) {
                            elt = (Element) n2;

                            if (elt.getTagName().equals("PathToFile")) {
                                s = elt.getAttribute("value");
                                if (s != null) {
                                    paths.add(s);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error when loading extra parameters of Strawberry: " + e.getMessage());
        }
    }

    protected String getAboutString() {
        String s = "Strawberry CrocWidget\nProgrammed by L. and A. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "Strawberry Widget options:\n";
        s += "* PathToFile <String> (default = none)\n";
        s += "[This option may be used several times to configure how many strawberries you can eat]\n";
        return s;
    }

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }

} // End of class StrawberryCrocWidget

