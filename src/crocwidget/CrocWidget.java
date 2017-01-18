
/**
 * Class CrocWidget
 * Widget main class
 * Creation: May, 05, 2009
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

public abstract class CrocWidget extends JPanel implements MouseListener {

    // Operating system
    protected static final String DETECT_COMMAND = "/bin/uname";
    protected final int OS_NOT_DETECTED = 0;
    protected final int LINUX = 1;
    protected final int SOLARIS = 2;
    protected final int MACOS = 3;
    protected final int UNKNOWN_OS = 4;
    protected int OS = OS_NOT_DETECTED;

    // ImageLoaderThread
    protected static ImageLoaderThread ilt;


    // Panel
    protected JCrocBarFrame frame;
    protected JCrocBarPanels mainPanel;

    // Frame size and position
    protected int posx, posy, width, height;
    protected Color fg, bg;

    protected boolean faces[];

    // Menus
    protected JPopupMenu menu;
    protected ActionListener menuAL;
    protected JMenuItem nextFace, previousFace, quit, about, help, reload;
    protected int popupX, popupY;

    // Mouse
    protected boolean rememberPosition;
    protected int rememberX, rememberY;

    // Timer
    protected static java.util.Timer timer;
    protected TimerTaskCrocWidget ttcw;


    // Constructor
    public CrocWidget(JCrocBarFrame _frame, JCrocBarPanels _panel, int _posx, int _posy, int _width, int _height, Color _bg, Color _fg, NodeList _nl, NodeList _listData, boolean [] _faces) {
        frame = _frame;
        mainPanel = _panel;
        posx = _posx;
        posy = _posy;
        width = _width;
        height = _height;
        bg = _bg;
        fg = _fg;
        faces = _faces;

        if (timer == null) {
            timer = new java.util.Timer(true);
        }

        if (ilt == null) {
            ilt = new ImageLoaderThread();
            ilt.start();
        }

        loadExtraParam(_nl);
        loadExtraData(_listData);

        //System.out.println("Setting bounds x=" + posx + " y=" + posy + " width=" + width + " height=" + height);
        setBounds(posx, posy, width, height);
        setBackground(bg);
        setForeground(bg);
        mainPanel.addToPanels(this, faces);

        makeMenu();

        addMouseListener(this);
    }

    public boolean[] getFaces() {
        return faces;
    }

    private final void makeMenu() {
        // Menus
        menuAL = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popupAction(e);
            }
        };

        nextFace = new JMenuItem("Next face");
        nextFace.addActionListener(menuAL);
        nextFace.setBackground(bg);
        nextFace.setForeground(fg);

        previousFace = new JMenuItem("Previous face");
        previousFace.addActionListener(menuAL);
        previousFace.setBackground(bg);
        previousFace.setForeground(fg);

        if (mainPanel.getNbOfFaces() < 2) {
            nextFace.setEnabled(false);
            previousFace.setEnabled(false);
        }

        quit = new JMenuItem("Quit CrocBar");
        quit.addActionListener(menuAL);
        quit.setBackground(bg);
        quit.setForeground(fg);


        about = new JMenuItem("About");
        about.addActionListener(menuAL);
        about.setBackground(bg);
        about.setForeground(fg);

        help = new JMenuItem("Help");
        help.addActionListener(menuAL);
        help.setBackground(bg);
        help.setForeground(fg);

        reload = new JMenuItem("Reload");
        reload.addActionListener(menuAL);
        reload.setBackground(bg);
        reload.setForeground(fg);

        quit = new JMenuItem("Quit CrocoBar");
        quit.addActionListener(menuAL);
        quit.setBackground(bg);
        quit.setForeground(fg);

        try {
            //UIManager.put("Menu.selectionBackground", Color.red);
            //UIManager.put("MenuItem.selectionBackground", Color.red);
            //UIManager.put("PopupMenuUI","crocwidget.CustomPopupMenuUI");
            //UIManager.put("MenuItemUI","crocwidget.CustomMenuItemUI");
        } catch (Exception e) {
            System.out.println("Exception when setting colors:" + e.getMessage());
        }
        //System.out.println("Setting colors");

        menu = new JPopupMenu();
        menu.setLightWeightPopupEnabled(false);
        menu.setBackground(Color.blue);
        menu.setForeground(Color.green);
        menu.setOpaque(false);

        makeExtraMenu();

        menu.add(previousFace);
        menu.add(nextFace);
        menu.addSeparator();
        menu.add(about);
        menu.add(help);
        menu.addSeparator();
        menu.add(reload);
        menu.addSeparator();
        menu.add(quit);

    }

    // Method called to add items to the right-click menu
    protected void makeExtraMenu() {
    }

    // Menu management
    public void popupAction(ActionEvent e) {
        if (e.getSource() == quit) {
            System.exit(0);
        } else if (e.getSource() == reload) {
            frame.reload();
        } else if (e.getSource() == about) {
            JDialogInfo jdi = new JDialogInfo(frame, "About", getAboutString());
            jdi.setVisible(true);
        } else if (e.getSource() == help) {
            JDialogInfo jdi = new JDialogInfo(frame, "Help", getHelpString());
            jdi.setVisible(true);
        } else if (e.getSource() == nextFace) {
            mainPanel.nextFace();
        } else if (e.getSource() == previousFace) {
            mainPanel.previousFace();
        } else {
            extraPopupAction(e);
        }
    }

    // Method called when an action on a additional menu item has been performed
    protected void extraPopupAction(ActionEvent e) {
    }



    public void openPopupMenu(int x, int y) {
        popupX = x;
        popupY = y;

        //System.out.println("Showing menu color=" + menu.getBackground());

        menu.show(this, x, y);
    }

    public Image getImage(String s) {
        if (s.startsWith("http://")) {
            try {
                // Create a URL for the image's location
                URL url = new URL(s);

                // Get the image
                Image img = Toolkit.getDefaultToolkit().createImage(url);
                img.flush();
                //System.out.println("Got image: w=" + img.getWidth(null) + " h=" + img.getHeight(null));
                return img;
            } catch (MalformedURLException e) {
                System.err.println("Error when loading image:" + s);
                return null;
            } /*catch (IOException e) {
                System.err.println("Error when loading image:" + s);
                return null;
                }*/
        } else {
            URL url = this.getClass().getResource(s);

            if (url != null)  {
                Image img = new ImageIcon(url).getImage();
                //System.out.println("Got Image");
                return img;
            }

            // Trying to load as a regular file
            //Image img = Toolkit.getDefaultToolkit().createImage(getClass().getResource(s));
            try {
                File f = new File(s);
                BufferedImage bimg = ImageIO.read(new File(s));
                if (bimg != null) {
                    return bimg;
                }
                //f.close();
            } catch (Exception e) {
            }

            System.err.println("Error when loading image:" + s);
            System.out.println("Could not load " + s);
            return null;

        }
    }

    public Image loadBackgroundImage() {
        return loadBackgroundImage(0);
    }

    protected Image loadBackgroundImage(int id) {
        /*Image background;
          try {
          Robot rbt = new Robot();
          Toolkit tk = Toolkit.getDefaultToolkit();
          Dimension dim = tk.getScreenSize();
          background = rbt.createScreenCapture(new Rectangle(frame.getPosx()+posx, frame.getPosy()+posy, width, height));
          } catch (Exception e) {
          System.out.println("Pb when capturing background image");
          return null;
          }
          return background;*/

        //System.out.println("toto1 " +id);
        Image image = frame.getBackgroundImage();
        //System.out.println("toto2 " + id);
        image = createImage(new FilteredImageSource(image.getSource(),new CropImageFilter(posx, posy, width, height)));
        MediaTracker mt = new MediaTracker(this);
        //System.out.println("toto3 " + id);
        mt.addImage(image, 0);
        //System.out.println("toto4 " + id);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) {
        }
        return image;
    }

    public String getHTTPDocument(String path) throws Exception {
        URL url = new URL(path);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(
                url.openStream()));

        StringBuffer bf = new StringBuffer();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            //System.out.println("xml:"+ inputLine);
            bf.append(inputLine+"\n");
        }

        in.close();

        return bf.toString();
    }

    public String getDataToSave() {
        return "";
    }

    public void loadExtraData(NodeList nl) {
    }


    // Mouse management
    public void mousePressed(MouseEvent e) {
        rememberPosition = false;
        if (e.getButton() == MouseEvent.BUTTON3) {
            //System.out.println("popup!");
            openPopupMenu(e.getX(), e.getY());
        }

        if ((e.getButton() == MouseEvent.BUTTON1) && (mainPanel.getNbOfFaces() > 1)) {
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
                            mainPanel.nextFace();
                        } else {
                            mainPanel.previousFace();
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

    protected void loadExtraParam(NodeList nl) {}

    protected abstract String getAboutString();

    protected abstract String getHelpString();

    public  ImageIcon getIcon(String s) {
        URL url = this.getClass().getResource(s);

        if (url != null)  {
            return new ImageIcon(url);
        } else {
            System.out.println("Could not load " + s);
        }

        return null;
    }

    public int getPositiveIntFromString(String s) {
        try {
            return Integer.decode(s).intValue();
        } catch (Exception e) {
            return -1;
        }

    }

    public String replaceAllString(String s, String input, String snew) {
        if (s == null) {
            return s;
        }
        int index;
        while((index = s.indexOf(input)) > -1 ) {
            s = s.substring(0, index) + snew + s.substring(index + input.length());
        }
        return  s;
    }

    protected synchronized int getOS() {
        if (OS == OS_NOT_DETECTED) {
            detectOS();
        }
        return OS;
    }

    protected void detectOS() {
        String str = "";
        DataInputStream dis;
        Process proc;
        String command;

        try {
            // Detect the operating system
            proc = Runtime.getRuntime().exec(DETECT_COMMAND);
            dis = new DataInputStream(proc.getInputStream());
            while ((str = dis.readLine()) != null) {
                analyzeDetectString(str);
            }
        } catch (Exception e) {
            System.out.println("Exception on Memory Load widget thread: " + e.getMessage());
            System.out.println("Aborting MemoryLoad");
        }
    }

    public void analyzeDetectString(String s) {
        String tmps = s.trim();
        if (tmps.startsWith("Linux")) {
            OS = LINUX;
            System.out.println("Linux detected");
            return;
        }

        if (tmps.startsWith("SunOS")) {
            OS = SOLARIS;
            System.out.println("Solaris detected");
            return;
        }

        OS = UNKNOWN_OS;
    }

    public void timerExpired() {
    }

    public void newPeriod() {
    }

    public void newTimer(long time) {
        ttcw = new TimerTaskCrocWidget(this);
        timer.schedule(ttcw, time);
    }

    public void newPeriodicTimer(long time) {
        //System.out.println("Periodic timer");
        ttcw = new TimerTaskCrocWidget(this, true);
        timer.schedule(ttcw, time ,time);
    }

    // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                         image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        if (cm != null) {
            return cm.hasAlpha();
        }

        return false;
    }

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    public BufferedImage getScaledInstance(BufferedImage img,
                                           int targetWidth,
                                           int targetHeight,
                                           Object hint,
                                           boolean higherQuality) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                   BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

    public void imageWorkDone(Image img) {
    }

    public void showDialog(JDialog dialog, int _sizex, int _sizey) {
        dialog.setSize(_sizex, _sizey);
        int locx = frame.getPosx() + posx;
        int locy =  frame.getPosy() + posy;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if ((locx + _sizex) > dim.getWidth()) {
            locx = (int)(dim.getWidth() - _sizex);
        }
        if ((locy + _sizey) > dim.getHeight()) {
            locy = (int)(dim.getHeight() - _sizey);
        }
        dialog.setLocation(locx, locy);
        dialog.setVisible(true);
    }

    public String makeXML(String s) {
        if (s != null) {
            s = Conversion.replaceAllChar(s, '&', "&amp;");
            s = Conversion.replaceAllChar(s, '<', "&lt;");
            s = Conversion.replaceAllChar(s, '>', "&gt;");
            s = Conversion.replaceAllChar(s, '"', "&quot;");
            s = Conversion.replaceAllChar(s, '\'', "&apos;");
        }
        return s;
    }

    public String runOneLineCommand(String command) {
        String str = null;
        DataInputStream dis;
        Process proc;
        String first = null;
        try {
            proc = Runtime.getRuntime().exec(command);
            dis = new DataInputStream(proc.getInputStream());
            while ((str = dis.readLine()) != null) {
                if (first == null) {
                    first = str;
                    //System.out.println("HD first=" + first);
                }
            }
        } catch (Exception e) {
            System.err.println("Error when executing process=" + command);
        }
        return first;
    }

    public long[] getTwoLongFromLine(String line) {
        long []ret = new long[2];
        int index = line.indexOf(' ');

        if (index == -1) {
            return null;
        }

        try {
            String str1 = line.substring(0, index);
            String str2 = line.substring(index+1, line.length());
            //System.out.println("str1=" + str1 + " str2=" + str2);
            ret[0] = Integer.decode(str1).longValue();
            ret[1] = Integer.decode(str2).longValue();
        } catch (Exception e) {
            System.err.println("Error when getting long from string=" + line);
        }

        return ret;

    }




} // End of class CrocWidget
