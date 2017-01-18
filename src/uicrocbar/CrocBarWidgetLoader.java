
/**
 * Class CrocBarWidgetLoader
 * Loader for xml configurations file.
 * Creation: May, 05, 2009
 * @author Ludovic APVRILLE
 * @see
 */

package uicrocbar;

import crocwidget.*;

import java.awt.*;
import javax.swing.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import java.io.*;

//import myutil.*;

public class CrocBarWidgetLoader {
    private static String CROCBAR_HEADER = "CrocBarConfiguration";
    private static String CROCBAR_SIZE_ATTRIBUTES = "CrocBarSizeAttributes";
    private static String CROCBAR_COLOR_ATTRIBUTES = "CrocBarColorAttributes";
    private static String CROCBAR_TABLE_ATTRIBUTES = "CrocBarTableAttributes";
    private static String CROCBAR_FACE_ATTRIBUTES = "CrocBarFaceAttributes";
    private static String CROC_WIDGET = "CrocWidget";
    private static String CROCBAR_DATA = "CrocBarData";

    private static String CROC_WIDGET_TIME_AND_DATE = "TIMEANDDATE";
    private static String CROC_WIDGET_SINGLE_PICTURE = "SINGLEPICTURE";
    private static String CROC_WIDGET_RANDOM_PICTURE_WHEN_CLICK = "RANDOMPICTUREWHENCLICK";
    private static String CROC_WIDGET_CPU_LOAD = "CPULOAD";
    private static String CROC_WIDGET_MEMORY_LOAD = "MEMORYLOAD";
    private static String CROC_WIDGET_MULTIPLE_PICTURES = "MULTIPLEPICTURES";
    private static String CROC_WIDGET_MAIN_PROCESSES = "MAINPROCESSES";
    private static String CROC_WIDGET_EXECUTOR = "EXECUTOR";
    private static String CROC_WIDGET_VOLUME_CTRL = "VOLUMECTRL";
    private static String CROC_WIDGET_METEO_ROQUEFORT = "METEOROQUEFORT";
    private static String CROC_WIDGET_RETRIEVE_PICTURE_AND_DISPLAY = "RETRIEVEPICTUREANDDISPLAY";
    private static String CROC_WIDGET_CURVE_METEO_ROQUEFORT = "CURVEMETEOROQUEFORT";
    private static String CROC_WIDGET_SAT24 = "SAT24";
    private static String CROC_WIDGET_STRAWBERRY = "STRAWBERRY";
    private static String CROC_WIDGET_ACTIONICON = "ACTIONICON";
    private static String CROC_WIDGET_SHOW_RSS_UPDATE = "SHOWRSSUPDATE";
    private static String CROC_WIDGET_TO_DO = "TODO";
    private static String CROC_WIDGET_HD_SIZE = "HDSIZE";
    private static String CROC_WIDGET_XBIFF = "XBIFF";
    private static String CROC_WIDGET_SHORTCUT = "SHORTCUT";
    private static String CROC_MAIL_ALERT = "MAILALERT";


    private String configuration;
    private JCrocBarFrame frame;

    private int toDoID = 0;

    public CrocBarWidgetLoader(String _configuration, JCrocBarFrame _frame) {
        configuration = _configuration;
        frame = _frame;
    }


    public boolean load() {
        System.out.println("Loading configuration");

        DocumentBuilderFactory dbf;
        DocumentBuilder db;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            dbf = null;
            db = null;
        }

        if ((dbf == null) || (db == null)) {
            return false;
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(decodeString(configuration).getBytes());
        int i;

        try {
            // building nodes from xml String
            Document doc = db.parse(bais);
            NodeList nl;
            Node node;

            nl = doc.getElementsByTagName(CROCBAR_HEADER);

            if (nl == null) {
                return false;
            }

            for(i=0; i<nl.getLength(); i++) {
                node = nl.item(i);
                //System.out.println("Node = " + dnd);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    // create design, and get an index for it
                    return loadConfiguration(node);
                }
            }

        } catch (IOException e) {
            System.err.println("Error when loading configuration file:" + e.getMessage());
            return false;
        } catch (SAXException saxe) {
            System.err.println("Error when loading configuration file:" + saxe.getMessage());
            return false;
        }
        return true;

    }

    private boolean loadConfiguration(Node node1) {
        NodeList diagramNl = node1.getChildNodes();
        Element elt;
        Node node;
        NodeList listData = null;

        String tmp;
        int val;
        int j;
        int nbOfFaces = 1; // default value;

        int[] colors;

        try {
            //Searching for CrocBarData
            for(j=0; j<diagramNl.getLength(); j++) {
                node = diagramNl.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    elt = (Element)node;

                    // Data
                    if (elt.getTagName().compareTo(CROCBAR_DATA) == 0) {
                        System.out.println("Data found");
                        listData = elt.getElementsByTagName("WidgetData");
                    }
                }
            }

            for(j=0; j<diagramNl.getLength(); j++) {
                //System.out.println("Ndes: " + j);
                node = diagramNl.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    elt = (Element)node;

                    // Main window size attributes
                    if (elt.getTagName().compareTo(CROCBAR_SIZE_ATTRIBUTES) == 0) {
                        tmp = elt.getAttribute("x");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            frame.setPosx(val);
                        }
                        tmp = elt.getAttribute("y");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            frame.setPosy(val);
                        }
                        tmp = elt.getAttribute("width");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            frame.setWidth(val);
                        }
                        tmp = elt.getAttribute("height");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            frame.setHeight(val);
                        }
                    }

                    // Color attributes
                    if (elt.getTagName().compareTo(CROCBAR_COLOR_ATTRIBUTES) == 0) {
                        tmp = elt.getAttribute("bg");
                        //System.out.println("Is a bg color=" + tmp);
                        if (isAColor(tmp)) {
                            //System.out.println("yes!" + tmp);
                            colors = getColors(tmp);
                            frame.setBg(colors);
                        }
                        tmp = elt.getAttribute("fg");
                        //System.out.println("Is a fg color=" + tmp);
                        if (isAColor(tmp)) {
                            //System.out.println("yes!" + tmp);
                            colors = getColors(tmp);
                            frame.setFg(colors);
                        }
                    }

                    // Table attributes
                    if (elt.getTagName().compareTo(CROCBAR_TABLE_ATTRIBUTES) == 0) {
                        tmp = elt.getAttribute("cellx");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            frame.setNbOfXCell(val);
                        }
                        tmp = elt.getAttribute("celly");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            frame.setNbOfYCell(val);
                        }
                    }

                    // Face attributes
                    if (elt.getTagName().compareTo(CROCBAR_FACE_ATTRIBUTES) == 0) {
                        tmp = elt.getAttribute("nb_of_face");
                        if (isAnInt(tmp)) {
                            val = getInt(tmp);
                            if (val > 1) {
                                frame.getJCrocBarPanels().makeFaces(val);
                                nbOfFaces = val;
                                System.out.println("Nb of faces = " + nbOfFaces);
                            }
                        }
                    }

                    // Widget
                    if (elt.getTagName().compareTo(CROC_WIDGET) == 0) {
                        tmp = elt.getAttribute("type");
                        if ((tmp != null) && (tmp.length() > 0)) {
                            int poscellx = 0, poscelly=0, nbcellx=1, nbcelly=1;
                            int borderX = 0, borderY = 0;
                            int x, y ,width, height;
                            int jj;
                            String bgs, fgs, type = null;
                            String face;
                            boolean [] faces;
                            Color bgtmp = frame.getBackgroundColor(), fgtmp = frame.getForegroundColor();

                            type = elt.getAttribute("type");

                            if ((type != null) && (type.trim().length() > 0)) {

                                // default: on no face
                                faces = new boolean[nbOfFaces];
                                for(jj=0; jj<nbOfFaces; jj++) {
                                    faces[jj] = false;
                                }

                                tmp = elt.getAttribute("face");
                                if (tmp != null) {
                                    fillFaceWith(faces, tmp);
                                } else {
                                    setOnAllFaces(faces);
                                }

                                tmp = elt.getAttribute("poscellx");
                                if (isAnInt(tmp)) {
                                    poscellx = getInt(tmp);
                                }
                                tmp = elt.getAttribute("poscelly");
                                if (isAnInt(tmp)) {
                                    poscelly = getInt(tmp);
                                }
                                tmp = elt.getAttribute("nbcellx");
                                if (isAnInt(tmp)) {
                                    nbcellx = getInt(tmp);
                                }
                                tmp = elt.getAttribute("nbcelly");
                                if (isAnInt(tmp)) {
                                    nbcelly = getInt(tmp);
                                }
                                tmp = elt.getAttribute("borderX");
                                if (isAnInt(tmp)) {
                                    borderX = getInt(tmp);
                                }
                                tmp = elt.getAttribute("borderY");
                                if (isAnInt(tmp)) {
                                    borderY = getInt(tmp);
                                }
                                tmp = elt.getAttribute("bg");
                                //System.out.println("Is a bg color=" + tmp);
                                if (isAColor(tmp)) {
                                    //System.out.println("yes!" + tmp);
                                    colors = getColors(tmp);
                                    bgtmp = new Color(colors[0], colors[1], colors[2], colors[3]);

                                }
                                tmp = elt.getAttribute("fg");
                                //System.out.println("Is a fg color=" + tmp);
                                if (isAColor(tmp)) {
                                    //System.out.println("yes!" + tmp);
                                    colors = getColors(tmp);
                                    fgtmp = new Color(colors[0], colors[1], colors[2], colors[3]);
                                }

                                //System.out.println("BorderX=" + borderX);

                                x = frame.getPosXFrom(poscellx) + borderX;
                                y = frame.getPosYFrom(poscelly) + borderY;
                                width = frame.getWidthFrom(nbcellx) - (2 * borderX);
                                height = frame.getHeightFrom(nbcelly) - (2 * borderY);

                                System.out.println("type=" + type);

                                if (type.toUpperCase().equals(CROC_WIDGET_TIME_AND_DATE)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    TimeAndDateCrocWidget tadcw = new TimeAndDateCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(tadcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_SINGLE_PICTURE)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    SinglePictureCrocWidget spcw = new SinglePictureCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(spcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_RANDOM_PICTURE_WHEN_CLICK)) {
                                    System.out.println("Creating random picture when click widget");
                                    RandomPictureWhenClickCrocWidget rpwhcw = new RandomPictureWhenClickCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(rpwhcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_MULTIPLE_PICTURES)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    MultiplePicturesCrocWidget mpiccw = new MultiplePicturesCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(mpiccw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_MAIN_PROCESSES)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    MainProcessesCrocWidget mpcw = new MainProcessesCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(mpcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_EXECUTOR)) {
                                    //System.out.println("Creating Executor croc widget");
                                    ExecutorCrocWidget mpcw = new ExecutorCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(mpcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_VOLUME_CTRL)) {
                                    //System.out.println("Creating Executor croc widget");
                                    VolumeCtrlCrocWidget mpcw = new VolumeCtrlCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(mpcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_CPU_LOAD)) {
                                    //System.out.println("Creating CPU Load croc widget");
                                    CPULoadCrocWidget cpulcw = new CPULoadCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(cpulcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_MEMORY_LOAD)) {
                                    //System.out.println("Creating CPU Load croc widget");
                                    MemoryLoadCrocWidget memlcw = new MemoryLoadCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(memlcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_METEO_ROQUEFORT)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    MeteoRoquefortCrocWidget mrcw = new MeteoRoquefortCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(mrcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_RETRIEVE_PICTURE_AND_DISPLAY)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    RetrievePictureAndDisplayCrocWidget rplcw = new RetrievePictureAndDisplayCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(rplcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_CURVE_METEO_ROQUEFORT)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    CurveMeteoRoquefortCrocWidget cmrcw = new CurveMeteoRoquefortCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(cmrcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_SAT24)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    Sat24CrocWidget s24cw = new Sat24CrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(s24cw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_STRAWBERRY)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    StrawberryCrocWidget strawcw = new StrawberryCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(strawcw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_ACTIONICON)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    ActionIconCrocWidget aicw = new ActionIconCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(aicw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_SHOW_RSS_UPDATE)) {
                                    //System.out.println("Creating Time and date croc widget");
                                    ShowRSSUpdateCrocWidget srssucw = new ShowRSSUpdateCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(srssucw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_TO_DO)) {
                                    //System.out.println("Creating To do");
                                    ToDoCrocWidget todocw = new ToDoCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces, toDoID);
                                    frame.addWidget(todocw, faces);
                                    toDoID++;
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_HD_SIZE)) {
                                    //System.out.println("Creating To do");
                                    HDSizeCrocWidget hdscw = new HDSizeCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(hdscw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_XBIFF)) {
                                    //System.out.println("******** Creating XBIFF ***********");
                                    XbiffCrocWidget xbiff = new XbiffCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(xbiff, faces);
                                }

                                if (type.toUpperCase().equals(CROC_WIDGET_SHORTCUT)) {
                                    System.out.println("******** Creating SHORTCUT ***********");
                                    ShortcutCrocWidget scw = new ShortcutCrocWidget(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(scw, faces);
                                }

                                if (type.toUpperCase().equals(CROC_MAIL_ALERT)) {
                                    //System.out.println("******** Creating MAIL ALERT ***********");
                                    MailAlert ma = new MailAlert(frame, frame.getJCrocBarPanels(), x, y, width, height, bgtmp, fgtmp, elt.getElementsByTagName("ExtraParams"), listData, faces);
                                    frame.addWidget(ma, faces);
                                }

                            } else {
                                System.err.println("Unknown type of widget: " + type + ". Skipping");
                                System.err.println("Stack:" + Thread.currentThread().getStackTrace());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Pico a une SUPER TOP grande gueule\n");
            System.err.println("Exception in CrocBarWidget " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public boolean isAnInt(String s) {
        if (s == null) {
            return false;
        }

        try {
            Integer integer = Integer.decode(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    public int getInt(String s) {
        if (s == null) {
            return 0;
        }

        try {
            Integer integer = Integer.decode(s);
            return integer.intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void fillFaceWith(boolean [] _faces, String _s) {
        //System.out.println("Analyzing faces: " + _s);
        String tmp = _s.trim().toUpperCase();
        if (tmp.equals("ALL") || (tmp.length() == 0)) {
            setOnAllFaces(_faces);
            return;
        }

        String tmps[] = tmp.split(";");
        int index;
        boolean faceFound = false;
        for (int i=0; i<tmps.length; i++) {
            index = getInt(tmps[i]);
            if ((index != -1) && (index < _faces.length)) {
                _faces[index] = true;
                faceFound = true;
                //System.out.println("On face: " + index);
            } else {
                System.out.println("Face error: " + tmps[i]);
            }
        }

        if (!faceFound) {
            setOnAllFaces(_faces);
        }
    }

    public void setOnAllFaces(boolean [] _faces) {
        //System.out.println("On all faces");
        for(int j=0; j<_faces.length; j++) {
            _faces[j] = true;
        }
    }

    public static boolean isAColor(String s) {
        //System.out.println("toto1");
        if (s == null) {
            return false;
        }

        //System.out.println("toto2");

        if (s.length() == 0) {
            return false;
        }

        //System.out.println("toto3");

        String[] colors = s.split(",");

        if ((colors.length <3) || (colors.length >4)) {
            return false;
        }

        //System.out.println("toto4");

        String tmp;
        for(int i=0; i<colors.length; i++) {
            try {
                tmp = colors[i].trim();
                Integer integer = Integer.decode(tmp);
            } catch (NumberFormatException e) {
                //System.out.println("toto5: " + tmp);
                return false;
            }
        }
        return true;
    }

    // s must be provided in rgb format as follows: r, g, b
    public static Color getColor(String s) {
        int [] colors = getColors(s);
        return new Color(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int[] getColors(String s) {
        if (!isAColor(s)) {
            return null;
        }

        String[] colors = s.split(",");
        int i;

        for(i=0; i<colors.length; i++) {
            colors[i] = colors[i].trim();
        }

        int[] intcolors = new int[4];
        intcolors[3] = 255;
        try {
            for(i=0; i<colors.length; i++) {
                intcolors[i] = Integer.decode(colors[i]).intValue();
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return intcolors;
    }

    public static String decodeString(String s)  {
        if (s == null)
            return s;
        byte b[] = null;
        try {
            b = s.getBytes("ISO-8859-1");
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }



} // End of class CrocBarWidgetLoader
