package com.restro.controllers.users;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.restro.messages.Messages;
import com.restro.models.users.OrdersModel;
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
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;

public class OrdersController implements Initializable {

    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    private final String sql = "SELECT "
                                    + "`orders`.`order_id`, "
                                    + "`orders`.`order`, "
                                    + "`users`.`username`, "
                                    + "`orders`.`price`, "
                                    + "`orders`.`full_price`, "
                                    + "`orders`.`created_at` "
                                + "FROM "
                                    + "`orders`, `users` "
                                + "WHERE "
                                    + "`orders`.`user_id` = `users`.`id`";
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private ObservableList <com.restro.models.users.OrdersModel> ordersList;
    private JFXTreeTableColumn <com.restro.models.users.OrdersModel, String> orderID, order, orderer, price, fullPrice, createdDate;
    @FXML
    private JFXTreeTableView <com.restro.models.users.OrdersModel> ordersTable;
    @FXML
    private JFXTextField searchOrders, hidden, orID, orFullPrice, orOrderer, orDate;
    @FXML
    private JFXTextArea orDetails, orPrices;
    @FXML
    private TabPane tabPane;
    
    // Searching for specific order
    @FXML
    protected void findInOrders() {
        if( searchOrders.getText().isEmpty() ) {
            fillOrdersTableData(sql);
        } else {
            fillOrdersTableData(sql+" AND (`orders`.`order_id` LIKE '"+searchOrders.getText().trim()+"%' OR `orders`.`price` LIKE '"+searchOrders.getText().trim()+"%')");
        }
    }
    
    @FXML
    protected void done() {
        tabPane.getSelectionModel().select(0);           orID.setText("");
        orDetails.setText("");                           orPrices.setText("");
        orFullPrice.setText("");                         orOrderer.setText("");
        orDate.setText("");
    }
    
    // Creating orders table columns and fill it with data from database
    private void initOrdersTableCols() {
        orderID = new JFXTreeTableColumn("Order ID");
        orderID.setPrefWidth(80);
        orderID.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().order_id;
        });
        order = new JFXTreeTableColumn("Order Details");
        order.setPrefWidth(200);
        order.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().order;
        });
        price = new JFXTreeTableColumn("Order Prices");
        price.setPrefWidth(100);
        price.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().price;
        });
        fullPrice = new JFXTreeTableColumn("Full Price");
        fullPrice.setPrefWidth(82);
        fullPrice.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().fullPrice;
        });
        orderer = new JFXTreeTableColumn("Orderer");
        orderer.setPrefWidth(200);
        orderer.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().orderer;
        });
        createdDate = new JFXTreeTableColumn("Created Since");
        createdDate.setPrefWidth(135);
        createdDate.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.users.OrdersModel, String> param) -> {
            return param.getValue().getValue().created_at;
        });
        ordersTable.getColumns().addAll(orderID, order, price, fullPrice, orderer, createdDate);
        fillOrdersTableData(sql);
    }
    
    // Fill orders table data
    private void fillOrdersTableData( String sql ) {
        ordersList = FXCollections.observableArrayList();
        TreeItem<com.restro.models.users.OrdersModel> root = new RecursiveTreeItem<>(ordersList, RecursiveTreeObject::getChildren);
        root.getChildren().clear();
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while ( rs.next() ) {
                ordersList.add(new OrdersModel(
                        rs.getString("order_id"), 
                        rs.getString("order"), 
                        rs.getString("username"), 
                        rs.getString("price"), 
                        rs.getString("full_price"), 
                        rs.getString("created_at")
                ));
                ordersTable.setShowRoot(false);
                ordersTable.setRoot(root);
                ordersTable.setRowFactory(rv -> {
                    JFXTreeTableRow<com.restro.models.users.OrdersModel> row  = new JFXTreeTableRow<>();
                    row.setOnMouseClicked(e -> {
                        if ( row.getItem() == null ) {
                            Messages.warning_message("Order Selection", "You can select empty order record!");
                        } else {
                            tabPane.getSelectionModel().select(1);                            orID.setText( row.getItem().getOrderID() );
                            orDetails.setText( row.getItem().getOrder() );                    orPrices.setText( row.getItem().getPrices());
                            orFullPrice.setText( row.getItem().getFullPrice()+" SD" );        orOrderer.setText( row.getItem().getOrderer() );
                            orDate.setText( row.getItem().getCreated());
                        }
                    });
                    return row;
                });
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch orders data, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Calling the function that build orders table
        initOrdersTableCols();
        
    }    
    
}
