package com.restro.controllers.admins;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.restro.messages.Messages;
import com.restro.models.admins.FoodsModel;
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
import javafx.scene.control.TabPane;
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

public class FoodController implements Initializable {

    // Necessary Variables
    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    private final String sql = "SELECT "
                                    + "`foods`.`id`, "
                                    + "`foods`.`image`, "
                                    + "`foods`.`name`, "
                                    + "`cates`.`category`, "
                                    + "`foods`.`price`, "
                                    + "`foods`.`description`, "
                                    + "`foods`.`created_at`, "
                                    + "`foods`.`updated_at` "
                                + "FROM "
                                    + "`foods`, "
                                    + "`cates` "
                                + "WHERE "
                                    + "`foods`.`cate_id` = `cates`.`id`";
    private Connection con = null;
    private Statement st = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private JFXTreeTableColumn<com.restro.models.admins.FoodsModel, String> imgCol, nameCol, cateCol, priceCol, descCol, createdCol, updatedCol;
    private ObservableList<com.restro.models.admins.FoodsModel> foodsList;
    private File file;
    private InputStream in;
    private OutputStream out;
    private final byte[] content = new byte[1024];
    private int size = 0;
    private final LocalDateTime dt = LocalDateTime.now();
    @FXML
    private JFXTreeTableView<com.restro.models.admins.FoodsModel> table;
    @FXML
    private JFXTextField search, hiddenCate, hiddenFood, cateAddTxt, cateEditTxt, addImgTxt, editImgTxt, nameAddTxt, nameEditTxt, priceAddTxt, priceEditTxt;
    @FXML
    private JFXTextArea cateAddDescTxt, cateEditDescTxt, descAddTxt, descEditTxt;
    @FXML
    private JFXComboBox cateBox, cateAddBox, cateEditBox;
    @FXML
    private Label cateAddMsg, cateEditMsg, imgAddMsg, imgEditMsg,nameAddMsg, nameEditMsg, priceAddMsg, priceEditMsg, cateFoodAddMsg, cateFoodEditMsg;
    @FXML
    private Circle addImg, editImg;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab viewTab, addTab, editTab;
    @FXML
    private VBox addLatestFoods, editLatestFoods;
    
    // Print juices menu list
    @FXML
    protected void printFoodsMenu() {
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement("SELECT `name` FROM `reports` WHERE `type` = 'foodsMenu'");
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
                    Messages.error_message("Server Error", "Unable to extract foods menu, please contact the developer?\n\nServer Error: "+ex);
                }
            } else {
                Messages.error_message("Report Failue", "The trageting report is not exsisting in the server!");   
            }
        } catch (SQLException | IOException ex) {
            Messages.error_message("Server Error", "Unable to extract foods menu, please contact the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Searching among food list
    @FXML
    protected void findFoods() {
        if( search.getText().isEmpty() ) {
            fillFoodTableData(sql);
        } else {
            fillFoodTableData(sql + " AND ( `foods`.`name` LIKE '%"+search.getText().trim()+"%' OR `foods`.`price` LIKE '%"+search.getText().trim()+"%' OR `cates`.`category` LIKE '%"+search.getText().trim()+"%' )");
        }
    }
    
    // Add new food categories
    @FXML
    protected void addCategories() {
        if( cateAddTxt.getText().isEmpty() || !cateAddTxt.getText().matches(manager.charsAndNumPattren) ) {
            validateCateAddData();
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("SELECT `category` FROM `cates` WHERE `category` = '"+cateAddTxt.getText().trim()+"'");
                rs = ps.executeQuery();
                if( rs.next() ) {
                    Messages.error_message("Foods Catedory Failure", "This category " + cateAddTxt.getText().trim() + " data aleardy exsisting, please enter different data.");
                } else {
                    ps = con.prepareStatement("INSERT INTO `cates` (`category`, `description`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?)");
                    ps.setString(1, cateAddTxt.getText().trim());
                    ps.setString(2, cateAddDescTxt.getText().trim());
                    ps.setString(3, LocalDateTime.now().toString());
                    ps.setString(4, LocalDateTime.now().toString());
                    int status = ps.executeUpdate();
                    if( status > 0 ) {
                        Messages.information_message("Food Category Success", "Successfully added foods category " + cateAddTxt.getText().trim() + " data.");
                        resetCateAddForm();
                        getCategoriesData();
                    } else {
                        Messages.error_message("Food Category Failure", "Unable to add foods category " + cateAddTxt.getText().trim() + " data?, please contact the developer!");
                    }
                }
            } catch(SQLException ex) {
                Messages.error_message("Server Error", "Unable to add category data, please contact the developer?!\n\nServer Error: "+ex);
            }
        }
    }
    
    // Edit food categories
    @FXML
    protected void editCategories() {
        if( cateBox.getSelectionModel().getSelectedItem() == null ) {
            Messages.warning_message("Edit Category Data", "You must select a category from categories select box in order to edit category");
        } else {
            if ( cateEditTxt.getText().isEmpty() || !cateEditTxt.getText().matches(manager.charsAndNumPattren) ) {
                validateCateEditData();
            } else {
                try {
                    con = com.restro.database.ConnectDB.getConnection();
                    String id = getCategoryID( cateBox );
                    st = con.createStatement();
                    if( st.executeUpdate("UPDATE `cates` SET `category` = '"+cateEditTxt.getText().trim()+"', `description` = '"+cateEditDescTxt.getText().trim()+"' WHERE `id` = '"+id+"'") == 1 ) {
                        Messages.information_message("Food Category Success", "Successfully updated category "+cateBox.getSelectionModel().getSelectedItem()+" to "+cateEditTxt.getText().trim());
                        getCategoriesData();
                        resetCateEditForm();
                    } else {
                        Messages.error_message("Food Category Failure", "Unable to update category "+cateBox.getSelectionModel().getSelectedItem()+" to "+cateEditTxt.getText().trim()+", please contact the developer!");
                    }
                } catch ( SQLException ex ) {
                    Messages.error_message("Server Error", "Unable to update food category data, please contact the developer?\n\nServer Error: "+ex);
                }
            }
        }
    }
    
    // Delete food categories
    @FXML
    protected void delCategories() {
        if( cateBox.getSelectionModel().getSelectedItem() == null ) {
            Messages.warning_message("Delete Category Data", "You must select a category from categories select box in order to delete category");
        } else {
            try {
                con = com.restro.database.ConnectDB.getConnection();
                String id = getCategoryID( cateBox );
                st = con.createStatement();
                if( st.executeUpdate("DELETE FROM `cates` WHERE `id` = '"+id+"'") == 1 ) {
                    Messages.information_message("Foods Category Success", "Successfully deleted category "+cateBox.getSelectionModel().getSelectedItem()+" data");
                    getCategoriesData();
                    resetCateEditForm();
                } else {
                    Messages.error_message("Foods Category failure", "Unable to delete category "+cateBox.getSelectionModel().getSelectedItem()+" data, please contact the developer!");
                }
            } catch ( SQLException ex ) {
                Messages.error_message("Server Error", "Unable to delete foods categories, please contact the developer?\n\nServer Error: "+ex);
            }
        }
    }
    
    // Cancel Categories updates
    @FXML
    protected void cancelCateUpdates() {
        if( cateBox.getSelectionModel().getSelectedItem() == null ) {
            Messages.warning_message("Cancel Category Data Update", "You must select a category from categories select box in order to cancel category update");
        } else {
            resetCateEditForm();
        }
    }
    
    // What happens when choosing cate from cates box for updating cate data
    @FXML
    protected void cateSelection() {
        if( cateBox.getSelectionModel().getSelectedItem() != null ) {
            cateEditTxt.setDisable(false);
            cateEditDescTxt.setDisable(false);
            try {
                con = com.restro.database.ConnectDB.getConnection();
                ps = con.prepareStatement("SELECT `category`, `description` FROM `cates` WHERE `category` = '"+cateBox.getSelectionModel().getSelectedItem()+"'");
                rs = ps.executeQuery();
                if( rs.next() ) {
                    cateEditTxt.setText(rs.getString("category"));
                    cateEditDescTxt.setText(rs.getString("description"));
                } else {
                    Messages.error_message("Foods Category Failure", "Unable to fetch foods category data, please contact the developer!");
                }
            } catch( SQLException ex ) {
                Messages.error_message("Server Error", "Unable to fetch foods category data, please contact the developer!\n\nServer Error: "+ex);
            }
        }
    }
    
    // Validate category add form
    @FXML
    protected void validateCateAddData() {
        
        if ( cateAddTxt.getText().isEmpty() ) {
            cateAddMsg.setVisible(true);
            cateAddMsg.setText("Foods category name is requird, please fill it!");
            cateAddMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if( !cateAddTxt.getText().matches(manager.charsAndNumPattren) ) {
            cateAddMsg.setVisible(true);
            cateAddMsg.setText("Only characters, whitespaces, and numbers allowed!");
            cateAddMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            cateAddMsg.setVisible(true);
            cateAddMsg.setText("Foods category name is acceptable");
            cateAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }

    }
    
    // Validate category edit form
    @FXML
    protected void validateCateEditData() {
        
        if ( cateEditTxt.getText().isEmpty() ) {
            cateEditMsg.setVisible(true);
            cateEditMsg.setText("Foods category name is requird, please fill it!");
            cateEditMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else if( !cateEditTxt.getText().matches(manager.charsAndNumPattren) ) {
            cateEditMsg.setVisible(true);
            cateEditMsg.setText("Only characters, whitespaces, and numbers allowed!");
            cateEditMsg.setTextFill(Paint.valueOf(manager.redColour));
        } else {
            cateEditMsg.setVisible(true);
            cateEditMsg.setText("Foods category name is acceptable");
            cateEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
        }

    }
    
    // Choose Foods Image File
    @FXML
    protected void chooseFoodsImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Food Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*.png", "*.jpg", "*.jpeg"));
        file = fc.showOpenDialog(new Stage());
        if( file == null ) {
            Messages.warning_message("Food Image Choosing", "You haven't selected any food image");
        } else {
            if( tabPane.getSelectionModel().isSelected(2) ) {
                addImgTxt.setText(file.getName());
            } else {
                editImgTxt.setText(file.getName());
            }
        }
    }
    
    // Upload selected food image
    @FXML
    protected void uploadFoodImg() {
        try {
            if( file == null ) {
                Messages.warning_message("Food Image Uploads", "Sorry you can't upload empty image data!");
            } else {
                String path = file.toURI().toURL().toString();
                if( tabPane.getSelectionModel().isSelected(2) ) {
                    addImg.setFill(new ImagePattern(new Image(path)));
                } else {
                    editImg.setFill(new ImagePattern(new Image(path)));
                }
            }
        } catch (MalformedURLException ex) {
            Messages.error_message("Server Error", "Unable to upload selected food image, please call the developer?\n\nServer Error: "+ex);
        }
    }
    
    // Adding new foods data
    @FXML
    protected void addFoods() {
        if ( nameAddTxt.getText().isEmpty() || !nameAddTxt.getText().matches(manager.charsAndNumPattren) || priceAddTxt.getText().isEmpty() || 
             !priceAddTxt.getText().matches(manager.numsOnlyPattren) || cateAddBox.getSelectionModel().isEmpty() ) {
            validateFoodsAddForm();
        } else {
            if ( file == null ) {
                Messages.warning_message("Food Image Selection", "Please select food image to proceed!");
            } else {
                try {
                    con = com.restro.database.ConnectDB.getConnection();
                    String id = getCategoryID( cateAddBox );
                    ps = con.prepareStatement("SELECT `name`, `cate_id` FROM `foods` WHERE `name` = '"+nameAddTxt.getText().trim()+"' AND `cate_id` = '"+id+"'");
                    rs = ps.executeQuery();
                    if( rs.next() ) {
                        Messages.error_message("Food Adding Error", "Foods with name "+nameAddTxt.getText().trim()+" and category "+cateAddBox.getSelectionModel().getSelectedItem()+" aleardy exsisting, please enter different food data!");
                    } else {
                        ps = con.prepareStatement("INSERT INTO `foods` (`image`, `name`, `cate_id`, `price`, `description`, `created_at`, `updated_at`) VALUES (?, ?, ?, ?, ?, ?, ?)");
                        ps.setString(1, file.getName().trim());
                        ps.setString(2, nameAddTxt.getText().trim());
                        ps.setString(3, id);
                        ps.setString(4, priceAddTxt.getText().trim());
                        ps.setString(5, descAddTxt.getText().trim());
                        ps.setString(6, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                        ps.setString(7, String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear()));
                        int status = ps.executeUpdate();
                        if ( status > 0 ) {
                            in = new FileInputStream(file);
                            out = new FileOutputStream("Foods\\"+file.getName().trim());
                            while ( ( size = in.read(content) ) != -1 ) {
                                out.write(content);
                            }
                            out.close();
                            in.close();
                            Messages.information_message("Food Data Success", "Successfully added food "+nameAddTxt.getText().trim()+" data.");
                            resetFoodAddForm();
                            fillFoodTableData(sql);
                            getLatestAddFoods();
                            getLatestEditFoods();
                        } else {
                            Messages.error_message("Food Data Failure", "Unable to add food "+nameAddTxt.getText().trim()+" data, please contact the developer!");
                        }
                    }
                } catch (SQLException | IOException ex) {
                    Messages.error_message("Server Error", "Unable to add foods data, please contact the developer?\n\nServer Error: "+ex);
                }
            }
        }
    }
    
    // Editing foods data
    @FXML
    protected void editFoods() {
        if( hiddenFood.getText().isEmpty() ) {
            Messages.warning_message("Food Update Warning", "Unable to edit empty foods data, please select food record from the foods table!!");
        } else {
            con = com.restro.database.ConnectDB.getConnection();
            String id = getCategoryID(cateEditBox);
            if( file == null ) {
                try {
                    st = con.createStatement();
                    if( st.executeUpdate("UPDATE `foods` SET `name` = '"+nameEditTxt.getText().trim()+"', `cate_id` = '"+id+"', "
                                         + "`price` = '"+priceEditTxt.getText().trim()+"', `description` = '"+descEditTxt.getText().trim()+"', "
                                         + "`updated_at` = '"+String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear())+"' WHERE `id` = '"+hiddenFood.getText().trim()+"'") == 1 ) {
                        Messages.information_message("Food Update Without File Success", "Successfully updated food "+nameEditTxt.getText().trim()+" data.");
                        cancelFoodUpdate();
                        fillFoodTableData(sql);
                        getLatestEditFoods();
                        getLatestAddFoods();
                    } else {
                        Messages.error_message("Food Update Without File Failure", "Unable to update food "+nameEditTxt.getText().trim()+" data, please contact the developer!");
                    }
                } catch (SQLException ex) {
                    Messages.error_message("Server Error Without File", "Unable to update foods data, please contact the developer?\n\nServer Error: "+ex);
                }
            } else {
                try {
                    st = con.createStatement();
                    if( st.executeUpdate("UPDATE `foods` SET `image` = '"+file.getName()+"', `name` = '"+nameEditTxt.getText().trim()+"', `cate_id` = '"+id+"', "
                                         + "`price` = '"+priceEditTxt.getText().trim()+"', `description` = '"+descEditTxt.getText().trim()+"', "
                                         + "`updated_at` = '"+String.valueOf(dt.getMonth()+" "+dt.getDayOfMonth()+", "+dt.getYear())+"' WHERE `id` = '"+hiddenFood.getText().trim()+"'") == 1 ) {
                        Messages.information_message("Food Update With File Success", "Successfully updated food "+nameEditTxt.getText().trim()+" data.");
                        in = new FileInputStream(file);
                        out = new FileOutputStream("Foods\\"+file.getName());
                        while( ( size = in.read(content) ) != -1 ) {
                            out.write(content);
                        }
                        out.close();
                        in.close();
                        cancelFoodUpdate();
                        fillFoodTableData(sql);
                        getLatestEditFoods();
                        getLatestAddFoods();
                    } else {
                        Messages.error_message("Food Update With File Failure", "Unable to update food "+nameEditTxt.getText().trim()+" data, please contact the developer!");
                    }
                } catch (SQLException | IOException ex) {
                    Messages.error_message("Server Error With File", "Unable to update foods data, please contact the developer?\n\nServer Error: "+ex);
                }
            }
        }
    }
    
    // Deleting foods data
    @FXML
    protected void delFoods() {
        if( hiddenFood.getText().isEmpty() ) {
            Messages.warning_message("Food Update Warning", "Unable to delete empty foods data, please select food record from the foods table!!");
        } else {
            Messages.confirmation_messaeg("Deleting Foods", "Are you sure you want to delete food "+nameEditTxt.getText()+" data");
            if( Messages.alert.getResult() == ButtonType.YES ) {
                try {
                    con = com.restro.database.ConnectDB.getConnection();
                    st = con.createStatement();
                    if( st.executeUpdate("DELETE FROM `foods` WHERE `id` = '"+hiddenFood.getText().trim()+"'") == 1 ) {
                        Messages.information_message("Foods Deleting Success", "Successfully deleted food "+nameEditTxt.getText().trim()+" data.");
                        cancelFoodUpdate();
                        fillFoodTableData(sql);
                        getLatestEditFoods();
                        getLatestAddFoods();
                    } else {
                        Messages.error_message("Foods Deleting Failure", "Unable to delete foods data, please contact the developer!");
                    }
                } catch (SQLException ex) {
                    Messages.error_message("Server Error", "Unable to delete foods data, please contact the developer?\n\nServer Error: "+ex);
                }
            } else {
                Messages.information_message("Cancel Foods Deleting", "Deleting food "+nameEditTxt.getText()+" canceled.");
            }

        }
    }
    
    // Cancel foods update
    @FXML
    protected void cancelFoodUpdate() {
        if( hiddenFood.getText().isEmpty() ) {
            Messages.warning_message("Food Update Warning", "You must choose food record from foods table to enable this option!!");   
        } else {
            resetFoodEditForm();
            tabPane.getSelectionModel().select(viewTab);
        }
    }
    
    // Validating foods names
    @FXML
    protected void validateFoodsNames() {
        if ( tabPane.getSelectionModel().isSelected(2) ) {
            if ( nameAddTxt.getText().isEmpty() ) {
                nameAddMsg.setVisible(true);
                nameAddMsg.setText("Food name is required, please fill it!");
                nameAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if ( !nameAddTxt.getText().matches(manager.charsAndNumPattren) ) {
                nameAddMsg.setVisible(true);
                nameAddMsg.setText("Only characters, whitespaces, and numbers allowed!");
                nameAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                nameAddMsg.setVisible(true);
                nameAddMsg.setText("Food name is acceptable");
                nameAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        } else {
            if ( nameEditTxt.getText().isEmpty() ) {
                nameEditMsg.setVisible(true);
                nameEditMsg.setText("Food name is required, please fill it!");
                nameEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if ( !nameEditTxt.getText().matches(manager.charsAndNumPattren) ) {
                nameEditMsg.setVisible(true);
                nameEditMsg.setText("Only characters, whitespaces, and numbers allowed!");
                nameEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                nameEditMsg.setVisible(true);
                nameEditMsg.setText("Food name is acceptable");
                nameEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        }
    }
    
    // Validating foods prices
    @FXML
    protected void validateFoodsPrices() {
        if ( tabPane.getSelectionModel().isSelected(2) ) {
            if ( priceAddTxt.getText().isEmpty() ) {
                priceAddMsg.setVisible(true);
                priceAddMsg.setText("Food price is required, please fill it!");
                priceAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if ( !priceAddTxt.getText().matches(manager.numsOnlyPattren) ) {
                priceAddMsg.setVisible(true);
                priceAddMsg.setText("Only numbers allowed!");
                priceAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                priceAddMsg.setVisible(true);
                priceAddMsg.setText("Food price is acceptable");
                priceAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        } else {
            if ( priceEditTxt.getText().isEmpty() ) {
                priceEditMsg.setVisible(true);
                priceEditMsg.setText("Food price is required, please fill it!");
                priceEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else if ( !priceEditTxt.getText().matches(manager.numsOnlyPattren) ) {
                priceEditMsg.setVisible(true);
                priceEditMsg.setText("Only numbers allowed!");
                priceEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                priceEditMsg.setVisible(true);
                priceEditMsg.setText("Food price is acceptable");
                priceEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        }
    }
    
    // Validating foods cates
    @FXML
    protected void validateFoodsCates() {
        if ( tabPane.getSelectionModel().isSelected(2) ) {
            if ( cateAddBox.getSelectionModel().isEmpty() ) {
                cateFoodAddMsg.setVisible(true);
                cateFoodAddMsg.setText("Food category is required, please fill it!");
                cateFoodAddMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                cateFoodAddMsg.setVisible(true);
                cateFoodAddMsg.setText("Food category is acceptable");
                cateFoodAddMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        } else {
            if ( cateEditBox.getSelectionModel().isEmpty() ) {
                cateFoodEditMsg.setVisible(true);
                cateFoodEditMsg.setText("Food category is required, please fill it!");
                cateFoodEditMsg.setTextFill(Paint.valueOf(manager.redColour));
            } else {
                cateFoodEditMsg.setVisible(true);
                cateFoodEditMsg.setText("Food category is acceptable");
                cateFoodEditMsg.setTextFill(Paint.valueOf(manager.greenColour));
            }
        }
    }
    
    // Validate all add form fields
    private void validateFoodsAddForm() {
        validateFoodsNames();
        validateFoodsPrices();
        validateFoodsCates();
    }
    
    // Fetching categories from database
    private void getCategoriesData() {
        cateBox.getItems().clear();
        cateAddBox.getItems().clear();
        cateEditBox.getItems().clear();
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement("SELECT `category` FROM `cates`");
            rs = ps.executeQuery();
            while( rs.next() ) {
                cateBox.getItems().addAll(rs.getString("category"));
                cateAddBox.getItems().addAll(rs.getString("category"));
                cateEditBox.getItems().addAll(rs.getString("category"));
            }
        } catch( SQLException ex ) {
            Messages.error_message("Server Error", "Unable to fetch foods categories data from database?\n\nServer Error: "+ex);
        }
    }
    
    // Fetching category id from database
    private String getCategoryID( JFXComboBox target ) {
        String id = "";
        try {
            ps = con.prepareStatement("SELECT `id` FROM `cates` WHERE `category` = '"+target.getSelectionModel().getSelectedItem()+"'");
            rs = ps.executeQuery();
            if( rs.next() ) {
                id = rs.getString("id");
            } else {
                Messages.error_message("Category ID Failure", "Unable to fetch category "+target.getSelectionModel().getSelectedItem()+" asscioated data, please contact the developer!");
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch category "+target.getSelectionModel().getSelectedItem()+" asscioated data, please contact the developer!\n\nServer Error: "+ex);
        }
        return id;
    }
    
    // Creating food table columns
    private void createFoodsTableColumns() {
        imgCol = new JFXTreeTableColumn("Food\nImage");
        imgCol.setPrefWidth(50);
        imgCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().image;
        });
        nameCol = new JFXTreeTableColumn("Food\nName");
        nameCol.setPrefWidth(140);
        nameCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().name;
        });
        cateCol = new JFXTreeTableColumn("Food\nCategory");
        cateCol.setPrefWidth(100);
        cateCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().category;
        });
        priceCol = new JFXTreeTableColumn("Food\nPrice");
        priceCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().price;
        });
        descCol = new JFXTreeTableColumn("Food\nDescription");
        descCol.setPrefWidth(215);
        descCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().description;
        });
        createdCol = new JFXTreeTableColumn("Add At");
        createdCol.setPrefWidth(80);
        createdCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().created_at;
        });
        updatedCol = new JFXTreeTableColumn("Last\nUpdate");
        updatedCol.setPrefWidth(80);
        updatedCol.setCellValueFactory((JFXTreeTableColumn.CellDataFeatures<com.restro.models.admins.FoodsModel, String> param) -> {
            return param.getValue().getValue().updated_at;
        });
        table.getColumns().addAll(nameCol, cateCol, priceCol, descCol, createdCol, updatedCol);
        fillFoodTableData(sql);
    }
    
    // Filling food table with data from database
    private void fillFoodTableData( String sql ) {
        try {
            foodsList = FXCollections.observableArrayList();
            TreeItem<com.restro.models.admins.FoodsModel> root = new RecursiveTreeItem<>(foodsList, RecursiveTreeObject::getChildren);
            root.getChildren().clear();
            con = com.restro.database.ConnectDB.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while( rs.next() ) {
                foodsList.add(new FoodsModel(
                        rs.getString("id"), 
                        rs.getString("image"), 
                        rs.getString("name"), 
                        rs.getString("category"), 
                        rs.getString("price"), 
                        rs.getString("description"), 
                        rs.getString("created_at"), 
                        rs.getString("updated_at")
                ));
                table.setShowRoot(false);
                table.setRoot(root);
                table.setRowFactory(rf -> {
                    JFXTreeTableRow<com.restro.models.admins.FoodsModel> row = new JFXTreeTableRow<>();
                    row.setOnMouseClicked(e -> {
                        if( row.getItem() == null ) {
                            Messages.warning_message("Select Food Data", "You can select empty food record, select food record to update it's data!");
                        } else {
                            tabPane.getSelectionModel().select(editTab);
                            hiddenFood.setText(row.getItem().getID());
                            editImg.setFill(new ImagePattern(new Image("file:Foods\\"+row.getItem().getImage())));
                            nameEditTxt.setText(row.getItem().getName());
                            priceEditTxt.setText(row.getItem().getPrice());
                            cateEditBox.getSelectionModel().select(row.getItem().getCategory());
                            descEditTxt.setText(row.getItem().getDescription());
                        }
                    });
                    return row;
                });
            }
        } catch (SQLException ex) {
            Messages.error_message("Server Error", "Unable to fetch food data from database.\n\nServer Error: " + ex);
        }
        
    }
    
    // Reseting category add form
    private void resetCateAddForm() {
        cateAddTxt.setText("");        cateAddDescTxt.setText("");
        cateAddMsg.setText("");
    }
    
    // Reseting category edit form
    private void resetCateEditForm() {
        cateEditTxt.setText("");                    cateEditDescTxt.setText("");
        cateEditTxt.setDisable(true);               cateEditDescTxt.setDisable(true);
        cateBox.getSelectionModel().select(null);   cateEditMsg.setText("");
    }
    
    // Reseting food add form
    private void resetFoodAddForm() {
        addImg.setFill(Paint.valueOf(manager.whiteSmokeColour));
        addImgTxt.setText("");        nameAddTxt.setText("");
        priceAddTxt.setText("");      descAddTxt.setText("");
        nameAddMsg.setText("");       priceAddMsg.setText("");
        cateAddMsg.setText("");       cateAddBox.getSelectionModel().select(null);
        cateFoodAddMsg.setText("");
    }
    
    // Reseting food edit form
    private void resetFoodEditForm() {
        editImg.setFill(Paint.valueOf(manager.whiteSmokeColour));
        editImgTxt.setText("");        nameEditTxt.setText("");
        priceEditTxt.setText("");      descEditTxt.setText("");
        nameEditMsg.setText("");       priceEditMsg.setText("");
        cateEditMsg.setText("");       cateEditBox.getSelectionModel().select(null);
        cateFoodEditMsg.setText("");
    }
    
    // Get Latest added foods
    private void getLatestAddFoods() {
        fillLatestFoods("Added since ", "created_at", addLatestFoods);
    }
    
    // Get Latest edit foods
    private void getLatestEditFoods() {
        fillLatestFoods("Last Update ", "updated_at", editLatestFoods);
    }
    
    // Fill Latest added or updated foods
    private void fillLatestFoods( String status, String columnName, VBox targetView ) {
        targetView.getChildren().clear();
        try {
            con = com.restro.database.ConnectDB.getConnection();
            ps  = con.prepareStatement("SELECT `image`, `name`, `price`, "+columnName+" FROM `foods` ORDER BY `id` DESC LIMIT 4");
            rs  = ps.executeQuery();
            while ( rs.next() ) {
                
                HBox parent     = new HBox();
                HBox subChilds  = new HBox();
                VBox childs     = new VBox();
                Circle image    =  new Circle();
                
                image.setFill(new ImagePattern(new Image("file:Foods\\"+rs.getString("image"))));
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
        
        // Initialize Food Table Columns and Data
        createFoodsTableColumns();
        
        // Fetching Foods Categories & Fill Categories Box
        getCategoriesData();
        
        // Fill latest added food
        getLatestAddFoods();
        
        // Fill latest edited food
        getLatestEditFoods();
        
    }    
    
}
