package com.restro.controllers.users;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInRight;
import com.jfoenix.controls.JFXButton;
import com.restro.manager.Manager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class UsersController implements Initializable {

    // Necessary variables
    private Manager manager;
    private final Stage newWindow = Manager.getWindow();
    public static String id, profile, username, job, email, bupEmail, phone;
    private double xOffset, yOffset;
    @FXML
    private Label pane;
    @FXML
    private AnchorPane holder;
    @FXML
    private FontAwesomeIconView dashboard_arrow, profile_arrow, orders_arrow, newOrders_arrow;
    @FXML
    private Circle actor;
    @FXML
    private JFXButton nameOfUser;
    
    // Building the current newWindow
    public void setApp(Manager manager) {
        this.manager = manager;
        newWindow.centerOnScreen();
    }
    
    // Logout users session
    @FXML
    protected void logout() throws IOException {
        manager.goToLogin();
    }
    
    // Terminate application
    @FXML
    protected void close() {
        manager.terminate();
    }
    
    // Minimize window
    @FXML
    protected void minimize() {
        manager.minimize(newWindow);
    }
    
    // Load page for the user = cashier
    @FXML
    protected void loadDashboard() throws IOException {
        createPages("/com/restro/pages/dashboard.fxml", dashboard_arrow);
    }
    
    // Load page for the user = cashier
    @FXML
    protected void loadProfile() throws IOException {
        com.restro.controllers.ProfileController.idTxt = id;
        com.restro.controllers.ProfileController.profileTxt = profile;
        com.restro.controllers.ProfileController.usernameTxt = username;
        com.restro.controllers.ProfileController.jobTxt = job;
        com.restro.controllers.ProfileController.emailTxt = email;
        com.restro.controllers.ProfileController.bupEmailTxt = bupEmail;
        com.restro.controllers.ProfileController.phoneTxt = phone;
        createPages("/com/restro/pages/profile.fxml", profile_arrow);
    }
    
    // Load page orders list for the user = cashier
    @FXML
    protected void loadOrders() throws IOException {
        createPages("/com/restro/pages/user/orders.fxml", orders_arrow);
    }
    
    // Load food and juice page for the user = cashier
    @FXML
    protected void loadNewOrders() throws IOException {
        createPages("/com/restro/pages/user/newOrders.fxml", newOrders_arrow);
    }
    
    // Switching among users pages
    private void createPages(String fxml, FontAwesomeIconView target) throws IOException {
        AnchorPane node = FXMLLoader.load(getClass().getResource(fxml));
        holder.getChildren().clear();
        holder.getChildren().add(node);
        new SlideInRight(holder).play();
        showArrows(target);
    }
    
    // Switching amonge active arrows
    private void showArrows(FontAwesomeIconView target) {
        // Creating the Font Awesome array
        List<FontAwesomeIconView> arrows = new ArrayList<>();
        // Adding icons inside the array
        arrows.add(0, dashboard_arrow);
        arrows.add(1, profile_arrow);
        arrows.add(2, orders_arrow);
        arrows.add(3, newOrders_arrow);
        // Looping to activate the target icon
        arrows.forEach((FontAwesomeIconView arrow) -> {
            // Checking the currunt icon if it == target
            if( target.equals(arrow) ) {
                target.setVisible(true);
                new SlideInLeft(target).play();
            } else {
                arrow.setVisible(false);
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        actor.setFill(new ImagePattern(new Image("file:Actors\\"+profile)));
        nameOfUser.setText(username);
        
        // Loading the default page = dashboard
        try {
            createPages("/com/restro/pages/dashboard.fxml", dashboard_arrow);
        } catch(IOException ex) {
            System.err.println("File users error: " + ex);
        }
        
        // Moving the window
        pane.setOnMouseEntered(e -> {
            pane.setTooltip(new Tooltip("Pressed here and then drag to move the window."));
        });
        
        pane.setOnMousePressed(e -> {
            xOffset = newWindow.getX() - e.getScreenX();
            yOffset = newWindow.getY() - e.getScreenY();
        });
        
        pane.setOnMouseDragged(e -> {
            newWindow.setX( xOffset + e.getScreenX() );
            newWindow.setY( yOffset + e.getScreenY() );
        });
        
    }    
    
}
