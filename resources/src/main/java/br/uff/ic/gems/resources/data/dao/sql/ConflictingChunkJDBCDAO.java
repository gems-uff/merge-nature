/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.states.DeveloperDecision;
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
public class ConflictingChunkJDBCDAO {

    public static final String ID = "id";
    public static final String BEGIN_LINE = "beginline";
    public static final String DEVELOPER_DECISION = "developerdecision";
    public static final String END_LINE = "endline";
    public static final String IDENTIFIER = "identifier";
    public static final String SEPARATOR_LINE = "separatorline";

    public static final String CONFLICTING_FILE_ID = "conflictingfile_id";

    private final Connection connection;

    public ConflictingChunkJDBCDAO(Connection connection) {
        this.connection = connection;
    }

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

        return DefaultOperations.insert(insertSQL, connection);

    }

    public Long insertAll(ConflictingChunk conflictingChunk, Long conflictingFileId) throws SQLException {

        Long conflictingChunkId = this.insert(conflictingChunk, conflictingFileId);

        KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO(connection);
        SolutionContentJDBCDAO solutionContentDAO = new SolutionContentJDBCDAO(connection);
        ConflictingContentJDBCDAO conflictingContentDAO = new ConflictingContentJDBCDAO(connection);

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

    public List<ConflictingChunk> selectByConflictingFileId(Long conflictingFileId) throws SQLException {
        List<ConflictingChunk> conflictingChunks = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.CONFLICTING_CHUNK
                + " WHERE " + CONFLICTING_FILE_ID + " = " + conflictingFileId;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                ConflictingChunk conflictingChunk = new ConflictingChunk();

                conflictingChunk.setBeginLine(results.getInt(BEGIN_LINE));
                conflictingChunk.setDeveloperDecision(DeveloperDecision.toDeveloperDecision(results.getString(DEVELOPER_DECISION)));
                conflictingChunk.setEndLine(results.getInt(END_LINE));
                conflictingChunk.setId(results.getLong(ID));
                conflictingChunk.setIdentifier(results.getString(IDENTIFIER));
                conflictingChunk.setSeparatorLine(results.getInt(SEPARATOR_LINE));

                conflictingChunks.add(conflictingChunk);
            }
        }

        return conflictingChunks;
    }

    public List<ConflictingChunk> selectAllByConflictingFileId(Long conflictingFileId) throws SQLException {
        List<ConflictingChunk> conflictingChunks = this.selectByConflictingFileId(conflictingFileId);

        ConflictingContentJDBCDAO conflictingContentDAO = new ConflictingContentJDBCDAO(connection);
        SolutionContentJDBCDAO solutionContentDAO = new SolutionContentJDBCDAO(connection);
        KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO(connection);

        for (ConflictingChunk conflictingChunk : conflictingChunks) {

            //Adding conflicting content
            conflictingChunk.setConflictingContent(conflictingContentDAO.selectAllByConflictingChunkId(conflictingChunk.getId()));

            //Adding solution content
            conflictingChunk.setSolutionContent(solutionContentDAO.selectAllByConflictingChunkId(conflictingChunk.getId()));

            List<KindConflict> kindConflict = kindConflictDAO.selectAllByConflictingChunkId(conflictingChunk.getId());

            if (kindConflict.size() != 2) {
                throw new SQLException("Wrong number of kind of conflicts.");
            } else {
                for (KindConflict kc : kindConflict) {
                    if (kc.getSide() == Side.LEFT) {
                        conflictingChunk.setLeftKindConflict(kc);
                    } else {
                        conflictingChunk.setRightKindConflict(kc);
                    }
                }
            }

        }

        return conflictingChunks;
    }

    public ConflictingChunk selectByConflictingChunkId(Long conflictingChunkId) throws SQLException {

        ConflictingChunk conflictingChunk = new ConflictingChunk();

        String query = "SELECT * FROM " + Tables.CONFLICTING_CHUNK
                + " WHERE " + ID + " = " + conflictingChunkId;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            if (results.next()) {

                conflictingChunk.setBeginLine(results.getInt(BEGIN_LINE));
                conflictingChunk.setDeveloperDecision(DeveloperDecision.toDeveloperDecision(results.getString(DEVELOPER_DECISION)));
                conflictingChunk.setEndLine(results.getInt(END_LINE));
                conflictingChunk.setId(results.getLong(ID));
                conflictingChunk.setIdentifier(results.getString(IDENTIFIER));
                conflictingChunk.setSeparatorLine(results.getInt(SEPARATOR_LINE));

            }
        }

        return conflictingChunk;
    }

    public ConflictingChunk selectAllByConflictingChunkId(Long conflictingChunkId) throws SQLException {

        ConflictingChunk conflictingChunk = this.selectByConflictingChunkId(conflictingChunkId);

        ConflictingContentJDBCDAO conflictingContentDAO = new ConflictingContentJDBCDAO(connection);
        SolutionContentJDBCDAO solutionContentDAO = new SolutionContentJDBCDAO(connection);
        KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO(connection);

        //Adding conflicting content
        conflictingChunk.setConflictingContent(conflictingContentDAO.selectAllByConflictingChunkId(conflictingChunk.getId()));

        //Adding solution content
        conflictingChunk.setSolutionContent(solutionContentDAO.selectAllByConflictingChunkId(conflictingChunk.getId()));

        List<KindConflict> kindConflict = kindConflictDAO.selectAllByConflictingChunkId(conflictingChunk.getId());

        if (kindConflict.size() != 2) {
            throw new SQLException("Wrong number of kind of conflicts.");
        } else {
            for (KindConflict kc : kindConflict) {
                if (kc.getSide() == Side.LEFT) {
                    conflictingChunk.setLeftKindConflict(kc);
                } else {
                    conflictingChunk.setRightKindConflict(kc);
                }
            }
        }

        return conflictingChunk;
    }

}
