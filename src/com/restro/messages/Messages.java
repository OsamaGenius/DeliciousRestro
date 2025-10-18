/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.restro.messages;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author u_s_e
 */
public class Messages {
    
    public static Alert alert;

    private static void setAlert(Alert alertConfig/*, Node node*/, String title) {
        alertConfig.setHeaderText(title);
        alertConfig.showAndWait();
    }

    public static void information_message(String title, String message) {
        alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        setAlert(alert/*, node*/, title);
    }

    public static void confirmation_messaeg(String title, String message) {
        alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        setAlert(alert/*, node*/, title);
    }

    public static void warning_message(String title, String message) {
        alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        setAlert(alert/*, node*/, title);
    }

    public static void error_message(String title, String message) {
        alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        setAlert(alert/*, node*/, title);
    }

}
