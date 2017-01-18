
/**
 * Class JCrocBarPictureFrame
 * Frame for displaying a given picture
 * Creation: May, 05, 2009
 * @author Ludovic APVRILLE
 * @see
 */

package uicrocbar;

import crocwidget.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


//import java.io.*;

//import myutil.*;

public class JCrocBarPictureFrame extends JFrame implements Runnable {
    protected Image image;
    protected Image calculatedImage;
    protected MediaTracker media;


    // Main frame position and size
    int posxf, posyf, widthf, heightf;

    //Panel position and size
    int middleposxp, middleposyp;


    protected JCrocBarImagePanel panel;

    // Frame size and position
    protected int posx = 5;
    protected int posy = 5;
    protected int width = 800;
    protected int height = 600;
    protected int sizeOfXBorder = 5;
    protected int sizeOfYBorder = 5;

    protected boolean rescale = true;

    // Attributes
    Color bg = Color.black;


    // Constructor
    public JCrocBarPictureFrame(int _middleposxp, int _middleposyp, int _posxf, int _posyf, int _widthf, int _heightf, Color _bg) {
        middleposxp = _middleposxp;
        middleposyp = _middleposyp;

        posxf = _posxf;
        posyf = _posyf;
        widthf = _widthf;
        heightf = _heightf;

        bg = _bg;
        try {
            setBackground(bg);
        } catch (Exception e) {
        }

    }

    public void setWidthHeight(int _width, int _height) {
        width = _width;
        height = _height;
    }

    public void make(String pathToImage) {

    }

    public void make(Image _image) {
        image = _image;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            setUndecorated(true);
        } catch (Exception e) {
            System.err.println("Exception: title bar could not be disabled");
        }

        Thread t = new Thread(this);
        t.start();

    }

    public void setRescale(boolean b) {
        rescale = b;
    }

    public void run() {
        MediaTracker media = new MediaTracker(this);
        if (image == null) {
            System.out.println("null image");
            return;
        }
        media.addImage(image, 0);

        try {
            //System.out.println("Waiting for image 1");
            media.waitForAll();
        } catch (InterruptedException ie) {
            System.out.println("Media tracker Interrupted");
        }

        media = null;

        int widthP = image.getWidth(null);
        int heightP = image.getHeight(null);

        // Determine size of frame
        if ((rescale) &&((widthP > width) || (heightP > height))) {
            // Must rescale the picture
            float fw = width / (float)widthP;
            float fh = height / (float)heightP;

            float coeff = fw;

            if ((heightP * fw) > height) {
                coeff = fh;
            }
            calculatedImage = image.getScaledInstance((int)(widthP * coeff), (int)(heightP * coeff), Image.SCALE_SMOOTH);
            media = new MediaTracker(this);
            media.addImage(calculatedImage, 0);

            /*try {
              System.out.println("Waiting for image 2");
              media.waitForAll();
              } catch (InterruptedException ie) {
              System.out.println("Media tracker Interrupted");
              }*/
            width = (int)(widthP * coeff) + 2 * sizeOfXBorder;
            height = (int)(heightP * coeff) + 2 * sizeOfYBorder;
        } else {
            width = widthP + 2*sizeOfXBorder;
            height = heightP + 2*sizeOfYBorder;
            calculatedImage = image;
        }

        System.out.println("Images 1 done");



        // Determine the position of the frame
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        if (heightf > widthf) {
            // Vertical bar
            // Put window on the right?
            if (posxf + widthf + width < dim.getWidth()) {
                // Put window on right
                posx = posxf + widthf;
            } else {
                // Put window on left
                posx = posxf - width;
            }
            posy = posyf + middleposyp - (height/2);
            posy = Math.max(0, posy);
            int diff = (int)(dim.getHeight()) - (posy + height);
            if (diff < 0) {
                posy = posy + diff;
            }

        } else {
            // Horizontal bar
            if (posyf + heightf + height < dim.getHeight()) {
                // Put window under
                posy = posyf + heightf;
            } else {
                // Put window on top
                posy = posyf - height;
            }
            posx = posxf + middleposxp - (width/2);
            posx = Math.max(0, posx);
            int diff = (int)(dim.getWidth()) - (posx + width);
            if (diff < 0) {
                posx = posx + diff;
            }

        }

        panel = new JCrocBarImagePanel(this, calculatedImage, media, bg, sizeOfXBorder, sizeOfYBorder, widthP, heightP);
        panel.setBounds(sizeOfXBorder, sizeOfYBorder, widthP+sizeOfXBorder, heightP+sizeOfYBorder);
        add(panel);

        setBounds(posx, posy, width, height);

        if (media != null) {
            try {
                System.out.println("Waiting for image 2");
                media.waitForAll();
            } catch (InterruptedException ie) {
                System.out.println("Media tracker Interrupted");
            }
            System.out.println("Images 2 done");
            panel.repaint();
        }
    }

    public Image getImage() {
        return image;
    }




} // End of class JCrocBarPictureFrame
