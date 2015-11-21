/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
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
public class ConflictingFileJDBCDAO {

    public static final String ID = "id";
    public static final String FILETYPE = "filetype";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String REMOVED = "removed";

    public static final String REVISION_ID = "revision_id";

    private final String database;

    public ConflictingFileJDBCDAO(String database) {
        this.database = database;
    }

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

        return DefaultOperations.insert(insertSQL, database);

    }

    public Long insertAll(ConflictingFile conflictingFile, Long revisionId) throws SQLException {

        long conflictingFile_id = this.insert(conflictingFile, revisionId);

        //Adding conflicting chunks
        ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(database);

        for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
            conflictingChunkDAO.insertAll(conflictingChunk, conflictingFile_id);
        }

        return conflictingFile_id;

    }

    public List<ConflictingFile> selectByRevisionId(Long revisionId) throws SQLException {
        List<ConflictingFile> conflictingFiles = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.CONFLICTING_FILE
                + " WHERE " + REVISION_ID + " = " + revisionId;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                ConflictingFile conflictingFile = new ConflictingFile();

                conflictingFile.setFileType(results.getString(FILETYPE));
                conflictingFile.setId(results.getLong(ID));
                conflictingFile.setName(results.getString(NAME));
                conflictingFile.setPath(results.getString(PATH));
                conflictingFile.setRemoved(results.getBoolean(REMOVED));

                conflictingFiles.add(conflictingFile);
            }
        }

        return conflictingFiles;
    }

    public List<ConflictingFile> selectAllByRevisionId(Long revisionId) throws SQLException {
        List<ConflictingFile> conflictingFiles = this.selectByRevisionId(revisionId);

        ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(database);

        for (ConflictingFile conflictingFile : conflictingFiles) {
            conflictingFile.setConflictingChunks(conflictingChunkDAO.selectAllByConflictingFileId(conflictingFile.getId()));
        }

        return conflictingFiles;
    }

    public ConflictingFile selectByConflictingFileId(Long conflictingFileId) throws SQLException {
        ConflictingFile conflictingFile = new ConflictingFile();

        String query = "SELECT * FROM " + Tables.CONFLICTING_FILE
                + " WHERE " + ID + " = " + conflictingFileId;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            if (results.next()) {

                conflictingFile.setFileType(results.getString(FILETYPE));
                conflictingFile.setId(results.getLong(ID));
                conflictingFile.setName(results.getString(NAME));
                conflictingFile.setPath(results.getString(PATH));
                conflictingFile.setRemoved(results.getBoolean(REMOVED));

            }
        }

        return conflictingFile;
    }

    public ConflictingFile selectAllByConflictingFileId(Long conflictingFileId) throws SQLException {
        ConflictingFile conflictingFile = this.selectByConflictingFileId(conflictingFileId);

        ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(database);

        conflictingFile.setConflictingChunks(conflictingChunkDAO.selectAllByConflictingFileId(conflictingFile.getId()));

        return conflictingFile;
    }
}
