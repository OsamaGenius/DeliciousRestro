package com.restro.controllers.admins;

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

public class AdminsController implements Initializable {

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
    private FontAwesomeIconView dashboard_arrow, profile_arrow, users_arrow, food_arrow, juice_arrow;
    @FXML
    private Circle actor;
    @FXML
    private JFXButton nameOfUser;

    // Building the current newWindow
    public void setApp(Manager manager) {
        this.manager = manager;
        newWindow.centerOnScreen();
    }
    
    // Logout admins session
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
    
    // Loading dashboard page
    @FXML
    protected void loadDashboard() throws IOException {
        createPages("/com/restro/pages/dashboard.fxml", dashboard_arrow);
    }
    
    // Loading Profile page
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
    
    // Loading users page
    @FXML
    protected void loadUsers() throws IOException {
        createPages("/com/restro/pages/admin/users.fxml", users_arrow);
    }
    
    // Loading food page
    @FXML
    protected void loadFood() throws IOException {
        createPages("/com/restro/pages/admin/food.fxml", food_arrow);
    }
    
    // Loading juice page
    @FXML
    protected void loadJuice() throws IOException {
        createPages("/com/restro/pages/admin/juice.fxml", juice_arrow);
    }
    
    // Switching among admins pages
    private void createPages( String fxml, FontAwesomeIconView target ) throws IOException {
        AnchorPane node = FXMLLoader.load(getClass().getResource(fxml));
        holder.getChildren().clear();
        holder.getChildren().add(node);
        new SlideInRight(holder).play();
        showArrow(target);
    }
    
    // Showing focuing arrows 
    private void showArrow(FontAwesomeIconView target) {
        // Creating the Font Awesome array
        List<FontAwesomeIconView> arrows = new ArrayList<>();
        // Adding icons inside the array
        arrows.add(0, dashboard_arrow);
        arrows.add(1, profile_arrow);
        arrows.add(2, users_arrow);
        arrows.add(3, food_arrow);
        arrows.add(4, juice_arrow);
        // Looping to activate the target icon
        arrows.forEach((FontAwesomeIconView arrow) -> {
            // Checking the currunt icon if it == target
            if (target.equals(arrow)) {
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
            System.err.println("File admins error: " + ex);
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
