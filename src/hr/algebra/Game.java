/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra;

import hr.algebra.model.Bomb;
import hr.algebra.model.Player;
import hr.algebra.model.UDPDataPackage;
import hr.algebra.udp.MulticastServerThread;
import hr.algebra.udp.UnicastServerThread;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author brand
 */
public class Game {
    private List<Bomb> bombs;
    private Random rand = new Random();
    private int bombFrequency = 100;
    private double deleteLine = 600;
    private List<Player> players;
    private boolean running = true;
    
    MulticastServerThread t1;
    
    private UnicastServerThread unicastServThread1;
    private UnicastServerThread unicastServThread2;
    
    public Game() {
        bombs = new ArrayList<>();
        players = new ArrayList<>();
        players.add(new Player(34530, "left"));
        players.add(new Player(34531, "right"));
    }
    
    public void start() {
        startUDCSockets();
        gameLoop();
    }
    
    private void gameLoop() {
        //if (players.size() != 2) {
        //    return;
        //}
        //
        //if (!(players.get(0).getHealth() > 0 || players.get(1).getHealth() > 0)) {
        //    running = false;
        //    return;
        //}
        
        Thread gameThread = new Thread(() -> {
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
                    try {
                        Thread.sleep(33 - delta);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try {
                    if(!running){
                        return;
                    }
                    
                    if (!(players.get(0).getHealth() > 0 || players.get(1).getHealth() > 0)) {
                        running = false;
                        return;
                    }
                    SpawnBombs();
                    MoveBombs();
                    CheckBombCollision();
                    ClearBombsOutsideMap(); 
                    loadPlayerClientMovement();
                } catch (Exception e){
                    
                }
                
                t1.setUdpPackage(getUDPDataPackage());
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
            Bomb bomb = new Bomb(600);
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
        //for (BtnBomb bomb : bombs) {
        //    
        //    //if bombs hit the player then take damage and then mark the bomb for delete
        //    if (isIntersect(player, bomb) && !bomb.disableProperty().get()) {
        //        player.takeDamage(bomb.damage);
        //        bomb.setDisable(true);
        //    }
        //}
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
    
    private boolean isIntersect(Button a, Button b) {
        double aMinX = a.layoutXProperty().get();
        double aMaxX = a.layoutXProperty().get() + a.widthProperty().get();
        double aMinY = a.layoutYProperty().get();
        double aMaxY = a.layoutYProperty().get() + a.heightProperty().get();
        
        double bMinX = b.layoutXProperty().get();
        double bMaxX = b.layoutXProperty().get() + b.widthProperty().get();
        double bMinY = b.layoutYProperty().get();
        double bMaxY = b.layoutYProperty().get() + b.heightProperty().get();
        
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
    
    private void loadPlayerClientMovement() {
        if(unicastServThread1.getPlayerMovement() == 1)
            players.get(0).setX(players.get(0).getX() - players.get(0).getSpeed());
        if(unicastServThread1.getPlayerMovement() == 2)
            players.get(0).setX(players.get(0).getX() + players.get(0).getSpeed());
    }
}
