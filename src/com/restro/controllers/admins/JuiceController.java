package com.restro.controllers.admins;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class JuiceController implements Initializable {

    // Necessary variables
    private Connection con = null;
    private PreparedStatement ps = null;
    private Statement st = null;
    private ResultSet rs = null;
    private File file;
    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    private JFXTreeTableColumn<com.restro.models.admins.JuicesModel, String> imgCol, nameCol, typeCol, priceCol, descriptionCol, createdCol, updatedCol;
    private ObservableList<com.restro.models.admins.JuicesModel> juicesList;
    private final String sql = "SELECT * FROM `juices`";
    private InputStream in;
    private OutputStream out;
    private final byte[] content = new byte[1024];
    private int size = 0;
    private final LocalDateTime dt = LocalDateTime.now();
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private Tab viewTab, addTabe, updateTabe;
    @FXML
    private Circle addImg, editImg;
    @FXML
    private JFXTextField search, hidden, imgAddTxt, imgEditTxt, nameAddTxt, nameEditTxt, priceAddTxt, priceEditTxt;
    @FXML
    private JFXComboBox typeAddTxt, typeEditTxt;
    @FXML
    private JFXTextArea descriptionAddTxt, descriptionEditTxt;
    @FXML
    private Label imgAddMsg, imgEditMsg, nameAddMsg, nameEditMsg, priceAddMsg, priceEditMsg, typeAddMsg, typeEditMsg;
    @FXML
    private JFXTreeTableView<com.restro.models.admins.JuicesModel> table;
    @FXML
    private VBox addLatestJuices, editLatestJuices;
    
    // Print juices menu list
    @FXML
    protected void printJuicesMenu() {
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement("SELECT `name` FROM `reports` WHERE `type` = 'juicesMenu'");
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
                    Messages.error_message("Server Error", "Unable to extract juices menu, please contact the developer?\n\nServer Error: "+ex);
                }
            } else {
                Messages.error_message("Report Failue", "The trageting report is not exsisting in the server!");   
            }
        } catch (SQLException | IOException ex) {
            Messages.error_message("Server Error", "Unable to extract juices menu, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Creating new juices
    @FXML
    protected void createJuices() {
        if( nameAddTxt.getText().isEmpty() || !nameAddTxt.getText().matches(manager.charsAndNumPattren) || priceAddTxt.getText().isEmpty() ||
            !priceAddTxt.getText().matches(manager.numsOnlyPattren) || typeAddTxt.getSelectionModel().isEmpty() ) {
            validateAddFormData();
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("SELECT `name` FROM `juices` WHERE `name` = '"+nameAddTxt.getText().trim()+"' AND `type` = '"+typeAddTxt.getSelectionModel().getSelectedItem()+"'");
                rs = ps.executeQuery();
                if( rs.next() ) {
                    Messages.error_message("Juice Adding Error", "Juice " + nameAddTxt.getText().trim() + " with type " + typeAddTxt.getSelectionModel().getSelectedItem() + " already exsisting, please enter a different juice name!");
                } else {
                    if( file == null ) {
                        Messages.error_message("Juice Image Error", "Please choose juice image");
                    } else {
                        ps = con.prepareStatement("INSERT INTO `juices` (`image`, `name`, `type`, `price`, `description`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        ps.setString(1, file.getName());
                        ps.setString(2, nameAddTxt.getText().trim());
                        ps.setString(3, typeAddTxt.getSelectionModel().getSelectedItem().toString());
                        ps.setString(4, priceAddTxt.getText().trim());
                        ps.setString(5, descriptionAddTxt.getText().trim());
                        ps.setString(6, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                        ps.setString(7, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                        int status = ps.executeUpdate();
                        if( status > 0 ) {
                            in = new FileInputStream(file.getAbsolutePath());
                            out = new FileOutputStream("Juices\\"+file.getName());
                            while( ( size = in.read(content) ) != -1 ) {
                                out.write(content);
                            }
                            out.close();
                            in.close();
                            Messages.information_message("Adding Juices", "Juice " + nameAddTxt.getText().trim() + " data inserted successfully");
                            fillTableData(sql);
                            resetAddFormData();
                            getLatestAddJuices();
                            getLatestEditJuices();
                        } else {
                            Messages.error_message("Adding Juices", "Unable to juice " + nameAddTxt.getText().trim() + " data, please contact the developer!");
                        }
                    }
                    
                }
            } catch (SQLException | IOException ex) {
                Messages.error_message("Server Error", "Unable to add juice data, please contact the developer!\nServer Error: "+ex);
            }
        }
    }
    
    // Updating selected juice
    @FXML
    protected void updateJuice() {
        if( hidden.getText().isEmpty() ) {
            Messages.warning_message("Juice Update Warning", "Unable to update empty juices data, please select juice record from the juices table!!");
        } else {
            if( nameEditTxt.getText().isEmpty() || !nameEditTxt.getText().matches(manager.charsAndNumPattren) || priceEditTxt.getText().isEmpty() ||
                !priceEditTxt.getText().matches(manager.numsOnlyPattren) || typeEditTxt.getSelectionModel().isEmpty() ) {
                validateEditFormData();
            } else {
                try {
                    con = com.restro.database.ConnectDB.getConnection();
                    ps = con.prepareStatement("SELECT * FROM `juices` WHERE `name` = '"+nameEditTxt.getText().trim()+"' AND `type` = '"+typeEditTxt.getSelectionModel().toString().trim()+"'");
                    rs = ps.executeQuery();
                    if( rs.next() ) {
                        Messages.error_message("Updating Exsisting Juice Data", "Juice with name "+nameEditTxt.getText().trim()+" and type of "+typeEditTxt.getSelectionModel().toString().trim()+" aleardy exsisting, you can't update exsisting data!");
                    } else {
                        if( file == null ) {
                            try {
                                st = con.createStatement();
                                if( st.executeUpdate("UPDATE `juices` SET `name` = '"+nameEditTxt.getText().trim()+"', `type` = '"+typeEditTxt.getSelectionModel().getSelectedItem()+"'"
                                        + ", `price` = '"+priceEditTxt.getText().trim()+"', `description` = '"+descriptionEditTxt.getText().trim()+"'"
                                                + ", `updated_at` = '"+String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear())+"' WHERE `id` = '"+hidden.getText().trim()+"'") == 1 ) {
                                    Messages.information_message("Updating Juices without File", "Juice "+nameEditTxt.getText().trim()+" data has been successfully updated.");
                                    cancelUpdate();
                                    fillTableData(sql);
                                    getLatestAddJuices();
                                    getLatestEditJuices();
                                } else {
                                    Messages.error_message("Updating Juices Without File", "Unable to update juice "+nameEditTxt.getText().trim()+" data, please contact the developer?!");
                                }
                            } catch (SQLException ex) {
                                Messages.error_message("Update Juices without File", "Unable to update juice data, please contact the developer\n\nServer Error: "+ex);
                            }
                        } else {
                            try {
                                ps = con.prepareStatement("UPDATE `juices` SET `image` = ?, `name` = ?, `type` = ?, `price` = ?, `description` = ?, `updated_at` = ? WHERE `id` = '"+hidden.getText().trim()+"'");
                                ps.setString(1, file.getName().trim());
                                ps.setString(2, nameEditTxt.getText().trim());
                                ps.setString(3, typeEditTxt.getSelectionModel().getSelectedItem().toString().trim());
                                ps.setString(4, priceEditTxt.getText().trim());
                                ps.setString(5, descriptionEditTxt.getText().trim());
                                ps.setString(6, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                                int status = ps.executeUpdate();
                                if( status > 0 ) {
                                    in = new FileInputStream(file.getAbsolutePath());
                                    out = new FileOutputStream("Juices\\"+file.getName());
                                    while( ( size = in.read(content) ) != -1 ) {
                                        out.write(content);
                                    }
                                    out.close();
                                    in.close();
                                    Messages.information_message("Updating Juices With File", "Juice "+nameEditTxt.getText().trim()+" data successfully been updated.");
                                    cancelUpdate();
                                    fillTableData(sql);
                                    getLatestAddJuices();
                                    getLatestEditJuices();
                                } else {
                                    Messages.error_message("Updating Juices with file", "Unable to update juice "+nameEditTxt.getText().trim()+" data, please contact the developer?!");
                                }
                            } catch (SQLException | IOException ex) {
                                Messages.error_message("Update Juices with File", "Unable to update juice data, please contact the developer\n\nServer Error: "+ex);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JuiceController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    // Deleting selected juice
    @FXML
    protected void deleteJuice() {
        if( hidden.getText().isEmpty() ) {
            Messages.warning_message("Juice Update Warning", "Unable to delete empty juices data, please select juice record from the juices table!!");
        } else {
            Messages.confirmation_messaeg("Deleting Juices", "Are you sure you want to delete juice "+nameEditTxt.getText()+" data?");
            if ( Messages.alert.getResult() == ButtonType.YES ) {
                try {
                    con = com.restro.database.ConnectDB.getConnection();
                    st = con.createStatement();
                    if( st.executeUpdate("DELETE FROM `juices` WHERE `id` = '"+hidden.getText().trim()+"'") == 1 ) {
                        Messages.information_message("Deleting Juices", "Successfully deleted juice " + nameEditTxt.getText().trim() + " data.");
                        cancelUpdate();
                        fillTableData(sql);
                        getLatestAddJuices();
                        getLatestEditJuices();
                    } else {
                        Messages.error_message("Deleting Juices", "Unable to delete juice " + nameEditTxt.getText().trim() + " data, please contact the developer?!");
                    }
                } catch (SQLException ex) {
                    Messages.error_message("Server Error", "Unable to delete juices data, please contact the developer\n\nServer Error: "+ex);
                }
            } else {
                Messages.information_message("Cancel Juices Deleting", "Deleting juice "+nameEditTxt.getText()+" canceled.");
            }
        }
    }
    
    // Cancel updating juice data
    @FXML
    protected void cancelUpdate() {
        tabPane.getSelectionModel().select(viewTab);
        resetEditFormData();
    }
    
    // Select juice image from local files
    @FXML
    protected void selectJuiceImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose juice image to upload");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        file = fc.showOpenDialog(new Stage());
        if(file != null) {
            if(tabPane.getSelectionModel().isSelected(1)) {
                imgAddTxt.setText(file.getName());
            } else {
                imgEditTxt.setText(file.getName());
            }
        } else {
            Messages.warning_message("Juice Image Selection", "You haven't choose any image");
        }
    }
    
    // Upload selected image for add form
    @FXML
    protected void uploadImgAdd() {
        if( file != null ) {
            try {
                String path = file.toURI().toURL().toString();
                addImg.setFill(new ImagePattern(new Image(path)));
            } catch (MalformedURLException ex) {
                Messages.error_message("Server Error", "Unable to upload juices image, please contact the developer?\n\nServer Error: "+ex);
            }
        } else {
            Messages.warning_message("Upload Juice Image", "You haven't choosing any image to upload");
        }
    }
    
    // Upload selected image for edit form
    @FXML
    protected void uploadImgEdit() {
        if( file != null ) {
            try {
                String path = file.toURI().toURL().toString();
                editImg.setFill(new ImagePattern(new Image(path)));
            } catch (MalformedURLException ex) {
                Logger.getLogger(JuiceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Messages.warning_message("Upload Juice Image", "You haven't choosing any image to upload");
        }
    }
    
    // Searching to find specific juice from juices table
    @FXML
    protected void findJuice() {
        try {
            if (search.getText().isEmpty()) {
                fillTableData(sql);
            } else {
                fillTableData(sql + "WHERE `name` LIKE '%"+search.getText().trim()+"%' OR `type` LIKE '%"+search.getText().trim()+"%' OR `price` LIKE '%"+search.getText().trim()+"%'");
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to search in juices table\nServer Error: "+ex);
        }
    }
    
    // Validate the juice name
    @FXML
    protected void validateName() {
        if(tabPane.getSelectionModel().isSelected(1)) {
            if( nameAddTxt.getText().isEmpty() ) {
                nameAddMsg.setVisible(true);
                nameAddMsg.setText("Juice name is required, please fill it!");
                nameAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if(!nameAddTxt.getText().matches(manager.charsAndNumPattren)) {
                nameAddMsg.setVisible(true);
                nameAddMsg.setText("Juice name accepts small/capital letters, whitespaces, and numbers only!");
                nameAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                nameAddMsg.setVisible(true);
                nameAddMsg.setText("Juice name is acceptable");
                nameAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        } else {
            if( nameEditTxt.getText().isEmpty() ) {
                nameEditMsg.setVisible(true);
                nameEditMsg.setText("Juice name is required, please fill it!");
                nameEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if(!nameEditTxt.getText().matches(manager.charsAndNumPattren)) {
                nameEditMsg.setVisible(true);
                nameEditMsg.setText("Juice name accepts small/capital letters, whitespaces, and numbers only!");
                nameEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                nameEditMsg.setVisible(true);
                nameEditMsg.setText("Juice name is acceptable");
                nameEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        }
    }
    
    // Validate the juice price
    @FXML
    protected void validatePrice() {
        if ( tabPane.getSelectionModel().isSelected(1) ) {
            if ( priceAddTxt.getText().isEmpty() ) {
                priceAddMsg.setVisible(true);
                priceAddMsg.setText("Juice price is required, please fill it!");
                priceAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if( !priceAddTxt.getText().matches(manager.numsOnlyPattren) ) {
                priceAddMsg.setVisible(true);
                priceAddMsg.setText("Juice price accepts only numbers!");
                priceAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                priceAddMsg.setVisible(true);
                priceAddMsg.setText("Juice price is Acceptable");
                priceAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        } else {
            if ( priceEditTxt.getText().isEmpty() ) {
                priceEditMsg.setVisible(true);
                priceEditMsg.setText("Juice price is required, please fill it!");
                priceEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if( !priceEditTxt.getText().matches(manager.numsOnlyPattren) ) {
                priceEditMsg.setVisible(true);
                priceEditMsg.setText("Juice price accepts only numbers!");
                priceEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                priceEditMsg.setVisible(true);
                priceEditMsg.setText("Juice price is Acceptable");
                priceEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        }
    }
    
    // Validate the juice type
    @FXML
    protected void validateType() {
        if ( tabPane.getSelectionModel().isSelected(1) ) {
            if ( typeAddTxt.getSelectionModel().isEmpty() ) {
                typeAddMsg.setVisible(true);
                typeAddMsg.setText("Juice type is required, please select one!");
                typeAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                typeAddMsg.setVisible(true);
                typeAddMsg.setText("Juice type is Acceptable");
                typeAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        } else {
            if ( typeEditTxt.getSelectionModel().isEmpty() ) {
                typeEditMsg.setVisible(true);
                typeEditMsg.setText("Juice type is required, please select one!");
                typeEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                typeEditMsg.setVisible(true);
                typeEditMsg.setText("Juice type is Acceptable");
                typeEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        }
    }
    
    // Validating add form
    private void validateAddFormData() {
        // Validating juice name
        validateName();
        // Validating juice price
        validatePrice();
        // Validating juice type
        validateType();
    }
    
    // Validating edit form
    private void validateEditFormData() {
        // Validating juice name
        validateName();
        // Validating juice price
        validatePrice();
        // Validating juice type
        validateType();
    }
    
    // Creating and initialize juice table columns
    private void createTableColumns() {
        imgCol = new JFXTreeTableColumn("Juice\nImage");
        imgCol.setPrefWidth(100);
        imgCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().image;
        });
        nameCol = new JFXTreeTableColumn("Juice\nName");
        nameCol.setPrefWidth(146);
        nameCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().name;
        });
        typeCol = new JFXTreeTableColumn("Juice\nType");
        typeCol.setPrefWidth(81);
        typeCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().type;
        });
        priceCol = new JFXTreeTableColumn("Juice\nPrice");
        priceCol.setPrefWidth(81);
        priceCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().price;
        });
        descriptionCol = new JFXTreeTableColumn("Juice\nDescription");
        descriptionCol.setPrefWidth(200);
        descriptionCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().description;
        });
        createdCol = new JFXTreeTableColumn("Add\nSince");
        createdCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().created_at;
        });
        updatedCol = new JFXTreeTableColumn("Last\nUpdate");
        updatedCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.JuicesModel, String> param) -> {
            return param.getValue().getValue().updated_at;
        });
        table.getColumns().addAll(nameCol, typeCol, priceCol, descriptionCol, createdCol, updatedCol);
        try {
            con = com.restro.database.ConnectDB.getConnection();
            fillTableData(sql);
        } catch(SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch juices data from the database\nServer Error: "+ex);
        }
    }
    
    // Fetching juice data from the database
    private void fillTableData( String sql ) throws SQLException {
        juicesList = FXCollections.observableArrayList();
        TreeItem<com.restro.models.admins.JuicesModel> root = new RecursiveTreeItem<>(juicesList, RecursiveTreeObject::getChildren);
        root.getChildren().clear();
        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();
        while( rs.next() ) {
            juicesList.add(new com.restro.models.admins.JuicesModel(
                    rs.getString("id"), 
                    rs.getString("image"), 
                    rs.getString("name"), 
                    rs.getString("type"), 
                    rs.getString("price"), 
                    rs.getString("description"), 
                    rs.getString("created_at"), 
                    rs.getString("updated_at")
            ));
        }
        table.setShowRoot(false);
        table.setRoot(root);
        table.setRowFactory(ev -> {
            JFXTreeTableRow<com.restro.models.admins.JuicesModel> row = new JFXTreeTableRow<>();
            row.setOnMouseClicked(e -> {
                if( row.getItem() == null ) {
                    Messages.warning_message("Juices Selection", "Unable to select empty recorder");
                } else {
                    tabPane.getSelectionModel().select(updateTabe);
                    hidden.setText(row.getItem().getID());
                    editImg.setFill(new ImagePattern(new Image("file:Juices\\"+row.getItem().getImage())));
                    nameEditTxt.setText(row.getItem().getName());
                    typeEditTxt.getSelectionModel().select(row.getItem().getType());
                    priceEditTxt.setText(row.getItem().getPrice());
                    descriptionEditTxt.setText(row.getItem().getDescription());
                }
            });
            return row;
        });
    }
    
    // Reset the adding form
    private void resetAddFormData() {
        nameAddTxt.setText("");        typeAddTxt.getSelectionModel().select(null);
        nameAddMsg.setText("");        typeAddMsg.setText("");
        priceAddTxt.setText("");       priceAddMsg.setText("");
        descriptionAddTxt.setText(""); imgAddTxt.setText("");
        addImg.setFill(Paint.valueOf(manager.whiteSmokeColour));
    }

    // Reset the editing form
    private void resetEditFormData() {
        nameEditTxt.setText("");        typeAddTxt.getSelectionModel().select(null);
        nameEditMsg.setText("");        typeEditMsg.setText("");        
        priceEditTxt.setText("");       priceEditMsg.setText("");
        descriptionEditTxt.setText(""); imgEditTxt.setText("");
        editImg.setFill(Paint.valueOf(manager.whiteSmokeColour));
    }    
    
    // Get Latest added foods
    private void getLatestAddJuices() {
        fillLatestJuices("Added Since ", "created_at", addLatestJuices);
    }
    
    // Get Latest edit foods
    private void getLatestEditJuices() {
        fillLatestJuices("Last Update ", "updated_at", editLatestJuices);
    }
    
    // Fill Latest added or updated foods
    private void fillLatestJuices( String status, String columnName, VBox targetView ) {
        targetView.getChildren().clear();
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps  = con.prepareStatement("SELECT `image`, `name`, `price`, "+columnName+" FROM `juices` ORDER BY `id` DESC LIMIT 4");
            rs  = ps.executeQuery();
            while ( rs.next() ) {
                
                HBox parent     = new HBox();
                HBox subChilds  = new HBox();
                VBox childs     = new VBox();
                Circle image    =  new Circle();
                
                image.setFill(new ImagePattern(new Image("file:Juices\\"+rs.getString("image"))));
                image.setRadius(40);
                image.getStyleClass().add("danger-shadow");
                
                subChilds.setPadding(new Insets(2, 0, 5, 0));
                subChilds.getChildren().addAll( new Label(rs.getString("name")), new Label("  "), new Label(rs.getString("price")+ " SD") );
                
                childs.setPadding(new Insets(0, 0, 0, 10));
                childs.getChildren().addAll( subChilds, new Label(status+rs.getString(columnName)) );
                
                parent.setPadding(new Insets(10, 0, 12, 0));
                parent.getChildren().addAll( image, childs );
                
                if ( rs.isLast() ) {
                    targetView.getChildren().addAll( parent );
                } else {
                    targetView.getChildren().addAll( parent, new Separator(Orientation.HORIZONTAL) );
                }
                
            }
        } catch ( SQLException ex ) {
            Messages.error_message("Server Error", "Unable to get latest foods data, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Initialize table data
        createTableColumns();
        
        // Fill type add, edit comboboxs data
        typeAddTxt.getItems().addAll("Cold", "Hot");
        typeEditTxt.getItems().addAll("Cold", "Hot");
        
        // get Latest added juices
        getLatestAddJuices();
        
        // get Latest Edit juices
        getLatestEditJuices();
        
    }    
    
}
