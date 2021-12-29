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
    private short x;
    private short y;
    private short width;
    private short height;
    
    private int maxHealth = 3;
    private int health = 3;
    private boolean stunned = false;
    private int speed = 20;

    public Player() {
        this.width  =   (short)(50);
        this.height =   (short)(90);
        this.x      =   (short)(100);
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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
        this.speed = speed;
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
