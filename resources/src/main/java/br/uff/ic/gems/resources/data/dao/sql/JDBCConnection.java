/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class JDBCConnection {

    Connection connection = null;

    public Connection getConnection(String databaseName) throws SQLException {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JDBCConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + databaseName, "postgres",
                    "kraken");

//            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + databaseName, "gleiph",
//                    "ghiotto");
        }
        
        return connection;
    }
    
    
    public Connection getConnection(String databaseName, String port, String login, String password) throws SQLException {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(JDBCConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:"+ port+"/" + databaseName, login,
                    password);

//            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + databaseName, "gleiph",
//                    "ghiotto");
        }
        
        return connection;
    }
}
