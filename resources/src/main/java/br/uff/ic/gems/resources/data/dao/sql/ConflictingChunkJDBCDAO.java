/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import java.sql.SQLException;

/**
 *
 * @author gleiph
 */
public class ConflictingChunkJDBCDAO {

    public static final String ID = "id";
    public static final String BEGIN_LINE = "beginline";
    public static final String DEVELOPER_DECISION = "developerdecision";
    public static final String END_LINE = "endline";
    public static final String IDENTIFIER = "identifier";
    public static final String SEPARATOR_LINE = "separatorline";

    public static final String CONFLICTING_FILE_ID = "conflictingfile_id";

    public Long insert(ConflictingChunk conflictingChunk, Long conflictingFileId) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.CONFLICTING_CHUNK
                + "("
                + BEGIN_LINE + ", "
                + DEVELOPER_DECISION + ", "
                + END_LINE + ", "
                + IDENTIFIER + ", "
                + SEPARATOR_LINE + ", "
                + CONFLICTING_FILE_ID
                + ") "
                + "VALUES(\'"
                + conflictingChunk.getBeginLine() + "\', \'"
                + conflictingChunk.getDeveloperDecision().toString() + "\', \'"
                + conflictingChunk.getEndLine() + "\', \'"
                + conflictingChunk.getIdentifier() + "\', \'"
                + conflictingChunk.getSeparatorLine() + "\', \'"
                + conflictingFileId
                + "\')";

        return DefaultOperations.insert(insertSQL);

    }

    public Long insertAll(ConflictingChunk conflictingChunk, Long conflictingFileId) throws SQLException {

        Long conflictingChunkId = this.insert(conflictingChunk, conflictingFileId);

        KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO();
        SolutionContentJDBCDAO solutionContentDAO = new SolutionContentJDBCDAO();
        ConflictingContentJDBCDAO conflictingContentDAO = new ConflictingContentJDBCDAO();

        //persisting kinds of conflicts
        //Left
        kindConflictDAO.insertAll(conflictingChunk.getLeftKindConflict(), conflictingChunkId, Side.LEFT);
        //Right
        kindConflictDAO.insertAll(conflictingChunk.getRightKindConflict(), conflictingChunkId, Side.RIGHT);

        //Persisting contents
        //Solution 
        for (String solutionContent : conflictingChunk.getSolutionContent()) {
            solutionContentDAO.insertAll(solutionContent, conflictingChunkId);
        }

        //Conflicting
        for (String conflictingContent : conflictingChunk.getConflictingContent()) {
            conflictingContentDAO.insertAll(conflictingContent, conflictingChunkId);
        }

        return conflictingChunkId;

    }

}
