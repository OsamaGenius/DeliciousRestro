package com.restro.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.restro.messages.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProfileController implements Initializable {

    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    private File file;
    public static String idTxt, profileTxt, usernameTxt, jobTxt, emailTxt, bupEmailTxt, phoneTxt;
    private Connection con = null;
    private Statement st = null;
    @FXML
    private Circle profile;
    @FXML
    private JFXTextField hiddenID, image, username, email, bEmail, phone;
    @FXML
    private JFXPasswordField password;
    @FXML
    private Label userMsg, passMsg, emailMsg, bEMsg, phoneMsg;

    // Validate username field
    @FXML
    protected void checkUsername() {
        if ( username.getText().isEmpty() ) {
           userMsg.setVisible(true);
           userMsg.setText("Username must not be empty!");
           userMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if ( !username.getText().matches(manager.charsAndNumPattren) ) {
           userMsg.setVisible(true);
           userMsg.setText("Characters, whitespaces, and numbers only!");
           userMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
           userMsg.setVisible(true);
           userMsg.setText("");
           userMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }

    // Validate password field
    @FXML
    protected void checkPassword() {
        if ( password.getText().isEmpty() ) {
            passMsg.setVisible(true);
            passMsg.setText("Password can not be empty!");
            passMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if ( !password.getText().matches(manager.passPattren) ) {
            passMsg.setVisible(true);
            passMsg.setText("Password is week and not acceptable!");
            passMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            passMsg.setVisible(true);
            passMsg.setText("Password acceptable.");
            passMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }

    // Validate email field
    @FXML
    protected void checkEmail() {
        if ( email.getText().isEmpty() ) {
            emailMsg.setVisible(true);
            emailMsg.setText("Email most not be empty!");
            emailMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if ( !email.getText().matches(manager.emailPattren) ) {
            emailMsg.setVisible(true);
            emailMsg.setText("Not valid email format!");
            emailMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            emailMsg.setVisible(true);
            emailMsg.setText("Email is acceptable.");
            emailMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }

    // Validate backup email field
    @FXML
    protected void checkBEmail() {
        if ( bEmail.getText().isEmpty() ) {
            bEMsg.setText("");
        } else if ( !bEmail.getText().matches(manager.emailPattren) ) {
            bEMsg.setVisible(true);
            bEMsg.setText("Not valid email format!");
            bEMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            bEMsg.setVisible(true);
            bEMsg.setText("Email is acceptable.");
            bEMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }

    // Validate telephone number field
    @FXML
    protected void checkPhone() {
        if ( phone.getText().isEmpty() ) {
            phoneMsg.setText("");
        } else if ( !phone.getText().matches(manager.numsOnlyPattren) ) {
            phoneMsg.setVisible(true);
            phoneMsg.setText("Not valid telephone number!");
            phoneMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            phoneMsg.setVisible(true);
            phoneMsg.setText("Telephone number is acceptable.");
            phoneMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }
    }

    // Choose profile image
    @FXML
    protected void selectImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select New Profile Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images (png, jpg, jpeg)", "*.png", "*.jpg", "*.jpeg"));
        file = fc.showOpenDialog(new Stage());
        if( file == null ) {
            Messages.warning_message("File Cancelation", "You haven't selected and image to set as your new profile");
        } else {
            image.setText(file.getName());
        }
    }

    // Upload proflie image
    @FXML
    protected void uploadImage() {
        if( file == null ) {
            Messages.error_message("Profile Image Failure", "You must choose a profile image first, then try to upload it!");
        } else {
            try {
                String path = file.toURI().toURL().toString();
                profile.setFill(new ImagePattern(new Image(path)));
            } catch (MalformedURLException ex) {
                Messages.error_message("Sever Error", "Unable to upload users profiles images, please contact the developer?\n\nServer Error: "+ex);
            }
        }
    }
    
    // Save profile image
    @FXML
    protected void saveImage() {
        try {
            con = com.restro.database.ConnectDB.getConnection();
            st = con.createStatement();
            if( file == null ) {
                Messages.warning_message("Profile Image Changing", "Please select your new profile image and then press update");
            } else {
                if( st.executeUpdate("UPDATE "
                                        + "`users` "
                                    + "SET "
                                        + "`profile` = '"+file.getName().trim()+"', "
                                        + "`updated_at` = '"+LocalDateTime.now().toString()+"' "
                                    + "WHERE "
                                        + "`id` = '"+hiddenID.getText().trim()+"'") == 1 ) {
                    try (InputStream in = new FileInputStream(file); OutputStream out = new FileOutputStream("Actors\\"+file.getName().trim())) {
                        byte[] content = new byte[1024];
                        int size = 0;
                        while( ( size = in.read(content) ) != -1 ) {
                            out.write(content);
                        }
                    }
                    if( jobTxt.equals("Admin") ) {
                        com.restro.controllers.admins.AdminsController.profile = file.getName().trim();
                        manager.goToAdmins();
                    } else {
                        com.restro.controllers.users.UsersController.profile = file.getName().trim();
                        manager.goToUsers();
                    }
                } else {
                    Messages.error_message("Profile Image Failure", "Uable to update user "+usernameTxt+" profile image, please contact the developer!");
                }
            }
        } catch (SQLException | IOException ex) {
            Messages.error_message("Server Error", "Unable to update user profile image, please contact the developer?\n\nServer Error: "+ex);
        }
    }

    // Save user username
    @FXML
    protected void saveUsername() {
        if( username.getText().isEmpty() || !username.getText().matches(manager.charsAndNumPattren) ) {
            checkUsername();
        } else {
            con = com.restro.database.ConnectDB.getConnection();
            try {
                st = con.createStatement();
                if ( st.executeUpdate("UPDATE "
                                        + "`users` "
                                     + "SET "
                                        + "`username` = '"+username.getText().trim()+"', "
                                        + "`updated_at` = '"+LocalDateTime.now().toString()+"' "
                                     + "WHERE "
                                        + "`id` = '"+hiddenID.getText().trim()+"'") == 1 ) {
                    if ( jobTxt.equals("Admin") ) {
                        Messages.information_message("Username Success", "Successfully updated username to "+username.getText().trim());
                        com.restro.controllers.admins.AdminsController.username = username.getText();
                        manager.goToAdmins();
                    } else {
                        Messages.information_message("Username Success", "Successfully updated username to "+username.getText().trim());
                        com.restro.controllers.users.UsersController.username = username.getText();
                        manager.goToUsers();
                    }
                    username.setText("");
                    userMsg.setText("");
                } else {
                    Messages.error_message("Username Failure", "Failed to update username, please contact the developer!");
                }
            } catch (SQLException | IOException ex) {
                Messages.error_message("Server Error", "Unable to update user username?\n\nServer Error: "+ex);
            }
        }
    }
    
    // Save user new password
    @FXML
    protected void savePassword() {
        if ( password.getText().isEmpty() | !password.getText().matches(manager.passPattren) ) {
            checkPassword();
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                st = con.createStatement();
                if( st.executeUpdate("UPDATE "
                                        + "`users` "
                                    + "SET "
                                        + "`password` = '"+manager.hashPassword(password.getText().trim())+"', "
                                        + "`updated_at` = '"+LocalDateTime.now().toString()+"' "
                                    + "WHERE "
                                        + "`id` = '"+hiddenID.getText().trim()+"'") == 1 ) {
                    Messages.information_message("Password Updates Success", "Successfully updated user password.");
                    password.setText("");
                    passMsg.setText("");
                } else {
                    Messages.error_message("Password Updates Failure", "Unable to change user password, please contact the developer!");
                }
            } catch (SQLException ex) {
                Messages.error_message("Server Error", "Unable to change user password, please contact the developer?\n\nServer Error: "+ex);
            }
        }
    }
    
    // Save user contact info (email, backup email, telephone)
    @FXML
    protected void saveContactInfo() {
        if ( email.getText().isEmpty() || !email.getText().matches(manager.emailPattren) ) {
            checkEmail();
        } else {
            try {
                String tempEmail, tempPhone;
                if ( bEmail.getText().isEmpty() ) {
                    tempEmail = "Not Set";
                } else {
                    tempEmail = bEmail.getText().trim();
                }
                if ( phone.getText().isEmpty() ) {
                    tempPhone = "Not Set";
                } else {
                    tempPhone = phone.getText().trim();
                }
                con = com.restro.database.ConnectDB.getConnection();
                st = con.createStatement();
                if ( st.executeUpdate("UPDATE "
                                        + "`users` "
                                      + "SET "
                                        + "`email` = '"+email.getText().trim()+"', "
                                        + "`b_email` = '"+tempEmail+"', "
                                        + "`phone` = '"+tempPhone+"', "
                                        + "`updated_at` = '"+LocalDateTime.now().toString()+"' "
                                      + "WHERE "
                                        + "`id` = '"+hiddenID.getText().trim()+"'") == 1 ) {
                    Messages.information_message("Contact Info Success", "Successfully updated user contact information.");
                } else {
                    Messages.error_message("Contact Info Failure", "Unable to update user contact info, please contact the developer!");
                }
            } catch (SQLException ex) {
                Messages.error_message("Server Error", "Unable to update user contact info, please contact the developer?\n\nServer Error: "+ex);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Initialize profile page data
        hiddenID.setText(idTxt);
        profile.setFill(new ImagePattern(new Image("file:Actors\\"+profileTxt)));
        username.setText(usernameTxt);
        email.setText(emailTxt);
        bEmail.setText(bupEmailTxt);
        phone.setText(phoneTxt);
        
    }    
    
}
