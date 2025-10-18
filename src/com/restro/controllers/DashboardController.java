package com.restro.controllers;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.restro.messages.Messages;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class DashboardController implements Initializable {

    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    @FXML
    private Label foods, juices, orders, users;
    @FXML
    private GridPane latestFoods, latestJuices;
    @FXML
    private JFXTreeTableView<com.restro.models.users.OrdersModel> ordersTable;
    private ObservableList<com.restro.models.users.OrdersModel> ordersList;
    private JFXTreeTableColumn<com.restro.models.users.OrdersModel, String> id, order, price, fullPrice, orderer, created;
    
    // Fetching foods number
    private void getFoods() {
        try {
            ps = con.prepareStatement("SELECT COUNT(`id`) FROM `foods`");
            rs = ps.executeQuery();
            if( rs.next() ) {
                foods.setText(rs.getString("COUNT(`id`)"));
            } else {
                Messages.error_message("Foods Fetching Failure", "Unable to fetch foods list numbers from database, please contact the developer!");
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch foods list numbers from database, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Fetching juices numbers
    private void getJuices() {
        try {
            ps = con.prepareStatement("SELECT COUNT(`id`) FROM `juices`");
            rs = ps.executeQuery();
            if( rs.next() ) {
                juices.setText(rs.getString("COUNT(`id`)"));
            } else {
                Messages.error_message("Juices Fetching Failure", "Unable to fetch juices list numbers from database, please contact the developer!");
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch juices list numbers from database, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Fetching orders numbers
    private void getOrders() {
        try {
            ps = con.prepareStatement("SELECT COUNT(`id`) FROM `orders`");
            rs = ps.executeQuery();
            if( rs.next() ) {
                orders.setText(rs.getString("COUNT(`id`)"));
            } else {
                Messages.error_message("Orders Fetching Failure", "Unable to fetch orders list numbers from database, please contact the developer!");
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch orders list numbers from database, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Fetching users numbers
    private void getUsers() {
        try {
            ps = con.prepareStatement("SELECT COUNT(`id`) FROM `users`");
            rs = ps.executeQuery();
            if( rs.next() ) {
                users.setText(rs.getString("COUNT(`id`)"));
            } else {
                Messages.error_message("Users Fetching Failure", "Unable to fetch users list numbers from database, please contact the developer!");
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch users list numbers from database, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Fetching latest 6 foods
    private void getLatestFoods() {
        try {
            ps = con.prepareStatement("SELECT `image`, `name`, `price` FROM `foods` ORDER BY `id` DESC LIMIT 6");
            rs = ps.executeQuery();
            int i = 0, j = 0;
            while( rs.next() ) {
                VBox parent = new VBox();
                parent.setAlignment(Pos.CENTER);
                VBox child = new VBox();
                child.setAlignment(Pos.CENTER);
                Circle image = new Circle();
                image.setRadius(40);
                image.setFill(new ImagePattern(new Image("file:Foods\\"+rs.getString("image"))));
                image.getStyleClass().add("primary-shadow");
                child.getChildren().addAll(new Label(rs.getString("name")), new Label(rs.getString("price")+" SD"));
                parent.getChildren().addAll(image, child);
                latestFoods.setAlignment(Pos.CENTER);
                latestFoods.setVgap(100);
                latestFoods.add(parent, i, j);
                if( i < 2 ) {
                    i++;
                } else {
                    j++;
                    i = 0;
                }
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch latest foods data, please contact the deeveloper?\n\nServer Error: "+ex);
        }
    }
    
    // Fetching latest 6 juices
    private void getLatestJuices() {
        try {
            ps = con.prepareStatement("SELECT `image`, `name`, `price` FROM `juices` ORDER BY `id` DESC LIMIT 6");
            rs = ps.executeQuery();
            int i = 0, j = 0;
            while( rs.next() ) {
                VBox parent = new VBox();
                parent.setAlignment(Pos.CENTER);
                VBox child = new VBox();
                child.setAlignment(Pos.CENTER);
                Circle image = new Circle();
                image.setRadius(40);
                image.setFill(new ImagePattern(new Image("file:Juices\\"+rs.getString("image"))));
                image.getStyleClass().add("danger-shadow");
                child.getChildren().addAll(new Label(rs.getString("name")), new Label(rs.getString("price")+" SD"));
                parent.getChildren().addAll(image, child);
                latestJuices.setAlignment(Pos.CENTER);
                latestJuices.setVgap(100);
                latestJuices.add(parent, i, j);
                if( i < 2 ) {
                    i++;
                } else {
                    j++;
                    i = 0;
                }
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch latest juices data, please contact the deeveloper?\n\nServer Error: "+ex);
        }
    }
    
    // Initialize Orders table columns
    private void initOrdersTableColumns() {
        id = new JFXTreeTableColumn("Order ID");
        id.setPrefWidth(80);
        id.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().order_id;
        });
        order = new JFXTreeTableColumn("Order List");
        order.setPrefWidth(180);
        order.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().order;
        });
        price = new JFXTreeTableColumn("Prices");
        price.setPrefWidth(80);
        price.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().price;
        });
        fullPrice = new JFXTreeTableColumn("Full Price");
        fullPrice.setPrefWidth(82);
        fullPrice.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().fullPrice;
        });
        orderer = new JFXTreeTableColumn("Order Orderer");
        orderer.setPrefWidth(200);
        orderer.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().orderer;
        });
        created = new JFXTreeTableColumn("Added Since");
        created.setPrefWidth(135);
        created.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().created_at;
        });
        ordersTable.getColumns().addAll(id, order, price, fullPrice, orderer, created);
        fillOrdersTableData();
    }
    
    // Fill orders table data
    private void fillOrdersTableData() {
        try {
            ordersList = FXCollections.observableArrayList();
            TreeItem<com.restro.models.users.OrdersModel> root = new RecursiveTreeItem<>(ordersList, RecursiveTreeObject::getChildren);
            root.getChildren().clear();
            ps = con.prepareStatement("SELECT `orders`.`id`, `orders`.`order_id`, `orders`.`order`, `orders`.`price`, `orders`.`full_price`, `users`.`username`, `orders`.`created_at` FROM `orders`, `users` WHERE `orders`.`user_id` = `users`.`id` ORDER BY `orders`.`id` DESC");
            rs = ps.executeQuery();
            while( rs.next() ) {
                ordersList.add(new com.restro.models.users.OrdersModel(
                        rs.getString("order_id"), 
                        rs.getString("order"),  
                        rs.getString("username"), 
                        rs.getString("price"), 
                        rs.getString("full_price"),
                        rs.getString("created_at")
                ));
            }
            ordersTable.setShowRoot(false);
            ordersTable.setRoot(root);
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch orders llist from database, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        con = com.restro.database.ConnectDB.getConnection();
        
        // Calling fetching foods number method
        getFoods();
        
        // Calling fetching juices number method
        getJuices();
        
        // Calling fetching orders number method
        getOrders();
        
        // Calling fetching users number method
        getUsers();
        
        // Calling fetching latest 6 foods method
        getLatestFoods();
        
        // Calling fetching latest 6 juices method
        getLatestJuices();
        
        // Calling initialize orders table columns and data
        initOrdersTableColumns();
        
    }
    
}
