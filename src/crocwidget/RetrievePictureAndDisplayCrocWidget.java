
/**
 * Class RetrievePictureAndDisplay
 * Widget for displaying a picture that has been fetched from another place
 * Creation: Jan, 22, 2016
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

public class RetrievePictureAndDisplayCrocWidget  extends CrocWidget implements Runnable   {

    protected int updateRate;       // Rate of update
    protected String pathToFile;    // Path  to the file containing the image
    protected String pathToCommand; // command to be executed

    protected Image image, smallImage;

    protected JCrocBarPictureFrame jcbpf;
    protected ImageWork imageWork;


    // Constructor
    public RetrievePictureAndDisplayCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        // To load the image and update it
        //Thread t = new Thread(this);
        //t.start();
        smallImage = null;

    }

    public void paintComponent(Graphics g) {
        //System.out.println("Painting components in Retrieve Display rate=" + updateRate);
        if (smallImage != null) {
            g.drawImage(smallImage, 0, 0, frame);
        } else {
            System.out.println("Null small imag in Retrieve Display");
        }

    }

    public void execute(String command) {
        String result;
        String str;

        BufferedReader proc_in;
        DataInputStream dis;
        DataOutputStream dos;
        Process proc;
        String out0;

        if ((command == null) || (command.length() == 0)) {
            return;
        }

        try {
            out0 = "";

            proc = Runtime.getRuntime().exec(command);
            dis = new DataInputStream(proc.getInputStream());

            while ((str = dis.readLine()) != null) {
                out0 = str;
                //System.out.println("read2:" + str);
            }
        } catch (IOException e) {
            System.out.println("IOException in RetrievePictureAndDisplay: "+ e.getMessage());
        }
    }

    public void run() {
        loadImages();
    }

    public void loadImages() {
        if ((pathToFile == null) || (pathToFile.length() == 0) || (pathToCommand == null) || (pathToCommand.length() == 0)) {
            return;
        }

        // Execute command
        System.out.println("Retrieve display image: executing: " + pathToCommand + " updateRate=" + updateRate);
        //newTimer(updateRate);
        execute(pathToCommand);
        //System.out.println("Execution done in retrieve display");


        // Read and display image
        //System.out.println("Loading images retrieve display");
        imageWork = new ImageWork(this);
        imageWork.pathToFile = pathToFile;
        imageWork.urgent = false;
        imageWork.load = true;
        imageWork.crop = false;
        imageWork.scale = true;
        //imageWork.xC = getCroppedX();
        //imageWork.yC = getCroppedY();
        //imageWork.widthC = getCroppedWidth();
        //imageWork.heightC = getCroppedHeight();
        imageWork.widthS = width;
        imageWork.heightS = height;
        ilt.addImageWork(imageWork);
    }

    public void imageWorkDone(Image img) {
        //System.out.println("Images loaded retrieve display=" + img + " updateRate=" + updateRate);
        smallImage = img;
        image = imageWork.image;
        imageWork = null;
        repaint();
        //newTimer(updateRate);
    }

    public void newPeriod() {
        System.out.println("Timer expired in Retrieve display image: executing: " + pathToCommand + " updateRate=" + updateRate);
        //newTimer(updateRate);
        System.out.println("Uploading image");
        loadImages();
    }

    public void loadExtraParam(NodeList nl) {
        pathToFile = "";
        updateRate = 60000;
        pathToCommand = "";
        //System.out.println("*** load extra params : updateRate *** ");
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

                            if (elt.getTagName().equals("PathToFile")) {
                                s = elt.getAttribute("value");
                                if ((s != null) && (s.length() > 0)) {
                                    pathToFile = s;
                                }
                            }

                            if (elt.getTagName().equals("PathToCommand")) {
                                s = elt.getAttribute("value");
                                if ((s != null) && (s.length() > 0)) {
                                    pathToCommand = s;
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            //System.out.println("Error");
            System.err.println("Error when loading extraparameters of RetrievePicture: " + e.getMessage());
        }
        System.out.println("Update rate=" + updateRate);
        Thread t = new Thread(this);
        t.start();
        newPeriodicTimer(updateRate);

        //timerExpired();
    }

    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked in RetrievePictureAndDisplay widget");
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


    protected String getAboutString() {
        String s = "RetrievePictureAndDisplay CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "RetrievePictureAndDisplay Widget options:\n";
        s += "* UpdateRate <int in ms> (default = 60000)\n";
        s += "* PathToFile <String> (default = none)\n";
        s += "* PathToCommand <String> (default = none)\n";
        return s;
    }

} // End of class
