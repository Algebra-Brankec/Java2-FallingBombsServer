/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

/**
 *
 * @author brand
 */
public enum PlayerMovement {
    STILL, LEFT, RIGHT;
    
    public static PlayerMovement fromInteger(int x) {
        switch(x) {
        case 0:
            return STILL;
        case 1:
            return LEFT;
        case 2:
            return RIGHT;
        }
        return null;
    }
}
