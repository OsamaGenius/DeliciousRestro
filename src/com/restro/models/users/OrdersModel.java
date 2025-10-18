package com.restro.models.users;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

public class OrdersModel extends RecursiveTreeObject<OrdersModel> {
    
    public final SimpleStringProperty order_id, order, orderer, price, fullPrice, created_at;
    
    public OrdersModel( String order_id, String order, String orderer, String price, String fullPrice, String created_at ) {
        this.order_id   = new SimpleStringProperty(order_id);
        this.order      = new SimpleStringProperty(order);
        this.orderer    = new SimpleStringProperty(orderer);
        this.price      = new SimpleStringProperty(price);
        this.fullPrice  = new SimpleStringProperty(fullPrice);
        this.created_at = new SimpleStringProperty(created_at);
    }
    
    public String getOrderID() {
        return order_id.get();
    }
    
    public String getOrder() {
        return order.get();
    }
    
    public String getOrderer() {
        return orderer.get();
    }
    
    public String getPrices() {
        return price.get();
    }
    
    public String getFullPrice() {
        return fullPrice.get();
    }
    
    public String getCreated() {
        return created_at.get();
    }
    
}
