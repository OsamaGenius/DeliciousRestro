package com.restro.controllers.users;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.SlideInDown;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.restro.messages.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class NewOrdersController implements Initializable {

    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    private String order = "", tempOrder = "", price = "", tempPrice = "";
    private int fullPrice, tempFullPrice;
    private final LocalDateTime dt = LocalDateTime.now();
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    @FXML
    private GridPane foodsList, juicesList;
    @FXML
    private TabPane tabPane;
    @FXML
    private JFXTextField foodsSearch, juicesSearch, orderAmount;
    @FXML
    private JFXComboBox filterFoodsBox, filterJuicesBox;
    @FXML
    private Label priceFullTxt, currentOrder, orderNumMsg;
    @FXML
    private JFXTextArea orderTxt, priceTxt;
    @FXML
    private AnchorPane detailsPane, amountPane;
    @FXML
    private Pane innerBox, amountBox;
    
    // Search in (foods/juices) menu with specific key word
    @FXML
    protected void findInMenu() {
        if( tabPane.getSelectionModel().isSelected(0) ) {
            getRestroMenu("`foods`", foodsList, "Foods\\", " WHERE `name` LIKE '"+foodsSearch.getText().trim()+"%' OR `price` LIKE '"+foodsSearch.getText().trim()+"%'");
        } else {
            getRestroMenu("`juices`", juicesList, "Juices\\", " WHERE `name` LIKE '"+juicesSearch.getText().trim()+"%' OR `price` LIKE '"+juicesSearch.getText().trim()+"%'");
        }
    }
    
    // Allowing the cashier to extract users orders ( get tackit )
    @FXML
    protected void requestOrder() {
        if( orderTxt.getText().isEmpty() ) {
            Messages.warning_message("Order Alert", "Please choose order data from the foods menu or juices menu or both and then request the order");
        } else {
            try {
                Random rand = new Random();
                int num = rand.nextInt(9999);
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("INSERT INTO `orders` (`order_id`, `user_id`, `order`, `price`, `full_price`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, "OR-"+String.valueOf(num));
                ps.setString(2, com.restro.controllers.users.UsersController.id);
                ps.setString(3, order);
                ps.setString(4, price);
                ps.setString(5, String.valueOf(fullPrice));
                ps.setString(6, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                ps.setString(7, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                int status = ps.executeUpdate();
                if ( status > 0 ) {
                    ps = con.prepareStatement("SELECT `name` FROM `reports` WHERE `type` = 'orders'");
                    rs = ps.executeQuery();
                    if ( rs.next() ) {
                        // Read the jrxml 
                        InputStream path = new FileInputStream(new File("Reports\\"+rs.getString("name")).getAbsolutePath());
                        try {
                            // Creating jasper designer object
                            JasperDesign jasperDesign = JRXmlLoader.load(path);
                            // Compiling jrxml with the hlep of JasperReport class
                            JasperReport report = JasperCompileManager.compileReport(jasperDesign);
                            // Using jasper report object to generate PDF
                            JasperPrint print = JasperFillManager.fillReport(report, null, con);
                            // Call jasper engine to display report in jasper viewer window
                            JasperViewer.viewReport(print, false);
                        } catch (JRException ex) {
                            Messages.error_message("Server Error", "Unable to extract user order, please contact the developer?\n\nServer Error: "+ex);
                        }
                    } else {
                        Messages.error_message("Order Creation Failure", "Your order have been saved to the server but unable to extract report, please contact the developer!");
                    }
                } else {
                    Messages.error_message("Order Creation Failure", "Unable to make your order, please contact the developer!");
                }
            } catch (SQLException | IOException ex) {
                Logger.getLogger("Server Error", "Can not creating your order data, please contact the developer?\n\nServer Error: "+ex);
            }
        }
    }
    
    // Extracting the customer order id 
    @FXML
    protected void getTicketID() {
        if( orderTxt.getText().isEmpty() ) {
            Messages.warning_message("Order ID Alert", "Please choose order data from the foods menu or juices menu or both and then request the order ID");
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("SELECT `name` FROM `reports` WHERE `type` = 'orderID'");   
                rs = ps.executeQuery();
                if( rs.next() ) {
                    // Read the jrxml 
                    InputStream path = new FileInputStream(new File("Reports\\"+rs.getString("name")).getAbsolutePath());
                    // Creating jasper designer object
                    JasperDesign jasperDesign = JRXmlLoader.load(path);
                    // Compiling jrxml with the hlep of JasperReport class
                    JasperReport report = JasperCompileManager.compileReport(jasperDesign);
                    // Using jasper report object to generate PDF
                    JasperPrint print = JasperFillManager.fillReport(report, null, con);
                    // Call jasper engine to display report in jasper viewer window
                    JasperViewer.viewReport(print, false);
                    doCancelation();
                } else {
                    Messages.error_message("User Ticket Failure", "Unable to get user ticket, please contact the developer!");
                }
            } catch (SQLException | IOException | JRException ex) {
                Messages.error_message("Server Error", "Unable to get user ticket, please contact the developer?\n\nServer Error: "+ex);
            }   
        }
    }
    
    // Cancel user order
    @FXML
    protected void cancelOrder() {
        Messages.confirmation_messaeg("Cancel Order", "Are you sure that you want to cancel this order?");
        if( Messages.alert.getResult() == ButtonType.YES ) {
            doCancelation();
            Messages.information_message("Order Cancelation", "Order canceled successfully.");
        } else {
            System.err.println("Order in process ...");
        }
    }
    private void doCancelation() {
        order = "";                 fullPrice = 0;
        orderTxt.setText("");       priceTxt.setText("");
        priceFullTxt.setText("0");  price = "";
    }
    
    // Show user order
    @FXML
    protected void showOrder() {
        detailsPane.setVisible(true);
        new FadeIn(detailsPane).play();
        new SlideInDown(innerBox).play();
        orderTxt.setText(order);
        priceTxt.setText(price);
        priceFullTxt.setText(String.valueOf(fullPrice)+" SD");
    }
    
    // Hide   user order
    @FXML
    protected void hideOrder() {
        new FadeOut(detailsPane).play();
        detailsPane.setVisible(false);
    }
    
    // Complete order selection
    @FXML
    protected void completeOrderSelection() {
        if( orderAmount.getText().isEmpty() ) {
            order += tempOrder + "\n";
            price += tempPrice + " SD\n";
            fullPrice += tempFullPrice;
            amountPane.setVisible(false);
            orderAmount.setText("");
            orderNumMsg.setText("");
            Messages.information_message("Order Completaion", "Selected order added successfully to the order list, press view button to see it.");
        } else {
            order += orderAmount.getText() + " " + tempOrder + "\n";
            int result = Integer.valueOf(orderAmount.getText()) * Integer.valueOf(tempPrice);
            price += String.valueOf(result) + " SD\n";
            fullPrice += tempFullPrice * Integer.valueOf(orderAmount.getText());
            orderAmount.setText("");
            orderNumMsg.setText("");
            amountPane.setVisible(false);
            Messages.information_message("Order Completaion", "Selected order added successfully to the order list, press view button to see it.");
        }
    }
    
    // Cancel order selection
    @FXML
    protected void cancelOrderSelection() {
        Messages.confirmation_messaeg("Order Cancelation", "Are you sure that you want to cancel ordering "+tempOrder);
        if( Messages.alert.getResult() == ButtonType.YES ) {
            amountPane.setVisible(false);
            orderAmount.setText("");
            orderNumMsg.setText("");
        }
    }
    
    // Validate users data when order amount request
    @FXML
    protected void validateOrderNum() {
        if( !orderAmount.getText().matches(manager.numsOnlyPattren) ) {
            orderNumMsg.setText("Accepts numbers only!");
            orderNumMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            orderNumMsg.setText("Accpted order amount");
            orderNumMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }
    
    // Filtering order menu by (foods/juices) box
    @FXML
    protected void filterMenuByBox() {
        if ( tabPane.getSelectionModel().isSelected(0) ) {
            if ( filterFoodsBox.getSelectionModel().getSelectedItem().equals("All") ) {
                getRestroMenu("`foods`", foodsList, "Foods\\", "");
            } else {
                try {
                    String id;
                    con = com.restro.database.ConnectDB.getConnection();
                    ps = con.prepareStatement("SELECT `id` FROM `cates` WHERE `category` = '"+filterFoodsBox.getSelectionModel().getSelectedItem()+"'");
                    rs = ps.executeQuery();
                    if ( rs.next() ) {
                        id = rs.getString("id");
                        getRestroMenu("`foods`", foodsList, "Foods\\", " WHERE `cate_id` LIKE '"+id+"'");
                    } else {
                        Messages.error_message("Filtering Foods Failure", "Unable to filter foods menu with the selected category, please contact the developer!");
                    }
                } catch (SQLException ex) {
                    Messages.error_message("Server Error", "Unable to filter foods menu with the selected category, please contact the developer?\n\nServer Error: "+ex);
                }
            }
        } else {
            if ( filterJuicesBox.getSelectionModel().getSelectedItem().equals("All") ) {
                getRestroMenu("`juices`", juicesList, "Juices\\", "");
            } else {
                getRestroMenu("`juices`", juicesList, "Juices\\", " WHERE `type` LIKE '"+filterJuicesBox.getSelectionModel().getSelectedItem()+"'");
            }
        }
    }
    
    // Get fill (foods/juices) menu in grid form
    private void getRestroMenu( String tablename, GridPane target, String path, String selectionPattern ) {
        
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement("SELECT `image`, `name`, `price` FROM "+tablename+selectionPattern);
            rs = ps.executeQuery();
            int i = 0, j = 0;
            target.getChildren().clear();
            while ( rs.next() ) {
                ImageView img = new ImageView(new Image("file:"+path+rs.getString("image")));
                img.setFitWidth(75);
                img.setFitHeight(75);
                Label name = new Label(rs.getString("name"));
                name.setPadding(new Insets(8, 0, 3, 0));
                VBox v = new VBox();
                v.setAlignment(Pos.CENTER);
                v.getStyleClass().add("list-box");
                v.getChildren().addAll(img, name, new Label(rs.getString("price")+" SD"));
                target.add(v, i, j);
                if ( i < 3 ) {
                    i++;
                } else {
                    j++;
                    i = 0;
                }
                v.setOnMouseClicked(e -> {
                    Object[] arr = v.getChildren().toArray();
                    Label data = (Label) arr[1];
                    try {
                        ps = con.prepareStatement("SELECT `name`, `price` FROM "+tablename+" WHERE `name` = '"+data.getText()+"'");
                        rs = ps.executeQuery();
                        if(rs.next()) {
                            tempOrder = rs.getString("name");
                            tempPrice = rs.getString("price");
                            tempFullPrice = Integer.valueOf(rs.getString("price"));
                            amountPane.setVisible(true);
                            currentOrder.setText("Ordering '"+tempOrder+"'");
                            new FadeIn(amountPane).play();
                            new SlideInDown(amountBox).play();
                        } else {
                            Messages.error_message("Selection Failure", "Unable to select your request, please contact the developer!");
                        }
                    } catch (SQLException ex) {
                        Messages.error_message("Server Error", "Unable to select your request, please contact the developer?\n\nServer Error: "+ex);
                    }
                });
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to select your request, please contact the developer?\n\nServer Error: "+ex);
        }
        
    }
    
    // Fill foods menu
    private void getFillFilterFoodsMenuBoxs() {
        try {
            filterFoodsBox.getItems().clear();
            filterFoodsBox.getItems().add("All");
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement("SELECT `category` FROM `cates`");
            rs = ps.executeQuery();
            while( rs.next() ) {
                filterFoodsBox.getItems().addAll(rs.getString("category"));
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fill foods filtering box!, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Fill foods menu
        getRestroMenu("`foods`", foodsList, "foods\\", "");
        
        // Fill juices menu
        getRestroMenu("`juices`", juicesList, "juices\\", "");
        
        // Filling filtering foods box
        getFillFilterFoodsMenuBoxs();
        
        // Filling filtering Juices box
        filterJuicesBox.getItems().addAll("All", "Cold", "Hot");
        
    }    
    
}
