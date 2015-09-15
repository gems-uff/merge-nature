/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import java.sql.SQLException;

/**
 *
 * @author gleiph
 */
public class ConflictingFileJDBCDAO {

    public static final String ID = "id";
    public static final String FILETYPE = "filetype";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String REMOVED = "removed";

    public static final String REVISION_ID = "revision_id";

    public Long insert(ConflictingFile conflictingFile, Long revisionId) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.CONFLICTING_FILE
                + "("
                + FILETYPE + ", "
                + NAME + ", "
                + PATH + ", "
                + REMOVED + ", "
                + REVISION_ID
                + ") "
                + "VALUES(\'"
                + conflictingFile.getFileType() + "\', \'"
                + conflictingFile.getName() + "\', \'"
                + conflictingFile.getPath() + "\', \'"
                + conflictingFile.isRemoved() + "\', \'"
                + revisionId
                + "\')";

        return DefaultOperations.insert(insertSQL);

    }

    public Long insertAll(ConflictingFile conflictingFile, Long revisionId) throws SQLException {

        long conflictingFile_id = this.insert(conflictingFile, revisionId);

        //Adding conflicting chunks
        ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO();

        for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
            conflictingChunkDAO.insertAll(conflictingChunk, conflictingFile_id);
        }

        return conflictingFile_id;

    }
}
