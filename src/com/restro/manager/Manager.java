package com.restro.manager;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Manager extends Application {

    // Creating the necessary variables  
    private static Stage window;
    public final static com.restro.messages.Messages messages = new com.restro.messages.Messages();
    public String redColour          = "#dc3545",
                  greenColour        = "#198754",
                  whiteSmokeColour   = "#f5f5f5",
                  charsOnlyPattren   = "^[a-z A-Z]{1,}$",
                  numsOnlyPattren    = "^[0-9]{1,}$",
                  charsAndNumPattren = "^[a-z A-Z 0-9]{1,}$",
                  emailPattren       = "^[a-zA-Z0-9.]+@[a-zA-Z0-9]+[.][a-zA-Z]{1,}$",
                  passPattren        = "^[(?=.*[0-9])(?=.[a-z])(?=.*[A-Z])(?=.*[@#$%^&*+=])(?=\\\\S+$)]{8,}$";
    
    // Getting the main window features and share it with other windows
    public static Stage getWindow() {
        return window;
    }
    
    // The JavaFX main builder function
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        goToLogin();
        primaryStage.show();
    }
    
    // Siwtching among windows
    private Initializable replaceScene(String file, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        InputStream src = Manager.class.getResourceAsStream(file);
        loader.setLocation(Manager.class.getResource(file));
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        AnchorPane holder;
        try {
            holder = (AnchorPane) loader.load(src);
        } finally {
            src.close();
        }
        Scene scene = new Scene(holder);
        scene.setFill(Color.TRANSPARENT);
        window.sizeToScene();
        window.setTitle(title);
        window.setScene(scene);
        return (Initializable) loader.getController();
    }
    
    // Navigate to login window
    public void goToLogin() throws IOException {
        com.restro.controllers.AuthController login = (com.restro.controllers.AuthController) replaceScene("/com/restro/pages/login.fxml", "Login to start your session");
        login.setApp(this);
    }
    
    // Navigate to admins home window
    public void goToAdmins() throws IOException {
        com.restro.controllers.admins.AdminsController homeD = (com.restro.controllers.admins.AdminsController) replaceScene("/com/restro/pages/admin/homeD.fxml", "Welcome to Admin Panel");
        homeD.setApp(this);
    }
    
    // Navigate to users home window
    public void goToUsers() throws IOException {
        com.restro.controllers.users.UsersController homeU = (com.restro.controllers.users.UsersController) replaceScene("/com/restro/pages/user/homeU.fxml", "Welcome to Users Panel");
        homeU.setApp(this);
    }
    
    // Terminate Appliction
    public void terminate() {
        Platform.exit();
    }
    
    // Miniize Windows
    public void minimize(Stage currentWindow) {
        currentWindow.setIconified(true);
    }
    
    // Hashing password
    public String hashPassword(String password) {
        String newPassword = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            newPassword = stringBuilder.toString();
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Unable to hash password");
        }
        return newPassword;
    }
    
    // Java Running function
    public static void main(String[] args) {
        launch(args);
    }
    
}
