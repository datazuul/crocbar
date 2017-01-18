
/**
* Class JCrocBarPanel
* Main panel for CrocBar Application.
* Creation: May, 05, 2009
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

public class JCrocBarPanel extends JPanel  implements MouseListener {

    // Attributes
    protected Color bg = Color.blue;
    protected Color fg = Color.red;


    // Menus
    protected JPopupMenu menu;
    protected ActionListener menuAL;
    protected JMenuItem quit, about, help;
    protected int popupX, popupY;

    protected int width, height;

    // CrocWidgets
    ArrayList<CrocWidget> list;

    protected JCrocBarFrame frame;
    protected Image backgroundImage;


    // Constructor
    public JCrocBarPanel(JCrocBarFrame _frame) {
        super();
        frame = _frame;
        System.out.println("Width=" + width + " height=" + height);
        list = new ArrayList<CrocWidget>();
        setLayout(null);
    }

    public void build(int _width, int _height) {
        width = _width;
        height = _height;
        setBackground(bg);
        setForeground(fg);

        //Calculate background image
        loadBackgroundImage();

        /*if (bg.getAlpha() < 255) {
        	// Must do the background image
        	System.out.println("Making background image");
        	Thread t = new Thread(this);
        	t.setPriority(Thread.MIN_PRIORITY+1);
        	t.start();
        }*/

        makeMenu();
        addMouseListener(this);
    }

    private final void makeMenu() {
        // Menus

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

        menu = new JPopupMenu();
        menu.setBackground(bg);
        menu.setForeground(fg);

        menu.add(about);
        menu.add(help);
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

    public void addWidget(CrocWidget cw) {
        list.add(cw);
    }

    public ArrayList<CrocWidget> getList() {
        return list;
    }

    // Colors
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
        } else if (e.getSource() == about) {
            JDialogInfo jdi = new JDialogInfo(frame, "About", getAboutString());
            jdi.setVisible(true);
        } else if (e.getSource() == help) {
            JDialogInfo jdi = new JDialogInfo(frame, "Help", getHelpString());
            jdi.setVisible(true);
        }
    }

    // Loading background image
    /*public void run() {
    	try {
    		Robot rbt = new Robot();
    		Toolkit tk = Toolkit.getDefaultToolkit();
    		Dimension dim = tk.getScreenSize();
    		background = rbt.createScreenCapture(new Rectangle(frame.getPosx(), frame.getPosy(), width, height));
    	} catch (Exception e) {
    		System.out.println("Pb when capturing background image");
    		return;
    	}
    	System.out.println("Background image is ready");

    }*/

    protected void loadBackgroundImage() {
        try {
            Robot rbt = new Robot();
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension dim = tk.getScreenSize();
            backgroundImage = rbt.createScreenCapture(new Rectangle(frame.getPosx(), frame.getPosy(), width, height));
            MediaTracker mt = new MediaTracker(this);
            mt.addImage(backgroundImage, 0);
            mt.waitForID(0);
            backgroundImageReady();
        } catch (Exception e) {
            System.out.println("Pb when capturing background image");
            return;
        }
    }


    // Mouse management
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            openPopupMenu(e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
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




} // End of class JCrocBarFrame

