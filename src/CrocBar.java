
/**
 * Class CrocBar
 * Main launcher for CrocBar Application. Verifies arguments and then
 * start the main frame.
 * Creation: May, 05, 2009
 * @author Ludovic APVRILLE
 * @see
 */

import java.io.*;
import javax.swing.*;

import myutil.*;
import uicrocbar.*;
import crocwidget.*;

public class CrocBar implements StartCrocBarInterface {
    private static String VERSION = "0.22";

    public String fileName;
    public boolean reloadBackground;

    public CrocBar(String _filename, boolean _reloadBackground) {
        fileName = _filename;
        reloadBackground = _reloadBackground;
    }

    public static void printVersion() {
        System.out.println("\n*** Your CrocBar version is: " + VERSION + "***\n");
    }

    public static boolean checkArguments(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar CrocBar.jar <config.xml> <-remoteReload>");
            return false;
        }
        return true;
    }

    /**
     * @return content of configuration file
     */
    public static String getConfiguration(String fileName) {

        File f = new File(fileName);

        if (!FileUtils.checkFileForOpen(f)) {
            return null;
        }

        String data = FileUtils.loadFileData(f);

        return data;

    }




    public static void main(String[] args) {

        printVersion();

        if (!checkArguments(args)) {
            System.exit(-1);
        }

        boolean remoteReload = false;
        if (args.length > 1) {
            if (args[1].toLowerCase().compareTo("-remotereload") ==0) {
                remoteReload = true;
            }
        }
        CrocBar cb = new CrocBar(args[0], remoteReload);
        cb.startCrocBar();
    }

    public  void startCrocBar() {
        String config = getConfiguration(fileName);

        //System.out.println("Configuration: " + config);

        if (config == null) {
            System.out.println("Error: Could not load configuration file:" + fileName);
            System.out.println("Aborting");
            System.exit(-1);
        }


        JCrocBarFrame jcbf = new JCrocBarFrame(this, config, fileName);
        jcbf.build();
        jcbf.start();

        if (reloadBackground) {
            BackgroundReloader br = new BackgroundReloader(jcbf);
            br.start();
        }

    }


} // End of class CrocBar
