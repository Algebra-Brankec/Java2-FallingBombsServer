/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp;

import hr.algebra.Game;
import hr.algebra.model.Player;
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
import hr.algebra.utilities.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author daniel.bele
 */
public class MulticastServerThread extends Thread {
    
    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final String GROUP = "GROUP";
    private static final Properties PROPERTIES = new Properties();
    
    private UDPDataPackage udpPackage = new UDPDataPackage();
    
    private UnicastServerThread unicastServThread1;
    private UnicastServerThread unicastServThread2;
    
    private List<Player> player = new ArrayList<>();

    public UDPDataPackage getUdpPackage() {
        return udpPackage;
    }

    public void setUdpPackage(UDPDataPackage udpPackage) {
        this.udpPackage = udpPackage;
    }

    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(MulticastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try (DatagramSocket serverSocket = new DatagramSocket()) {
            Game game = new Game();
            
            readPlayerInput();
            
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
                
                //dont send unless there is something on the screen
                if ((udpPackage.getBombs().size() < 1 && udpPackage.getPlayers().size() < 1)) {
                    continue;
                }
                
                sendUDPDataPackage(serverSocket);
            }
        } catch (SocketException ex) {
            Logger.getLogger(MulticastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(MulticastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendUDPDataPackage(DatagramSocket serverSocket) throws IOException {
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
    
    private void readPlayerInput() {
        unicastServThread1 = new UnicastServerThread(12345);
        unicastServThread1.setDaemon(true);
        unicastServThread1.start();
        
        unicastServThread2 = new UnicastServerThread(12346);
        unicastServThread2.setDaemon(true);
        unicastServThread2.start();
    }
}
