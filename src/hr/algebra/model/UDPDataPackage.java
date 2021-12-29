/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brand
 */
public class UDPDataPackage implements Externalizable {
    private List<Bomb> bombs = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    
    public UDPDataPackage(List<Bomb> bombs, List<Player> players) {
        this.bombs = bombs;
        this.players = players;
    }
    
    public UDPDataPackage() {
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void setBombs(List<Bomb> bombs) {
        this.bombs = bombs;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(bombs);
        out.writeObject(players);
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        bombs = (List<Bomb>) in.readObject();
        players = (List<Player>) in.readObject();
    }
}
