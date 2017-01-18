
/**
 * Class ReloadRemoteOrder
 * Sending remote order for background reload
 * Creation: May, 06, 2011
 * @author Ludovic APVRILLE
 * @see
 */

import java.io.*;
import java.net.*;


public class ReloadRemoteOrder  {

    public static final int DEFAULT_PORT = 9362;

    public static void main(String[] args) throws Exception{
        byte[] ipAddr = new byte[]{127, 0, 0, 1};
        InetAddress addr = InetAddress.getByAddress(ipAddr);
        String sendData = "reload background";
        byte [] buf = sendData.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, addr, ReloadRemoteOrder.DEFAULT_PORT);
        DatagramSocket serverSocket = new DatagramSocket(9876);
        serverSocket.send(sendPacket);
    }




} // End of class ReloadRemoteOrder
