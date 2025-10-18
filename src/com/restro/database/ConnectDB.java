package com.restro.database;

import com.restro.messages.Messages;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    
    private final static String url = "jdbc:mysql://localhost/delorest?autoReconnect=true&useSSL=true&requireSSL=true&verifyServerCertificate=false&zeroDateTimeBehavior=convertToNull";
    private final static String username = "root";
    private final static String password = "";
    private final com.restro.manager.Manager manager = new com.restro.manager.Manager();
    
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection(url, username, password);
            if( !con.equals(con) ) {
                con.close();
            } else {
                System.out.println("connection done");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Messages.error_message("Server Down", "Unable to connect the server, please check if you open connection with the server or not?!\n\n"
                                                          + " If the connection is open and not working please contact the developer?");
        }
        return con;
    }
    
}
