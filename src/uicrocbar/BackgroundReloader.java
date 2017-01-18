
/**
* Class BackgroundReloader
* Handling remote orders for reloading background
* Creation: Dec, 06, 2011
* @author Ludovic APVRILLE
* @see
*/

package uicrocbar;

import java.io.*;
import java.net.*;



//import java.io.*;

//import myutil.*;

public class BackgroundReloader extends Thread {

    public final int DEFAULT_PORT = 9362;

    protected JCrocBarFrame jcbf;
    protected DatagramSocket ds;
    protected boolean go;


    // Constructor
    public BackgroundReloader(JCrocBarFrame _jcbf) {
        super();
        jcbf = _jcbf;
        go = true;
    }

    public void run() {
        DatagramPacket dp;
        byte[] receivedData = new byte[1024];

        System.out.println("LISTENING FOR RELOAD");
        try {
            ds = new DatagramSocket(DEFAULT_PORT);
            dp = new DatagramPacket(receivedData, 1024);
            while(go == true) {
                System.out.println("Waiting for datagram");
                ds.receive(dp);
                String s = new String(dp.getData());
                System.out.println("Received: " + s);
                if (s.startsWith("reload background")) {
                    System.out.println("Reloading background");
                    jcbf.reloadBackground();
                }
            }
        } catch (Exception e) {
            System.out.println("Error on Reloading background thread: " + e.getMessage() + " -> aborting thread");
            go = false;
        }
        if (ds != null) {
            ds.close();
        }
        System.out.println("Waiting for datagram: TERMINATED");
    }

    public void stopMe() {
        go = false;
        if (ds != null) {
            ds.close();
        }
    }





} // End of class BackgroundReloader

