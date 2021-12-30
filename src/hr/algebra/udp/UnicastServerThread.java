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
import java.net.UnknownHostException;
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
    private int SERVER_PORT;
    
    private boolean isActive = false;
    private int playerAction;
    private int oldPlayerMovement = -1;
    
    public UnicastServerThread(int port) {
        SERVER_PORT = port;
    }
    
    @Override
    public void run() {
        try(DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            serverSocket.setReuseAddress(true);
            
            long lastTime = System.nanoTime();
            final double ns = 1000000000.0 / 60.0;
            double delta = 0;
            while(true){
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while(delta >= 1){
                    // first we read the payload length
                    byte[] numberOfUDPDataPackageBytes = new byte[4];
                    DatagramPacket packet = new DatagramPacket(numberOfUDPDataPackageBytes, numberOfUDPDataPackageBytes.length);
                    serverSocket.receive(packet);
                    playerAction = ByteUtils.byteArrayToInt(numberOfUDPDataPackageBytes);

                    if (playerAction != oldPlayerMovement) {
                        System.out.println("player: " + playerAction);
                        oldPlayerMovement = playerAction;
                    }
                    delta--; 
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(UnicastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UnicastServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getPlayerAction(){
        //0 - standing still
        //1 - moving left
        //2 - moving right
        return playerAction;
    }
    
    public void setPlayerAction(int value){
        playerAction = value;
    }

    public boolean isIsActive() {
        return isActive;
    }
}
