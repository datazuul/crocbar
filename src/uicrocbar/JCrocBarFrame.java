
/**
 * Class JCrocBarFrame
 * Main frame for CrocBar Application.
 * Creation: May, 05, 2009
 * @author Ludovic APVRILLE
 * @see
 */
package uicrocbar;

import crocwidget.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;


//import java.io.*;

import myutil.*;

public class JCrocBarFrame extends JFrame implements Runnable {
    protected String configuration;
    protected String configuration_without_data;
    protected String fileName;

    // Frame size and position
    protected int posx = 5;
    protected int posy = 5;
    protected int width = 100;
    protected int height = 400;
    protected int sizeOfXBorder = 5;
    protected int sizeOfYBorder = 5;

    // Attributes
    Color bg = Color.black;
    Color fg = Color.red;

    // Table information
    protected int nbOfXCell = 2;
    protected int nbOfYCell = 8;


    // Panel
    JCrocBarPanels panels;

    // Starting class
    StartCrocBarInterface starter;

    // Constructor
    public JCrocBarFrame(StartCrocBarInterface _starter, String _configuration, String _fileName) {
        starter = _starter;
        configuration = _configuration;
        fileName = _fileName;
        //UIManager.put("PopupMenuUI","CustomPopupMenuUI");
        //UIManager.put("MenuItemUI","CustomMenuItemUI");
        setTitle("crocbar");
        setFocusableWindowState(false);
    }

    protected void defaultBuild() {
        panels = new JCrocBarPanels(this);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    protected void endDefaultBuild() {
        panels.setBounds(0, 0, width, height);
        panels.updatePanelOnFace();
        add(panels);
    }

    public void build() {
        defaultBuild();
        CrocBarWidgetLoader loader = new CrocBarWidgetLoader(configuration, this);
        loader.load();
        endDefaultBuild();

    }

    public void start() {
        // No titlebar
        try {
            setUndecorated(true);
        } catch (Exception e) {
            System.err.println("Exception: title bar could not be disabled");
        }

        // Position frame

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (posx < 0) {
            posx = (int)(dim.getWidth() + posx - width);
        }

        if (posy < 0) {
            posy = (int)(dim.getHeight() + posy - height);
        }

        panels.build(width, height);

        setBounds(posx, posy, width, height);

        // Start frame
        setVisible(true);
    }

    public void setPosx(int _posx) {
        posx = _posx;
    }

    public void setPosy(int _posy) {
        posy = _posy;
    }

    public void setWidth(int _width) {
        width = _width;
    }

    public void setHeight(int _height) {
        height = _height;
    }

    public void setBg(int[] colors) {
        if (colors.length < 4) {
            return;
        }

        bg = new Color(colors[0], colors[1], colors[2], colors[3]);
        setUndecorated(true);
        setBackground(bg);
        panels.setBg(bg);
    }

    public void setFg(int[] colors) {
        if (colors.length < 4) {
            return;
        }

        fg = new Color(colors[0], colors[1], colors[2], colors[3]);
        setForeground(fg);
        panels.setFg(fg);
    }

    public Color getBackgroundColor() {
        return bg;
    }

    public Color getForegroundColor() {
        return fg;
    }

    public void addWidget(CrocWidget cw, boolean [] faces) {
        panels.addWidget(cw, faces);
    }

    public void setNbOfXCell(int _nbOfXCell) {
        if (_nbOfXCell > 0) {
            nbOfXCell = _nbOfXCell;
        }
    }

    public void setNbOfYCell(int _nbOfYCell) {
        if (_nbOfYCell > 0) {
            nbOfYCell = _nbOfYCell;
        }
    }

    public JCrocBarPanels getJCrocBarPanels() {
        return panels;
    }

    public int getPosXFrom(int _poscellx) {
        int tmp = width - 2*sizeOfXBorder;
        int cellSizex = tmp / nbOfXCell;
        return sizeOfXBorder + _poscellx * cellSizex;
    }

    public int getPosYFrom(int _poscelly) {
        int tmp = height - 2*sizeOfXBorder;
        int cellSizey = tmp / nbOfYCell;
        return sizeOfYBorder + _poscelly * cellSizey;
    }


    public int getWidthFrom(int _nbcellx) {
        int tmp = width - 2*sizeOfXBorder;
        int cellSizex = tmp / nbOfXCell;
        return _nbcellx * cellSizex;
    }

    public int getHeightFrom(int _nbcelly) {
        int tmp = height - 2*sizeOfYBorder;
        int cellSizey = tmp / nbOfYCell;
        return _nbcelly * cellSizey;
    }

    public void ring() {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        //System.out.println("Ring");
        int cpt = 0;
        int dec;
        while(cpt < 50) {
            cpt ++;
            if ((cpt%2) == 0) {
                dec = -5;
            } else {
                dec = 5;
            }
            //System.out.println("Move");
            setBounds(posx+dec, posy, width, height);
            repaint();

            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException ie) {
                //System.out.println("Interrupted");
            }
        }

        setBounds(posx, posy, width, height);
        repaint();

    }

    public int getPosx() {
        return posx;
    }

    public int getPosy() {
        return posy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getBackgroundImage() {
        return panels.getBackgroundImage();
    }

    /*public Color getBackgroundColor() {
      return panels.getBackground();
      }*/

    public boolean saveData() {
        if (configuration_without_data == null) {
            int index0, index1;
            index0 = configuration.indexOf("<CrocBarData>");
            index1 = configuration.indexOf("</CrocBarData>");
            if ((index0 == -1) && (index1 == -1)) {
                configuration_without_data = configuration;
            } else {
                configuration_without_data = configuration.substring(0, index0);
                configuration_without_data = configuration_without_data + configuration.substring(index1+14, configuration.length());
            }
            index0 = configuration_without_data.indexOf("</CrocBarConfiguration>");
            if (index0 != -1) {
                configuration_without_data = configuration_without_data.substring(0, index0);
            }

        }

        //System.out.println("Configuration without data = " + configuration_without_data);

        StringBuffer sb = new StringBuffer();
        sb.append(configuration_without_data);
        sb.append("<CrocBarData>\n");

        String s;
        for(CrocWidget cw: panels.getList()) {
            s = cw.getDataToSave();
            //System.out.println("Appending: " + s);
            sb.append(s);
        }
        sb.append("</CrocBarData>\n\n");
        sb.append("</CrocBarConfiguration>");

        File f = new File(fileName);

        try {
            if (!FileUtils.checkFileForSave(f)) {
                return false;
            }

            FileUtils.saveFile(fileName, sb.toString());
        } catch (FileException fe) {
            System.out.println("Exception when saving data in file " + fileName + ": " + fe.getMessage());
            return false;
        }
        System.out.println("Your configuration file " + fileName + " has been updated");
        return true;
    }

    public void repaintAll() {
        panels.repaintAll();
    }

    public void reload() {
        setVisible(false);
        starter.startCrocBar();
        dispose();
    }


    public void reloadBackground() {
        if (panels != null) {
            panels.reloadBackgroundImage();
        }
    }









} // End of class JCrocBarFrame
