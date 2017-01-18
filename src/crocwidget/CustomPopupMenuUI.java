
/**
* Class CustomPopupMenuUI
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
public class CustomPopupMenuUI extends BasicPopupMenuUI {
    public static ComponentUI createUI(JComponent c) {
        return new CustomPopupMenuUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        popupMenu.setOpaque(false);
    }

    public Popup getPopup(JPopupMenu popup, int x, int y) {
        Popup pp = super.getPopup(popup,x,y);
        JPanel panel = (JPanel)popup.getParent();
        panel.setOpaque(false);
        return pp;
    }

}

