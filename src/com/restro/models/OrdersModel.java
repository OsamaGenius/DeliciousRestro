package com.restro.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

public class OrdersModel extends RecursiveTreeObject<OrdersModel> {
    
    public final SimpleStringProperty order_id, order, price, username, created_at;
    
    public OrdersModel(String order_id, String order, String price, String username, String created_at) {
        this.order_id = new SimpleStringProperty(order_id);
        this.order = new SimpleStringProperty(order);
        this.price = new SimpleStringProperty(price);
        this.username = new SimpleStringProperty(username);
        this.created_at = new SimpleStringProperty(created_at);
    }
    
}
