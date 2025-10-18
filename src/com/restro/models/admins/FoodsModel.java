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
public class FoodsModel extends RecursiveTreeObject<FoodsModel> {
    
    // Local variables to hold data fetchec from database
    public final SimpleStringProperty id, image, name, category, price, description, created_at, updated_at;
    
    // Foods Model Constructor
    public FoodsModel(String id, String image, String name, String category, String price, String description, String created_at, String updated_at) {
        this.id = new SimpleStringProperty(id);
        this.image = new SimpleStringProperty(image);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.price = new SimpleStringProperty(price);
        this.description = new SimpleStringProperty(description);
        this.created_at = new SimpleStringProperty(created_at);
        this.updated_at = new SimpleStringProperty(updated_at);
    }
    
    // Return food id
    public String getID() {
        return id.get();
    }
    
    // Return food image
    public String getImage() {
        return image.get();
    }
    
    // Return food name
    public String getName() {
        return name.get();
    }
    
    // Return food category
    public String getCategory() {
        return category.get();
    }
    
    // Return food price
    public String getPrice() {
        return price.get();
    }
    
    // Return food description
    public String getDescription() {
        return description.get();
    }
    
    // Return food created date
    public String getCreated() {
        return created_at.get();
    }
    
    // Return food update date
    public String getUpdated() {
        return updated_at.get();
    }
    
}
