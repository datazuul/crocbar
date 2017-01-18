
/**
 * Class MeteoRoquefortCrocWidget
 * Widget for displaying a picture
 * Creation: May, 10, 2009
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

public abstract class CroppedPictureWithUpdateCrocWidget  extends CrocWidget { /*implements Runnable*/

    protected int updateRate;
    //protected Image croppedImage;

    //protected MediaTracker media;
    protected ImageWork imageWork;

    protected String pathToFile;
    protected Image image, smallImage;

    protected JCrocBarPictureFrame jcbpf;


    // Constructor
    public CroppedPictureWithUpdateCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the image and update it
        //Thread t = new Thread(this);
        //t.start();
        smallImage = null;
        loadImages();
    }

    public void paintComponent(Graphics g) {
        if (smallImage != null) {
            g.drawImage(smallImage, 0, 0, frame);
        }

    }

    public void loadImages() {
        //System.out.println("Loading images");
        imageWork = new ImageWork(this);
        imageWork.pathToFile = pathToFile;
        imageWork.urgent = false;
        imageWork.load = true;
        imageWork.crop = true;
        imageWork.scale = true;
        imageWork.xC = getCroppedX();
        imageWork.yC = getCroppedY();
        imageWork.widthC = getCroppedWidth();
        imageWork.heightC = getCroppedHeight();
        imageWork.widthS = width;
        imageWork.heightS = height;
        ilt.addImageWork(imageWork);
    }

    public void imageWorkDone(Image img) {
        smallImage = img;
        image = imageWork.image;
        imageWork = null;
        repaint();
        newTimer(updateRate);
    }

    public void timerExpired() {
        loadImages();
    }

    public void loadExtraParam(NodeList nl) {
        pathToFile = getImagePath();
        updateRate = 60000;
        System.out.println("*** load extra params : updateRate *** ");
        //System.out.println(nl.toString());
        try {

            NodeList nli;
            Node n1, n2;
            Element elt;
            int k;
            int ur;
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
                            if (elt.getTagName().equals("UpdateRate")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        ur = Integer.decode(s).intValue();
                                        if (ur > 0) {
                                            System.out.println("Setting update rate to " + ur);
                                            updateRate = ur;
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
            System.err.println("Error when loading extraparameters of MeteoRoquefort: " + e.getMessage());
        }
        //System.out.println("Path to File = " + pathToFile);
    }

    protected abstract String getImagePath();
    protected abstract int getCroppedX();
    protected abstract int getCroppedY();
    protected abstract int getCroppedWidth();
    protected abstract int getCroppedHeight();

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (jcbpf != null) {
                if (jcbpf.isVisible()) {
                    jcbpf.setVisible(false);
                    return;
                } else {
                    if ((jcbpf != null) && (jcbpf.getImage() == image)) {
                        jcbpf.setVisible(true);
                        System.out.println("Using old image");
                        return;
                    }
                }
            }
            jcbpf = new JCrocBarPictureFrame(posx+(width/2), posy+(height/2), frame.getPosx(), frame.getPosy(), frame.getWidth(), frame.getHeight(), bg);
            jcbpf.make(image);
            jcbpf.setVisible(true);
        }

    }


} // End of class
