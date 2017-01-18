
/**
* Class JCrocBarFullImagePanel
* Panel for picture of all widgets (transition between faces)
* Creation: May, 27, 2009
* @author Ludovic APVRILLE
* @see
*/

package uicrocbar;

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

public  class JCrocBarFullImagePanel extends JPanel implements Runnable   {

    protected Image imageN;

    protected int decX, decY;
    //protected int modeDec;
    protected int oldFace, newFace;
    protected JCrocBarFrame frame;
    protected int width, height;

    protected boolean busy;

    protected int changeOfFace = 0;
    protected boolean next;

    JCrocBarPanels panels;

    // Constructor
    public JCrocBarFullImagePanel(JCrocBarFrame _frame, JCrocBarPanels _panels, int _posx, int _posy, int _width, int _height) {
        frame = _frame;
        panels = _panels;
        width = _width;
        height = _height;
        setBounds(_posx, _posy, width, height);
        setVisible(false);
        // To load the images
        Thread t = new Thread(this);
        t.setPriority(Thread.MIN_PRIORITY+3);
        t.start();
    }

    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        //g.setColor(bg);
        if (imageN != null) {
            /*Rectangle clip = g.getClipBounds();
            g.setColor(new Color(0, 0, 255, alpha));
            g.fillRect(clip.x, clip.y, clip.width, clip.height);*/

            //g.setColor(new Color(0, 0, 0, 0));

            g.drawImage(frame.getBackgroundImage(), 0, 0, frame);
            g.setColor(frame.getBackground());
            g.fillRect(0, 0, width, height);
            g.setColor(Color.white);
            Font f = g.getFont();
            g.setFont(f.deriveFont(Font.BOLD, 150));
            String s = "" + newFace;
            int w = g.getFontMetrics().stringWidth(s);
            g.drawString(s, (width-w)/2, (height / 2));


            int x;
            if (next) {
                x = 0-decX;
            } else {
                x = decX;
            }

            g.drawImage(imageN, x, 0, frame);

            /*s = "" + oldFace;
            w = g.getFontMetrics().stringWidth(s);
            g.drawString(s, ((width-w)/2) + x, 75 + (height / 2));*/
            g.setFont(f);
        }
        //g.fillRect(decX, 0, width, height);
    }

    public synchronized void nextFace() {
        changeOfFace ++;
        notifyAll();
    }

    public synchronized void previousFace() {
        changeOfFace --;
        notifyAll();
    }

    public synchronized void waitForChange() {
        while(changeOfFace == 0) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }

        if (changeOfFace > 0) {
            next = true;
            changeOfFace --;
        } else {
            next = false;
            changeOfFace ++;
        }
    }

    public synchronized void go(int _modeDec, int _oldFace, int _newFace) {
        while(busy == true) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }

        notifyAll();
        try {
            wait();
        } catch (InterruptedException ie) {
        }
    }

    public synchronized void loadImage() {
        try {
            Robot rbt = new Robot();
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension dim = tk.getScreenSize();
            imageN = rbt.createScreenCapture(new Rectangle(frame.getPosx(), frame.getPosy(), width, height));
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(imageN, 0);
            mt.waitForID(0);
        } catch (Exception e) {
            System.out.println("Pb when capturing background image");
        }
        notifyAll();
    }


    public void run() {

        while(true) {
            waitForChange();

            loadImage();

            oldFace = panels.getCurrentFace();
            if (next) {
                panels.setNextFace();
            } else {
                panels.setPreviousFace();
            }
            newFace = panels.getCurrentFace();
            //modeDec = panels.getFaceMode();

            panels.updatePanelOnFace();

            setVisible(true);

            //System.out.println("anim on");

            decX = 0;
            long wait = 25;
            long start = System.currentTimeMillis();
            long waiting;
            long nextTime;

            for(int cpt=0; cpt<30; cpt++) {
                decX = decX + 15;
                if (decX > (width + 50)) {
                    break;
                }
                //frame.repaintAll();
                repaint();
                //nextTime = start + cpt * wait;
                //waiting = System.currentTimeMillis() - nextTime;
                //if (waiting > 0) {
                try {
                    Thread.currentThread().sleep(wait);
                } catch (InterruptedException ie) {
                    //System.out.println("Interrupted");
                }
                //}
            }
            setVisible(false);
            //release();
        }

        //System.out.println("anim off");
    }



} // End of class

