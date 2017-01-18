
/**
* Class CustomMenuItemUI
* Menu item
* Creation: Mov, 23, 2009
* @author Ludovic APVRILLE
* @see
*/

package crocwidget;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;



//import java.io.*;

//import myutil.*;
public class CustomMenuItemUI extends BasicMenuItemUI {
    public static ComponentUI createUI(JComponent c) {
        return new CustomMenuItemUI();
    }

    public void paint(Graphics g, JComponent comp) {
        // paint to the buffered image
        BufferedImage bufimg = new BufferedImage(

            comp.getWidth(),
            comp.getHeight(),
            BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = bufimg.createGraphics();
        // restore the foreground color in case the superclass needs it
        g2.setColor(g.getColor());
        super.paint(g2,comp);
        // do an alpha composite
        Graphics2D gx = (Graphics2D) g;
        gx.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,0.8f));
        gx.drawImage(bufimg,0,0,null);
    }

}



