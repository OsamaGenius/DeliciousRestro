package com.restro.controllers.admins;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.restro.messages.Messages;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class UsersController implements Initializable {

    // Necessary variales
    private Connection con = null;
    private Statement st = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    private final String sql = "SELECT `id`, `username`, `job`, `email`, `b_email`, `phone`, `created_at` FROM `users`";
    private JFXTreeTableColumn<com.restro.models.admins.UserModel, String> userCol, jobCol, emailCol, b_emailCol, phoneCol, createdCol;
    private ObservableList<com.restro.models.admins.UserModel> usersList;
    @FXML
    private VBox latestUsers;
    @FXML
    private JFXTextField userTxtAdd, emailTxtAdd, search;
    @FXML
    private JFXComboBox<String> jobTxtAdd;
    @FXML
    private JFXPasswordField passwordTxtAdd;
    @FXML
    private Label addUserTxtMsg, addEmailTxtMsg, addJobTxtMsg, addPassTxtMsg;
    @FXML
    private JFXCheckBox addShowBox;
    @FXML
    private JFXTreeTableView<com.restro.models.admins.UserModel> table;

    // Creating new users
    @FXML
    protected void createUsers() {
        if (userTxtAdd.getText().isEmpty() || !userTxtAdd.getText().matches(manager.charsOnlyPattren) || emailTxtAdd.getText().isEmpty()
                || !emailTxtAdd.getText().matches(manager.emailPattren) || passwordTxtAdd.getText().isEmpty() || !passwordTxtAdd.getText().matches(manager.passPattren)) {
            validateUserData();
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("SELECT `email` FROM `users` WHERE `email` = '" + emailTxtAdd.getText() + "'");
                rs = ps.executeQuery();
                if (rs.next()) {
                    addEmailTxtMsg.setVisible(true);
                    addEmailTxtMsg.setText("Email existitng");
                } else {
                    LocalDateTime dt = LocalDateTime.now();
                    ps = con.prepareStatement("INSERT INTO `users` (`username`, `email`, `job`, `password`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setString(1, userTxtAdd.getText().trim());
                    ps.setString(2, emailTxtAdd.getText().trim());
                    ps.setString(3, jobTxtAdd.getSelectionModel().getSelectedItem().trim());
                    ps.setString(4, manager.hashPassword(passwordTxtAdd.getText().trim()));
                    ps.setString(5, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                    ps.setString(6, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        Messages.information_message("Adding Users", "Successfully adding user '" + userTxtAdd.getText().trim() + "'");
                        fillTableData(sql);
                        resetMsgLables();
                        getLatestUsers();
                    } else {
                        Messages.error_message("Adding User", "Failed to add user '" + userTxtAdd.getText().trim() + "', please contact the developer!");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    // Validate username while typing
    @FXML
    protected void validateUsername() {
        if (userTxtAdd.getText().isEmpty()) {
            addUserTxtMsg.setVisible(true);
            addUserTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addUserTxtMsg.setText("Username is required, please fill it!");
        } else if (!userTxtAdd.getText().matches(manager.charsOnlyPattren)) {
            addUserTxtMsg.setVisible(true);
            addUserTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addUserTxtMsg.setText("only small/capital letters, and white spaces acceptable!");
        } else {
            addUserTxtMsg.setVisible(true);
            addUserTxtMsg.setTextFill(Paint.valueOf(manager.greenColour));
            addUserTxtMsg.setText("Accepted username");
        }
    }

    // Validate email while typing
    @FXML
    protected void validateEmail() {
        if (emailTxtAdd.getText().isEmpty()) {
            addEmailTxtMsg.setVisible(true);
            addEmailTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addEmailTxtMsg.setText("Email is required, please fill it!");
        } else if (!emailTxtAdd.getText().matches(manager.emailPattren)) {
            addEmailTxtMsg.setVisible(true);
            addEmailTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addEmailTxtMsg.setText("Email must contain @ + .[com|org|etc]");
        } else {
            addEmailTxtMsg.setVisible(true);
            addEmailTxtMsg.setTextFill(Paint.valueOf(manager.greenColour));
            addEmailTxtMsg.setText("Accepted email");
        }
    }

    // Validate password while typing
    @FXML
    protected void validatePassword() {
        if (passwordTxtAdd.getText().isEmpty()) {
            addPassTxtMsg.setVisible(true);
            addPassTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addPassTxtMsg.setText("Password is required, please fill it!");
        } else if (!passwordTxtAdd.getText().matches(manager.passPattren)) {
            addPassTxtMsg.setVisible(true);
            addPassTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addPassTxtMsg.setText("Week password, mix among symbels, characters, and numbers!");
        } else {
            addPassTxtMsg.setVisible(true);
            addPassTxtMsg.setTextFill(Paint.valueOf(manager.greenColour));
            addPassTxtMsg.setText("Accepted password");
        }
    }

    @FXML
    protected void validateJob() {
        if (jobTxtAdd.getSelectionModel().isEmpty()) {
            addJobTxtMsg.setVisible(true);
            addJobTxtMsg.setTextFill(Paint.valueOf(manager.redColour));
            addJobTxtMsg.setText("Job is required, please select one!");
        } else {
            addJobTxtMsg.setVisible(true);
            addJobTxtMsg.setTextFill(Paint.valueOf(manager.greenColour));
            addJobTxtMsg.setText("Accepted Job");
        }
    }
    
    // Validating users inputs
    private void validateUserData() {
        // Username Cases
        validateUsername();
        // Email Cases
        validateEmail();
        // Job cases
        validateJob();
        // Password Cases
        validatePassword();
    }

    // Set "Password@1234" as default password
    @FXML
    protected void setDefaultPassword() {
        if (passwordTxtAdd.getText().isEmpty()) {
            passwordTxtAdd.setText("Password@1234");
            validatePassword();
        } else {
            passwordTxtAdd.setText("");
            validatePassword();
        }
    }

    // Searching users table to get specific user recorder
    @FXML
    protected void findUser() {
        try {
            if (search.getText().isEmpty()) {
                fillTableData(sql);
            } else {
                fillTableData(sql + "WHERE `username` LIKE '%" + search.getText().trim() + "%' OR `email` LIKE '%" + search.getText().trim() + "%'");
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Reseting messages labels
    private void resetMsgLables() {
        addUserTxtMsg.setText("");
        addEmailTxtMsg.setText("");
        addJobTxtMsg.setText("");
        addPassTxtMsg.setText("");
        userTxtAdd.setText("");
        emailTxtAdd.setText("");
        jobTxtAdd.getSelectionModel().select("");
        passwordTxtAdd.setText("");
        addShowBox.setSelected(false);
    }

    // Filling the required combo boxs
    private void fillComboBox(JFXComboBox box) {
        box.getItems().addAll("Admin", "Cashier");
    }

    // Create Table columns
    private void createTableColumns() {
        userCol = new JFXTreeTableColumn("Username");
        userCol.setPrefWidth(140);
        userCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.UserModel, String> param) -> {
            return param.getValue().getValue().username;
        });
        jobCol = new JFXTreeTableColumn("Job\nTitle");
        jobCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.UserModel, String> param) -> {
            return param.getValue().getValue().job;
        });
        emailCol = new JFXTreeTableColumn("Email");
        emailCol.setPrefWidth(170);
        emailCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.UserModel, String> param) -> {
            return param.getValue().getValue().email;
        });
        b_emailCol = new JFXTreeTableColumn("Backup\nEmail");
        b_emailCol.setPrefWidth(170);
        b_emailCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.UserModel, String> param) -> {
            return param.getValue().getValue().b_email;
        });
        phoneCol = new JFXTreeTableColumn("Telephone");
        phoneCol.setPrefWidth(98);
        phoneCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.UserModel, String> param) -> {
            return param.getValue().getValue().phone;
        });
        createdCol = new JFXTreeTableColumn("Memeber\nSince");
        createdCol.setPrefWidth(140);
        createdCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.UserModel, String> param) -> {
            return param.getValue().getValue().created_at;
        });
        table.getColumns().addAll(userCol, jobCol, emailCol, b_emailCol, phoneCol, createdCol);
        try {
            con = com.restro.database.ConnectDB.getConnection();
            fillTableData(sql);
        } catch (SQLException ex) {
            Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Fetching the data from the database
    private void fillTableData(String sql) throws SQLException {
        usersList = FXCollections.observableArrayList();
        TreeItem<com.restro.models.admins.UserModel> root = new RecursiveTreeItem<>(usersList, RecursiveTreeObject::getChildren);
        root.getChildren().clear();
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
            usersList.add(new com.restro.models.admins.UserModel(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getString("job"),
                    rs.getString("email"),
                    rs.getString("b_email"),
                    rs.getString("phone"),
                    rs.getString("created_at")
            ));
            table.setShowRoot(false);
            table.setRoot(root);
            table.setRowFactory(e -> {
                JFXTreeTableRow<com.restro.models.admins.UserModel> row = new JFXTreeTableRow<>();
                row.setOnMouseClicked(event -> {
                    if( row.getItem() == null ) {
                        Messages.warning_message("Users Selection", "Select user recorder in order to delete it's data");
                    } else {
                        Messages.confirmation_messaeg("Deleting User '" + row.getItem().getUsername() + "'", "Are you sure you want to delete this user, this user have direct impact at your system!!");
                        if (Messages.alert.getResult() == ButtonType.YES) {
                            try {
                                st = con.createStatement();
                                if (st.executeUpdate("DELETE FROM `users` WHERE `id` = '" + row.getItem().getID() + "'") == 1) {
                                    Messages.information_message("Deleting Users", "Successfully deleting user '" + row.getItem().getUsername() + "'");
                                    fillTableData(sql);
                                    getLatestUsers();
                                } else {
                                    Messages.error_message("Deleting Users", "Unable to delete user '" + row.getItem().getUsername() + "', please contact the developer!");
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(UsersController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            Messages.information_message("Deleting Users", "You have cancel the deleting process successfully!");
                        }
                    }
                });
                return row;
            });
        }
    }
    
    // Fill latest users
    private void getLatestUsers() {
        try {
            latestUsers.getChildren().clear();
            con = com.restro.database.ConnectDB.getConnection();
            ps  = con.prepareStatement("SELECT `profile`, `username`, `created_at` FROM `users` ORDER BY `id` DESC LIMIT 2");
            rs  = ps .executeQuery();
            while( rs.next() ) {
                HBox parent = new HBox();
                VBox childs = new VBox();
                Circle profile = new Circle();
                
                profile.setFill(new ImagePattern(new Image("file:Actors\\"+rs.getString("profile"))));
                profile.getStyleClass().add("danger-shadow");
                profile.setRadius(40);
                
                childs.setPadding(new Insets(10, 0, 10, 0));
                childs.getChildren().addAll( new Label(rs.getString("username")), new Label("Member Since "+rs.getString("created_at")) );
                
                parent.setPadding(new Insets(10, 0, 10, 0));
                parent.getChildren().addAll(profile, childs);
                
                if ( rs.isLast() ) {
                    latestUsers.getChildren().addAll(parent);
                } else {
                    latestUsers.getChildren().addAll(parent, new Separator(Orientation.HORIZONTAL));
                }
            }
        } catch ( SQLException ex ) {
            Messages.error_message("Server Error", "Unable to get latest users data, please contact the developer?\n\nServer Error: "+ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize user table columns
        createTableColumns();

        // Fill add combo box
        fillComboBox(jobTxtAdd);
        
        // Fill Latest Users
        getLatestUsers();
        
    }

}
