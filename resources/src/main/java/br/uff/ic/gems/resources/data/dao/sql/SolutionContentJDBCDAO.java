/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import java.sql.SQLException;

/**
 *
 * @author gleiph
 */
public class SolutionContentJDBCDAO {

    public static final String ID = "id";
    public static final String CONTENT = "content";

    public static final String CONFLICTING_CHUNK_ID = "conflictingchunk_id";

    public Long insert(String content, Long conflictingChunkId) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.SOLUTION_CONTENT
                + "("
                + CONTENT + ", "
                + CONFLICTING_CHUNK_ID
                + ") "
                + "VALUES(?, ?)";

        return DefaultOperations.insertContent(insertSQL, content, conflictingChunkId);

    }

    public Long insertAll(String content, Long conflictingChunkId) throws SQLException {
        return this.insert(content, conflictingChunkId);
    }
}
