/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author brand
 */
public class Player implements Serializable {
    private int id;
    private short side;
    
    private short x;
    private short y;
    private short width;
    private short height;
    
    private short maxHealth = 3;
    private short health = 3;
    private boolean stunned = false;
    private short speed = 20;
    
    private boolean isActive = false;

    public Player(int id, String side) {
        this.id     =   id;
        switch(side){
            case "left":
                this.side = 1;
                break;
            case "right":
                this.side = 2;  
                break;
        }
        
        this.width  =   (short)(50);
        this.height =   (short)(90);
        this.x      =   (short)(100 + ((this.side-1) * 600 ));
        this.y      =   (short)(410);
    }
    
    public void takeDamage(int value) {
        if (health - value < 0) {
            die();
            return;
        }
        
        if (health - value >= 0) {
            health -= value;
        }
        
        if (health <= 0) {
            die();
        }
    }
    
    public void getHealth(int value) {
        if (health <= 0) {
            die();
        }
        
        if (health + value > maxHealth) {
            health = maxHealth;
            return;
        }
        
        health += value;
    }
    
    private void die() {
        stunned = true;
        speed = 0;
        health = 0;
    }

    public short getSide() {
        return side;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = (short)id;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = (short)maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = (short)health;
    }

    public boolean isStunned() {
        return stunned;
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = (short)speed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = (short)x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = (short)y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = (short)width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = (short)height;
    }
    
    public int getWeight() {
        return (width + height) / 75;
    }
}
