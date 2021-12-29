/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra;

import hr.algebra.model.UDPDataPackage;
import hr.algebra.utilities.ByteUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ObjectUtil;

/**
 *
 * @author daniel.bele
 */
public class ServerThread extends Thread {
    
    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final String GROUP = "GROUP";
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket()) {
            UDPDataPackage udpPackage = new UDPDataPackage();
            
            Game game = new Game();
            
            Calendar cal = Calendar.getInstance();
            int now = (int) cal.getTimeInMillis();
            int lastFrame = (int) cal.getTimeInMillis();
            
            while(true)
            {
                //limiting the while loop to 30 times a second
                now = (int) cal.getTimeInMillis();
                int delta = now - lastFrame;
                lastFrame = now;

                if(delta < 33)
                {
                    Thread.sleep(33 - delta);
                }
                
                game.start();
                
                udpPackage = game.getUDPDataPackage();
                
                if ((udpPackage.getBombs().size() < 1 && udpPackage.getPlayers().size() < 1)) {
                    continue;
                }
                
                byte[] buffer = ObjectUtil.objectToByteArray(udpPackage);
                
                // before sending byte[], write byte[].length into payload
                byte[] numberOfBufferBytes = ByteUtils.intToByteArray(buffer.length);
                InetAddress groupAddress = InetAddress.getByName(PROPERTIES.getProperty(GROUP));
                DatagramPacket packet = new DatagramPacket(
                        numberOfBufferBytes,
                        numberOfBufferBytes.length,
                        groupAddress, Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT))
                );
                serverSocket.send(packet);
                
                // now send the payload
                // payload must be at most 64KB!
                packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        groupAddress, Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)));
                serverSocket.send(packet);
            }
        } catch (SocketException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
