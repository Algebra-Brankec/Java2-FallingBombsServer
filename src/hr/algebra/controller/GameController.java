/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.controller;

import hr.algebra.model.Bomb;
import hr.algebra.model.Player;
import hr.algebra.model.UDPDataPackage;
import hr.algebra.udp.MulticastServerThread;
import hr.algebra.udp.UnicastServerThread;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author brand
 */
public class GameController implements Initializable {

    private List<Bomb> bombs;
    private Random rand = new Random();
    private int bombFrequency = 100;
    private double deleteLine = 600;
    private List<Player> players;
    
    private boolean running = true;
    
    MulticastServerThread t1;
    
    private UnicastServerThread unicastServThread1;
    private UnicastServerThread unicastServThread2;
    
    @FXML
    private AnchorPane apLevel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bombs = new ArrayList<>();
        players = new ArrayList<>();
        players.add(new Player(34530, "left"));
        players.add(new Player(34531, "right"));
        
        start();
    }    

    @FXML
    private void keyPressed(KeyEvent event) {
    }

    @FXML
    private void keyReleased(KeyEvent event) {
    }
    
    public void start() {
        startUDCSockets();
        gameLoop();
    }
    
    private void gameLoop() {
        Thread gameThread = new Thread(() -> {
            
            long lastTime = System.nanoTime();
            final double ns = 1000000000.0 / 30.0;
            double delta = 0;
            while(true){
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while(delta >= 1){
                    try {
                        if(!running){
                            resumeGame();
                            delta--;
                            continue;
                        }

                        if (players.get(0).getHealth() < 1 || players.get(1).getHealth() < 1) {
                            running = false;
                            delta--;
                            continue;
                        }
                        SpawnBombs();
                        MoveBombs();
                        CheckBombCollision();
                        ClearBombsOutsideMap(); 
                        loadPlayerClientActions();
                    } catch (Exception e){

                    }

                    t1.setUdpPackage(getUDPDataPackage());
                    delta--;
                }
            }
        });  
        gameThread.setDaemon(true);
        gameThread.start();
    }
    
    public UDPDataPackage getUDPDataPackage() {
        return new UDPDataPackage(bombs, players);
    }
    
    private void SpawnBombs() {

        int randInt1 = rand.nextInt(bombFrequency);
        int randInt2 = rand.nextInt(bombFrequency);

        if (randInt1 == randInt2) {
            Bomb bomb = new Bomb(800);
            bombs.add(bomb);
            
            if (bombFrequency > 20) {
                bombFrequency -= 1;
            }
        }
    }
    
    private void MoveBombs() {
        for (Bomb bomb : bombs) {
            bomb.setY(bomb.getY() + bomb.getWeight());
        }
    }
    
    private void CheckBombCollision()
    {
        for (Bomb bomb : bombs) {
            //if bombs hit the player then take damage and then mark the bomb for delete
            
            for (Player player : players) {
                if (isIntersect(player, bomb) && bomb.getActive()) {
                    player.takeDamage(bomb.getDamage());
                    bomb.setActive(false);
                }
            }
        }
    }
    
    private void ClearBombsOutsideMap() {
        List<Integer> bombsToClear = new ArrayList<>();
        
        for (int i = 0; i < bombs.size(); i++) {
            if (bombs.get(i).getY() - deleteLine - bombs.get(i).getHeight() > 0) {
                bombsToClear.add(i);
                bombs.get(i).setActive(false);
            }
        }
        
        for (int index : bombsToClear) {
            bombs.remove(index);
        }
        
        bombsToClear.clear();
    }
    
    private boolean isIntersect(Player a, Bomb b) {
        double aMinX = a.getX();
        double aMaxX = a.getX() + a.getWidth();
        double aMinY = a.getY();
        double aMaxY = a.getY() + a.getHeight();
        
        double bMinX = b.getX();
        double bMaxX = b.getX() + b.getWidth();
        double bMinY = b.getY();
        double bMaxY = b.getY() + b.getHeight();
        
        return (aMinX <= bMaxX && aMaxX >= bMinX) &&
               (aMinY <= bMaxY && aMaxY >= bMinY);
    }
    
    private void startUDCSockets() {
        t1 = new MulticastServerThread();
        t1.setDaemon(true);
        t1.start();
        
        unicastServThread1 = new UnicastServerThread(players.get(0).getId());
        unicastServThread1.setDaemon(true);
        unicastServThread1.start();
        
        unicastServThread2 = new UnicastServerThread(players.get(1).getId());
        unicastServThread2.setDaemon(true);
        unicastServThread2.start();
    }
    
    private void loadPlayerClientActions() {
        switch(unicastServThread1.getPlayerAction()) {
            case 1:
                players.get(0).setX(players.get(0).getX() - players.get(0).getSpeed());
                break;
            case 2:
                players.get(0).setX(players.get(0).getX() + players.get(0).getSpeed());
                break;
            case 101:
                running = false;
                break;
        }
        
        switch(unicastServThread2.getPlayerAction()) {
            case 1:
                players.get(1).setX(players.get(1).getX() - players.get(1).getSpeed());
                break;
            case 2:
                players.get(1).setX(players.get(1).getX() + players.get(1).getSpeed());
                break;
            case 101:
                running = false;
                break;
        }
    }
    
    private void resumeGame() {
        switch(unicastServThread1.getPlayerAction()) {
            case 100:
                running = true;
                break;
        }
        switch(unicastServThread2.getPlayerAction()) {
            case 100:
                running = true;
                break;
        }
    }
    
}
