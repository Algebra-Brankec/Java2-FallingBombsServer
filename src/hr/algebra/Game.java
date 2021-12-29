/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra;

import hr.algebra.model.Bomb;
import hr.algebra.model.Player;
import hr.algebra.model.UDPDataPackage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
    
    private Boolean running = true;
    
    public Game() {
        bombs = new ArrayList<>();
        players = new ArrayList<>();
    }
    
    public void start() {
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
        
        SpawnBombs();
        MoveBombs();
        CheckBombCollision();
        ClearBombsOutsideMap();
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
                
                //System.out.print("index: " + i + ", \r\n");
                //System.out.print("bomb: " + bombs.get(i).getX() + ", \r\n");
                //System.out.print("Map height: " + deleteLine + ", \r\n");
                //System.out.print("Bomb height: " + bombs.get(i).getX() + ", \r\n");
                //System.out.print("Calculated: " + (bombs.get(i).getX() - deleteLine) + ", \r\n\r\n");
            
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
}
