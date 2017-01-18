
/**
* Class RandomPictureWhenClickCrocWidget
* Widget for displaying a random picture
* Creation: May, 18, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;

import uicrocbar.*;
import myutil.*;

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

public  class RandomPictureWhenClickCrocWidget extends CrocWidget implements Runnable   {

    protected String pathToFile;
    protected Image image, smallImage;
    protected ArrayList<String> randomPaths;

    protected JCrocBarPictureFrame jcbpf;
    protected Image randomImage;
    protected boolean mustLoadRandomImage;
    protected MediaTracker media;

    // Constructor
    public RandomPictureWhenClickCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the image
        Thread t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY+2);
        t.start();
    }

    public void paintComponent(Graphics g) {
        if (smallImage != null) {
            g.drawImage(smallImage, 0, 0, frame);
        }
    }

    public void run() {
        image = getImage(pathToFile);
        smallImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        repaint();
    }



    public void loadExtraParam(NodeList nl) {
        pathToFile = "";
        randomPaths = new ArrayList<String>();
        mustLoadRandomImage = true;
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
                            if (elt.getTagName().equals("PathToImage")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    pathToFile = s;
                                }
                            }

                            if (elt.getTagName().equals("PathToLocalFile")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                loadLocalFile(s);
                            }

                            if (elt.getTagName().equals("PathToRandomImage")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    randomPaths.add(s);
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
        System.out.println("Random images = " + randomPaths.size());
        loadRandomImage();
    }

    protected void loadLocalFile(String _filename) {
        File file = new File(_filename);

        if (!FileUtils.checkFileForOpen(file)) {
            System.out.println("File " + _filename + " could not be opened");
            return;
        }

        String data = FileUtils.loadFileData(file);

        if (data == null) {
            System.out.println("File " + _filename + " could not be loaded");
            return;
        }

        System.out.println("Local file loaded:" + _filename);

        // Reading string
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String line;
        try {
            while((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (!line.startsWith("#")) {
                        randomPaths.add(line);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("Error when reading image paths: " + e.getMessage());
        }
    }

    protected String getAboutString() {
        String s = "RandomPictureWhenClick CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "RandomPictureWhenClick Widget options:\n";
        s += "* PathToImage <String> (default = none)\n";
        s += "* PathToLocalFile <String> (default = none)\n";
        s += "* PathToRandomImage<String> (default = none)\n";
        return s;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (jcbpf != null) {
                jcbpf.dispose();
                jcbpf = null;
                loadRandomImage();
            } else {
                if (randomPaths.size() > 0) {
                    if (randomImage == null) {
                        loadRandomImage();
                    }
                    jcbpf = new JCrocBarPictureFrame(posx+(width/2), posy+(height/2), frame.getPosx(), frame.getPosy(), frame.getWidth(), frame.getHeight(), bg);
                    jcbpf.setWidthHeight(1024, 768);
                    jcbpf.setRescale(true);
                    jcbpf.make(randomImage);
                    mustLoadRandomImage = true;
                    jcbpf.setVisible(true);
                }
            }
        }
    }

    public void loadRandomImage() {
        if (randomPaths.size() > 0) {
            int random = ((int)(Math.ceil(Math.random() * (randomPaths.size())))) - 1;
            media = new MediaTracker(this);
            //System.out.println("Preload image");
            randomImage = getImage(randomPaths.get(random));
            media.addImage(randomImage, 0);
            mustLoadRandomImage = false;
        }
    }




} // End of class RandomPictureWhenClickCrocWidget

