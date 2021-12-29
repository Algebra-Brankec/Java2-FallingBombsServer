/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.udp;

import hr.algebra.utilities.ByteUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daniel.bele
 */
public class UnicastServerThread extends Thread {

    private static final String HOST = "localhost";
    private static int SERVER_PORT = 12345;
    
    private boolean isActive = false;
    private int playerMovement;
    
    public UnicastServerThread(int port) {
        SERVER_PORT = port;
    }
    
    @Override
    public void run() {
        try(DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            
            Calendar cal = Calendar.getInstance();
            int now = (int) cal.getTimeInMillis();
            int lastFrame = (int) cal.getTimeInMillis();
            while (true) { 
                //limiting the while loop to 30 times a second
                now = (int) cal.getTimeInMillis();
                int delta = now - lastFrame;
                lastFrame = now;

                if(delta < 33)
                {
                    Thread.sleep(33 - delta);
                }
                
                // first we read the payload length
                byte[] numberOfUDPDataPackageBytes = new byte[4];
                DatagramPacket packet = new DatagramPacket(numberOfUDPDataPackageBytes, numberOfUDPDataPackageBytes.length);
                serverSocket.receive(packet);
                playerMovement = ByteUtils.byteArrayToInt(numberOfUDPDataPackageBytes);
                
                System.out.println("player: " + playerMovement);
            }
            
            
        } catch (SocketException ex) {
            Logger.getLogger(UnicastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UnicastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(UnicastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getPlayerMovement(){
        //0 - standing still
        //1 - moving left
        //2 - moving right
        int playerMov = playerMovement;
        playerMovement = 0;
        
        return playerMov;
    }

    public boolean isIsActive() {
        return isActive;
    }
}
