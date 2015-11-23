/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ConflictingContentJDBCDAO {

    public static final String ID = "id";
    public static final String CONTENT = "content";

    public static final String CONFLICTING_CHUNK_ID = "conflictingchunk_id";

    private final Connection connection;

    public ConflictingContentJDBCDAO(Connection connection) {
        this.connection = connection;
    }

    public Long insert(String content, Long conflictingChunkId) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.CONFLICTING_CONTENT
                + "("
                + CONTENT + ", "
                + CONFLICTING_CHUNK_ID
                + ") "
                + "VALUES(?,?)";

        return DefaultOperations.insertContent(insertSQL, content, conflictingChunkId, connection);

    }

    public Long insertAll(String content, Long conflictingChunkId) throws SQLException {
        return this.insert(content, conflictingChunkId);
    }

    public List<String> selectByConflictingChunkId(Long conflictingChunkId) throws SQLException {
        List<String> lines = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.CONFLICTING_CONTENT
                + " WHERE " + CONFLICTING_CHUNK_ID + " = " + conflictingChunkId;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                String line = results.getString(CONTENT);

                lines.add(line);

            }
        }

        return lines;
    }

    public List<String> selectAllByConflictingChunkId(Long conflictingChunkId) throws SQLException {
        return this.selectByConflictingChunkId(conflictingChunkId);
    }
}
