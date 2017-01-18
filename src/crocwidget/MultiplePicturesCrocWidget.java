
/**
 * Class MultiplePicturesCrocWidget
 * Widget for displaying pictures
 * Creation: May, 06, 2009
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

import myutil.*;

public  class MultiplePicturesCrocWidget extends CrocWidget implements Runnable, UseBackgroundImage   {
    protected ArrayList<String> paths;
    protected int nbOfImages;
    protected Image image;
    protected Image [] smallImages;
    protected int currentIndex;
    protected int waitingTime;

    protected MediaTracker media;

    protected JCrocBarPictureFrame jcbpf;

    protected Image backgroundImage;

    protected boolean entered;
    protected boolean paused;


    // Constructor
    public MultiplePicturesCrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        super(_frame, _panel, _posx, _posy, _width, _height, _bg, _fg, _nl, _listData, _faces);

        nbOfImages = paths.size();
        smallImages = new Image[nbOfImages];


        // To load the image
        Thread t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY+1);
        t.start();
    }

    public void paintComponent(Graphics g) {
        Image img = null;
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
        if (currentIndex > -1) {
            try {
                img = smallImages[currentIndex];

            } catch (Exception e) {
                return;
            }
        }

        if (img != null) {
            // Center image
            int widthP = img.getWidth(null);
            int heightP = img.getHeight(null);
            g.drawImage(img, (width-widthP)/2, (height-heightP)/2, frame);

            /*if (entered) {
            g.draw
            }*/

            return;
        } else {
            g.drawString("Loading...", 3, height/2);
        }

    }

    public void loadImage(int index) {
        image = getImage(paths.get(index));
        media.addImage(image, 0);
        try {
            media.waitForID(0);
        } catch (InterruptedException ie) {
            System.out.println("Media tracker Interrupted");
        }

        if (image == null) {
            System.out.println("Could not load image: " + paths.get(index));
            return;
        }

        int widthP = image.getWidth(null);
        int heightP = image.getHeight(null);
        float fw = width / (float)widthP;
        float fh = height / (float)heightP;
        float coeff = fw;

        if ((heightP * fw) > height) {
            coeff = fh;
        }

        smallImages[index] = image.getScaledInstance((int)(widthP * coeff), (int)(heightP * coeff), Image.SCALE_SMOOTH);
        media.addImage(smallImages[index], 0);
        try {
            media.waitForID(0);
        } catch (InterruptedException ie) {
            System.out.println("Media tracker Interrupted");
        }
        //image.flush();
        //image = null;

        //System.out.println("Image " + index + " loaded");
    }


    public void run() {
        backgroundImage = loadBackgroundImage();

        media = new MediaTracker(this);
        int nextIndex = -1;

        if (nbOfImages >= 1) {
            currentIndex = -1;
            while(true) {
                if (currentIndex == -1) {
                    Random randomGenerator = new Random();
                    nextIndex = randomGenerator.nextInt(nbOfImages);
                } else {
                    nextIndex  = (currentIndex  + 1)% nbOfImages;
                }
                // Check Whether picture to display is loaded.
                if (smallImages[nextIndex] == null) {
                    // Load image
                    loadImage(nextIndex);
                }

                currentIndex = nextIndex;
                repaint();

                try {
                    Thread.currentThread().sleep(waitingTime);
                } catch (InterruptedException ie) {
                }

                //System.out.println("Ready to print pictures:" + currentIndex);
                //currentIndex = (currentIndex  + 1)% nbOfImages;
                //System.out.println("currentIndex:" + currentIndex);
            }
        } else {
            repaint();
        }
    }



    public void loadExtraParam(NodeList nl) {
        paths = new ArrayList<String>();
        waitingTime = 10000;
        paused = false;
        System.out.println("*** load extra params : path *** ");
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
                            if (elt.getTagName().equals("PathToFile")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                if (s != null) {
                                    paths.add(s);
                                }
                            }

                            if (elt.getTagName().equals("PathToLocalFile")) {
                                //System.out.println("Path to file!");
                                s = elt.getAttribute("value");
                                //System.out.println("value=" +s );
                                loadLocalFile(s);
                            }

                            if (elt.getTagName().equals("WaitingTime")) {

                                s = elt.getAttribute("value");
                                try {
                                    if (s != null) {
                                        wt = Integer.decode(s).intValue();
                                        if (wt > 0) {
                                            //System.out.println("Setting waiting time to " + waitingTime);
                                            waitingTime = wt;
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
            System.err.println("Error when loading extraparameters of MultiplePictures: " + e.getMessage());
        }
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
                        paths.add(line);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("Error when reading image paths: " + e.getMessage());
        }
    }




    protected String getAboutString() {
        String s = "MultiplesPictures CrocWidget\nProgrammed by L. Apvrille";
        return s;
    }

    protected String getHelpString() {
        String s = "SinglePicture Widget options:\n";
        s += "* PathToFile <String> (default = none)\n";
        s += "* PathToLocalFile <String> (default = none)\n";
        s += "[This option may be used several times to configure selected pictures]\n";
        s += "* WaitingTime <int value in ms> (default = none)\n";
        return s;
    }


    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked in MultiplePictures widget");
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (jcbpf != null) {
                if (jcbpf.isVisible()) {
                    jcbpf.setVisible(false);
                    return;
                } else {
                    if ((jcbpf != null) && (image != null) && (jcbpf.getImage() == image)) {
                        jcbpf.setVisible(true);
                        System.out.println("Using image");
                        return;
                    }
                }
            }
            jcbpf = new JCrocBarPictureFrame(posx+(width/2), posy+(height/2), frame.getPosx(), frame.getPosy(), frame.getWidth(), frame.getHeight(), bg);
            jcbpf.make(image);
            jcbpf.setVisible(true);
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

    /*public void mouseEntered(MouseEvent e) {
      if ((jcbpf != null) && (jcbpf.getImage() == images[currentIndex])) {
      jcbpf.setVisible(true);
      System.out.println("Using old image");
      } else {
      jcbpf = new JCrocBarPictureFrame(posx+(width/2), posy+(height/2), frame.getPosx(), frame.getPosy(), frame.getWidth(), frame.getHeight(), bg);
      jcbpf.make(images[currentIndex]);
      jcbpf.setVisible(true);
      }
      }*/

    /*public void mouseExited(MouseEvent e) {
        //System.out.println("Mouse exited");
        if (jcbpf != null) {
            jcbpf.setVisible(false);
        }
        //jcbpf = null;
    }*/

    public void setBackgroundImage(Image _backgroundImage) {
        backgroundImage = _backgroundImage;
    }



} // End of class SinglePictureCrocWidget
