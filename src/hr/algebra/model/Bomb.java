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
public class Bomb implements Serializable {
    private short x;
    private short y;
    private short width;
    private short height;
    
    private Boolean active;

    public Bomb(int windowWidth) {
        Random rand =   new Random();
        this.width  =   (short)(rand.nextInt(100 - 20) + 20);
        this.height =   (short)(rand.nextInt(200 - 40) + 40);
        this.x      =   (short)(rand.nextInt(windowWidth));
        this.y      =   (short)(rand.nextInt(1));
    }
    
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        return (width + height) / 37;
    }
}
