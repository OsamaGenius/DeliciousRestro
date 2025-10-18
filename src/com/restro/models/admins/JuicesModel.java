/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.restro.models.admins;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author u_s_e
 */
public class JuicesModel extends RecursiveTreeObject<JuicesModel> {
   
    // Variables to hold database data
    public final SimpleStringProperty id, image, name, type, price, description, created_at, updated_at;
    
    // Initialize and fetch data from Database 
    public JuicesModel(String id, String image, String name, String type, String price, String description, String created_at, String updated_at) {
        this.id = new SimpleStringProperty(id);
        this.image = new SimpleStringProperty(image);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.price = new SimpleStringProperty(price);
        this.description = new SimpleStringProperty(description);
        this.created_at = new SimpleStringProperty(created_at);
        this.updated_at = new SimpleStringProperty(updated_at);
    }
    
    // Getting juice id from database
    public String getID() {
        return id.get();
    }
    
    // Getting juice image from database
    public String getImage() {
        return image.get();
    }
    
    // Getting juice name from database
    public String getName() {
        return name.get();
    }
    
    // Getting juice type from database
    public String getType() {
        return type.get();
    }
    
    // Getting juice price from database
    public String getPrice() {
        return price.get();
    }
    
    // Getting juice description from database
    public String getDescription() {
        return description.get();
    }
    
    // Getting juice created time from database
    public String getCreated() {
        return created_at.get();
    }
    
    // Getting juice updated time from database
    public String getUpdated() {
        return updated_at.get();
    }
    
}
