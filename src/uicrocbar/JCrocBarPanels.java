
/**
* Class JCrocBarPanels
* Panel manager for CrocBar Application.
* Creation: May, 27, 2009
* @author Ludovic APVRILLE
* @see
*/

package uicrocbar;

import crocwidget.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


//import java.io.*;

//import myutil.*;

public class JCrocBarPanels extends JPanel implements MouseListener, Runnable  {

    // Attributes
    protected Color bg = Color.blue;
    protected Color fg = Color.red;


    protected JCrocBarFrame frame;
    protected Image backgroundImage;

    // Menus
    protected JPopupMenu menu;
    protected ActionListener menuAL;
    protected JMenuItem nextFace, previousFace, quit, about, help, reload, reloadBackground;
    protected int popupX, popupY;

    protected int width, height;

    ArrayList<CrocWidget> list;
    int currentFace = 0;
    int nbOfFaces;

    JCrocBarFullImagePanel ip;

    // Mouse
    protected boolean rememberPosition;
    protected int rememberX, rememberY;


    // Constructor
    public JCrocBarPanels(JCrocBarFrame _frame) {
        frame = _frame;
        list = new ArrayList<CrocWidget>();
        setLayout(null);


    }

    public Color getBackground() {
        return bg;
    }

    public void build(int _width, int _height) {
        width = _width;
        height = _height;
        ip = new JCrocBarFullImagePanel(frame, this, 0, 0, width, height);
        add(ip);
        setBackground(bg);
        setForeground(fg);

        //Calculate background image
        loadBackgroundImage();

        makeMenu();
        addMouseListener(this);
    }

    private final void makeMenu() {
        menuAL = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popupAction(e);
            }
        };
        // Menus
        nextFace = new JMenuItem("Next face");
        nextFace.addActionListener(menuAL);
        nextFace.setBackground(bg);
        nextFace.setForeground(fg);

        previousFace = new JMenuItem("Previous face");
        previousFace.addActionListener(menuAL);
        previousFace.setBackground(bg);
        previousFace.setForeground(fg);

        if (getNbOfFaces() < 2) {
            nextFace.setEnabled(false);
            previousFace.setEnabled(false);
        }

        about = new JMenuItem("About");
        about.addActionListener(menuAL);
        about.setBackground(bg);
        about.setForeground(fg);

        help = new JMenuItem("Help");
        help.addActionListener(menuAL);
        help.setBackground(bg);
        help.setForeground(fg);

        quit = new JMenuItem("Quit CrocBar");
        quit.addActionListener(menuAL);
        quit.setBackground(bg);
        quit.setForeground(fg);

        reload = new JMenuItem("Reload");
        reload.addActionListener(menuAL);
        reload.setBackground(bg);
        reload.setForeground(fg);

        reloadBackground = new JMenuItem("Reload background");
        reloadBackground.addActionListener(menuAL);
        reloadBackground.setBackground(bg);
        reloadBackground.setForeground(fg);

        menu = new JPopupMenu();
        menu.setBackground(bg);
        menu.setForeground(fg);

        menu.add(previousFace);
        menu.add(nextFace);
        menu.addSeparator();
        menu.add(about);
        menu.add(help);
        menu.addSeparator();
        menu.add(reload);
        menu.addSeparator();
        menu.add(reloadBackground);
        menu.addSeparator();
        menu.add(quit);
    }

    public void paintComponent(Graphics g) {
        if (bg.getAlpha() < 255) {
            g.drawImage(backgroundImage, 0, 0, frame);
        }
        g.setColor(bg);
        g.fillRect(0, 0, width, height);
    }

    public int getNbOfFaces() {
        return nbOfFaces;
    }

    public int getCurrentFace() {
        return currentFace;
    }

    public void makeFaces(int _nbOfFaces) {
        nbOfFaces = _nbOfFaces;
        updatePanelOnFace();
    }

    public int getFaceMode() {
        return 1;
    }

    public void setNextFace() {
        if (nbOfFaces > 1) {
            currentFace ++;
            if (currentFace == nbOfFaces) {
                currentFace = 0;
            }
        }
    }

    public void setPreviousFace() {
        if (nbOfFaces > 1) {
            currentFace --;
            if (currentFace == -1) {
                currentFace = nbOfFaces - 1;
            }
        }
    }

    /*public void nextFace() {
    int tmp = currentFace;
    if (nbOfFaces > 1) {
    currentFace ++;
    if (currentFace == nbOfFaces) {
    currentFace = 0;
    }
    setFace(true, 0, tmp);
    }
    }

    public void previousFace() {
    int tmp = currentFace;
    if (nbOfFaces > 1) {
    currentFace --;
    if (currentFace == -1) {
    currentFace = nbOfFaces - 1;
    }
    setFace(true, 1, tmp);
    }
    }

    public void setFace(boolean anim, int mode, int oldFace) {
    //System.out.println("CurrentFace = " + currentFace + " anim=" + anim);
    if (anim) {
    ip.go(mode, oldFace, currentFace);
    }
    for(CrocWidget cw: list) {
    cw.setVisible(cw.getFaces()[currentFace]);
    }
    if (anim) {
    ip.ready();
    }
    }*/

    public void nextFace() {
        ip.nextFace();
    }

    public void previousFace() {
        ip.previousFace();
    }

    public void updatePanelOnFace() {
        for(CrocWidget cw: list) {
            cw.setVisible(cw.getFaces()[currentFace]);
        }
    }

    public void repaintAll() {
        for(CrocWidget cw: list) {
            if (cw.getFaces()[currentFace]) {
                cw.repaint();
            }
        }
    }

    public ArrayList<CrocWidget> getList() {
        return list;
    }


    public void addToPanels(CrocWidget cw, boolean [] faces) {
        //list.add(cw);
        add(cw);
    }

    public void run() {
        reloadBackgroundImageGo();
    }

    protected void reloadBackgroundImage() {
        Thread t = new Thread(this);
        t.start();
    }

    protected void reloadBackgroundImageGo() {
        Image img;
        System.out.println("Reloading background image");
        frame.setVisible(false);

        try {
            Thread.currentThread().sleep(1000);
        } catch (Exception e) {
            System.out.println("Interrupted");
        }

        backgroundImage = null;
        loadBackgroundImage();
        for(CrocWidget cw: list) {
            if (cw instanceof UseBackgroundImage) {
                img = cw.loadBackgroundImage();
                ((UseBackgroundImage)cw).setBackgroundImage(img);
                cw.repaint();
            }
        }
        frame.setVisible(true);
    }

    protected void loadBackgroundImage() {
        loadBackgroundImage(frame.getPosx(), frame.getPosy());
    }

    protected void loadBackgroundImage(int _x, int _y) {
        try {
            Robot rbt = new Robot();
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension dim = tk.getScreenSize();
            backgroundImage = rbt.createScreenCapture(new Rectangle(_x, _y, width, height));
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(backgroundImage, 0);
            mt.waitForID(0);
            backgroundImageReady();
        } catch (Exception e) {
            System.err.println("Pb when capturing background image");
            return;
        }
    }

    public void addWidget(CrocWidget cw, boolean[] faces) {
        list.add(cw);
    }

    public void setBg(Color _bg) {
        bg = _bg;
    }

    public void setFg(Color _fg) {
        fg = _fg;
    }

    // Menu management
    public void openPopupMenu(int x, int y) {
        popupX = x;
        popupY = y;
        menu.show(this, x, y);
    }

    public void popupAction(ActionEvent e) {
        if (e.getSource() == quit) {
            System.exit(0);
        } else if (e.getSource() == reload) {
            frame.reload();
        } else if (e.getSource() == reloadBackground) {
            reloadBackgroundImage();
        } else if (e.getSource() == about) {
            JDialogInfo jdi = new JDialogInfo(frame, "About", getAboutString());
            jdi.setVisible(true);
        } else if (e.getSource() == help) {
            JDialogInfo jdi = new JDialogInfo(frame, "Help", getHelpString());
            jdi.setVisible(true);
        } else if (e.getSource() == nextFace) {
            nextFace();
        } else if (e.getSource() == previousFace) {
            previousFace();
        }
    }


    protected String getAboutString() {
        String s = "CrocBar\nProgrammed by L. Apvrille, (C), 2009";
        return s;
    }

    protected String getHelpString() {
        String s = "You must provide, as argument a config.xml file:\n";
        s += "Example of such a file:\n";
        s += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
        s += "<CrocBarConfiguration>\n";
        s += "	<CrocBarSizeAttributes x=\"105\" y=\"205\" width=\"120\" height=\"400\" />\n";
        s += "	<CrocBarColorAttributes bg=\"50,40,40\" fg=\"42,218,33\" />\n";
        s += "	<CrocBarTableAttributes cellx=\"3\" celly=\"4\" />\n";
        s += "	<CrocWidget type=\"TimeAndDate\" poscellx=\"0\" poscelly=\"0\" nbcellx=\"3\" nbcelly=\"1\" bg=\"50,134,176\" fg=\"42,12,33\">\n";
        s += "		<ExtraParams>\n			<ShowSeconds value=\"false\" />\n			<ShowYear value=\"true\" />\n		</ExtraParams>\n";
        s += "	</CrocWidget>\n";
        s += "	<CrocWidget type=\"TimeAndDate\" poscellx=\"1\" poscelly=\"2\" nbcellx=\"2\" nbcelly=\"2\" bg=\"50,134,176\" fg=\"42,12,33\" />\n";
        s += "	<CrocWidget type=\"SinglePicture\" poscellx=\"0\" poscelly=\"1\" nbcellx=\"3\" nbcelly=\"1\" bg=\"50,134,176\" fg=\"42,12,33\">\n";
        s += "		<ExtraParams>\n			<PathToFile value=\"U:\\Perso\\pico-main-lafraise.jpg\" />\n		</ExtraParams>\n";
        s += "	</CrocWidget>\n";
        s += "</CrocBarConfiguration>\n";
        return s;
    }

    protected synchronized Image getBackgroundImage() {
        while (backgroundImage == null) {
            try {
                wait();
            } catch (InterruptedException ie) {
            }
        }
        return backgroundImage;
    }

    protected synchronized void backgroundImageReady() {
        notifyAll();
    }

    // Mouse management
    public void mousePressed(MouseEvent e) {
        rememberPosition = false;
        if (e.getButton() == MouseEvent.BUTTON3) {
            openPopupMenu(e.getX(), e.getY());
        }

        if ((e.getButton() == MouseEvent.BUTTON1) && (getNbOfFaces() > 1)) {
            rememberPosition = true;
            rememberX = e.getX();
            rememberY = e.getY();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (rememberPosition) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                int x = e.getX();
                int y = e.getY();
                // See whether another face must be selected
                int width = Math.abs(rememberX - x);
                int height = Math.abs(rememberY - y);
                if (width > 50) {
                    if (width > (2*height)) {
                        if (rememberX > x) {
                            nextFace();
                        } else {
                            previousFace();
                        }
                    }
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
    }




} // End of class

