/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.repository;

import hr.algebra.model.UDPDataPackage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author dnlbe
 */
public class Repository implements Serializable {
    
    private static final long serialVersionUID = 3L;
    
    // eager singleton    
    private Repository() {        
    }
    
    private static final Repository INSTANCE = new Repository();

    public static Repository getInstance() {
        return INSTANCE;
    }
    
    private UDPDataPackage udpDataPackage = new UDPDataPackage();

    // this is not a good practice, this is only to show observable pattern
    public UDPDataPackage getDrivers() {
        return udpDataPackage;
    }

    
    public void setUDPDataPackage(UDPDataPackage udpDataPackage) {
        this.udpDataPackage = udpDataPackage;
    }

    
    // observable lists are not Serializable - we must do it manually
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(udpDataPackage);
    }

    // we must imitate constructor, so we create lists manually
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        UDPDataPackage serializedUDPDataPackages = (UDPDataPackage) ois.readObject();
        INSTANCE.udpDataPackage = serializedUDPDataPackages;   
    }
       
    // Repository must be a TRUE Singleton so we must secure 
    // that the instance created by deserialization is discarded    
    private Object readResolve() {
        // Return the one true Repository and let the garbage collector
        // take care of the Repository impersonator.
        return INSTANCE;
    }
    
}
