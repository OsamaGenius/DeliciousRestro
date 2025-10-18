package com.restro.controllers;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.restro.manager.Manager;
import com.restro.messages.Messages;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class AuthController implements Initializable {

    // Necessary variables
    private Manager manager;
    private final Stage newWindow = Manager.getWindow();
    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private double xOffset, yOffset;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXPasswordField password;
    @FXML
    private Label emailMsg, passMsg, unmaskPass, pane;
    @FXML
    private JFXCheckBox passBox;
    @FXML
    private JFXSpinner progress;
    
    // Building the current newWindow
    public void setApp(Manager manager) {
        this.manager = manager;
        newWindow.centerOnScreen();
    }
    
    // Perform the login logic
    @FXML
    protected void login() {
        if ( email.getText().isEmpty() || !email.getText().matches(manager.emailPattren) || password.getText().isEmpty() || !password.getText().matches(manager.passPattren) ) {
            validateEmail();
            validatePassword();
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("SELECT * FROM `users` WHERE `email` = ? AND `password` = ?");
                ps.setString(1, email.getText().trim());
                ps.setString(2, manager.hashPassword(password.getText().trim()));
                rs = ps.executeQuery();
                if( rs.next() ) {
                    if( rs.getString("job").equals("Admin") ) {
                        com.restro.controllers.admins.AdminsController.id = rs.getString("id");
                        com.restro.controllers.admins.AdminsController.profile = rs.getString("profile");
                        com.restro.controllers.admins.AdminsController.username = rs.getString("username");
                        com.restro.controllers.admins.AdminsController.job = rs.getString("job");
                        com.restro.controllers.admins.AdminsController.email = rs.getString("email");
                        com.restro.controllers.admins.AdminsController.bupEmail = rs.getString("b_email");
                        com.restro.controllers.admins.AdminsController.phone = rs.getString("phone");
                        manager.goToAdmins();
                    } else {
                        com.restro.controllers.users.UsersController.id = rs.getString("id");
                        com.restro.controllers.users.UsersController.profile = rs.getString("profile");
                        com.restro.controllers.users.UsersController.username = rs.getString("username");
                        com.restro.controllers.users.UsersController.job = rs.getString("job");
                        com.restro.controllers.users.UsersController.email = rs.getString("email");
                        com.restro.controllers.users.UsersController.bupEmail = rs.getString("b_email");
                        com.restro.controllers.users.UsersController.phone = rs.getString("phone");
                        manager.goToUsers();
                    }
                } else {
                    Messages.error_message("Login Failure", "Unable to login, please check that you entered the right data(email, password).\n\nIf you entered the right data and this message still appears please contact the developer!");
                }
            } catch (SQLException | IOException ex) {
                Messages.error_message("Server Error", "Unable to login, please contact the developer?\n\nServer Error: "+ex);
            }
        }
    }
    
    // Validate email field
    @FXML
    protected void validateEmail() {
        if( email.getText().isEmpty() ) {
            emailMsg.setVisible(true);
            emailMsg.setText("Email is required, please fill it!");
            emailMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if( !email.getText().matches(manager.emailPattren) ) {
            emailMsg.setVisible(true);
            emailMsg.setText("This email is not valid one!");
            emailMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            emailMsg.setVisible(true);
            emailMsg.setText("Email is acceptable.");
            emailMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }
    
    // Validate password field
    @FXML
    protected void validatePassword() {
        if( password.getText().isEmpty() ) {
            passMsg.setVisible(true);
            passMsg.setText("Password is required, please fill it!");
            passMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if( !password.getText().matches(manager.passPattren) ) {
            passMsg.setVisible(true);
            passMsg.setText("This password is not acceptable!");
            passMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            passMsg.setVisible(true);
            passMsg.setText("Password is acceptable.");
            passMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }
    
    @FXML
    protected void showHidePass() {
        if( passBox.isSelected() ) {
            unmaskPass.setVisible(true);
            unmaskPass.textProperty().bind(password.textProperty());
        } else {
            unmaskPass.setVisible(false);
        }
    }
    
    // Close the application
    @FXML
    protected void close() {
        manager.terminate();
    }
    
    // Minimize the login window
    @FXML
    protected void minimize() {
        manager.minimize(newWindow);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
