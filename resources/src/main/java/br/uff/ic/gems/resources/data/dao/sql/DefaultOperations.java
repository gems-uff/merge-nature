/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gleiph
 */
public class DefaultOperations {

    private static final boolean debug = false;

    public static Long insert(String insertSQL) throws SQLException {
        long id;

        if (debug) {
            System.out.println(insertSQL);
        }

        try (Connection connection = (new JDBCConnection()).getConnection(Tables.DATABASE);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertSQL, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("Invalid identifier during insertion.");
            }
        }

        return id;

    }

    public static Long insertContent(String insertSQL, String content, Long conflictingChunkId) throws SQLException {
        long id;

        try (Connection connection = (new JDBCConnection()).getConnection(Tables.DATABASE);
                PreparedStatement statement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, content);
            statement.setLong(2, conflictingChunkId);

            if (debug) {
                System.out.println(statement.toString());
            }
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getLong(1);
            } else {
                throw new SQLException("Invalid identifier during insertion.");
            }
        }

        return id;

    }
}
