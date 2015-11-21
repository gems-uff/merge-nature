/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.states.MergeStatus;
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
public class RevisionJDBCDAO {

    public static final String ID = "id";
    public static final String BASE_SHA = "basesha";
    public static final String LEFT_SHA = "leftsha";
    public static final String NUMBER_CONFLICTING_FILES = "numberconflictingfiles";
    public static final String NUMBER_JAVA_CONFLICTING_FILES = "numberjavaconflictingfiles";
    public static final String RIGHT_SHA = "rightsha";
    public static final String SHA = "sha";
    public static final String STATUS = "status";

    public static final String PROJECT_ID = "project_id";

    private final String database;

    public RevisionJDBCDAO(String database) {
        this.database = database;
    }

    public Long insert(Revision revision, Long projectId) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.REVISION
                + "("
                + BASE_SHA + ", "
                + LEFT_SHA + ", "
                + NUMBER_CONFLICTING_FILES + ", "
                + NUMBER_JAVA_CONFLICTING_FILES + ", "
                + RIGHT_SHA + ", "
                + SHA + ", "
                + STATUS + ", "
                + PROJECT_ID
                + ")"
                + " VALUES(\'"
                + revision.getBaseSha() + "\', \'"
                + revision.getLeftSha() + "\', \'"
                + revision.getNumberConflictingFiles() + "\', \'"
                + revision.getNumberJavaConflictingFiles() + "\', \'"
                + revision.getRightSha() + "\', \'"
                + revision.getSha() + "\', \'"
                + revision.getStatus().toString() + "\', \'"
                + projectId
                + "\')";

        return DefaultOperations.insert(insertSQL, database);

    }

    public Long insertAll(Revision revision, Long projectId) throws SQLException {
        Long revision_id = this.insert(revision, projectId);

        //Adding conflicting files
        ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(database);

        for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
            conflictingFileDAO.insertAll(conflictingFile, revision_id);
        }

        return revision_id;
    }

    public List<Revision> selectByProjectId(Long projectId) throws SQLException {
        List<Revision> revisions = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.REVISION
                + " WHERE " + PROJECT_ID + " = " + projectId;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {

                Revision revision = new Revision();

                revision.setBaseSha(results.getString(BASE_SHA));
                revision.setId(results.getLong(ID));
                revision.setLeftSha(results.getString(LEFT_SHA));
                revision.setNumberConflictingFiles(results.getInt(NUMBER_CONFLICTING_FILES));
                revision.setNumberJavaConflictingFiles(results.getInt(NUMBER_JAVA_CONFLICTING_FILES));
                revision.setRightSha(results.getString(RIGHT_SHA));
                revision.setSha(results.getString(SHA));

                String mergeStatus = results.getString(STATUS);

                if (mergeStatus.equals(MergeStatus.CONFLICTING.toString())) {
                    revision.setStatus(MergeStatus.CONFLICTING);
                } else if (mergeStatus.equals(MergeStatus.FAST_FORWARD.toString())) {
                    revision.setStatus(MergeStatus.FAST_FORWARD);
                } else if (mergeStatus.equals(MergeStatus.NON_CONFLICTING.toString())) {
                    revision.setStatus(MergeStatus.NON_CONFLICTING);
                } else if (mergeStatus.equals(MergeStatus.OCTOPUS.toString())) {
                    revision.setStatus(MergeStatus.OCTOPUS);
                }

                revisions.add(revision);
            }
        }

        return revisions;
    }

    public List<Revision> selectAllByProjectId(Long projectId) throws SQLException {
        List<Revision> revisions = this.selectByProjectId(projectId);
        ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(database);

        for (Revision revision : revisions) {
            revision.setConflictingFiles(conflictingFileDAO.selectAllByRevisionId(revision.getId()));
        }

        return revisions;
    }

    public Revision selectByRevisionId(Long revisionId) throws SQLException {
        Revision revision = new Revision();

        String query = "SELECT * FROM " + Tables.REVISION
                + " WHERE " + ID + " = " + revisionId;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            if (results.next()) {

                revision.setBaseSha(results.getString(BASE_SHA));
                revision.setId(results.getLong(ID));
                revision.setLeftSha(results.getString(LEFT_SHA));
                revision.setNumberConflictingFiles(results.getInt(NUMBER_CONFLICTING_FILES));
                revision.setNumberJavaConflictingFiles(results.getInt(NUMBER_JAVA_CONFLICTING_FILES));
                revision.setRightSha(results.getString(RIGHT_SHA));
                revision.setSha(results.getString(SHA));

                String mergeStatus = results.getString(STATUS);

                if (mergeStatus.equals(MergeStatus.CONFLICTING.toString())) {
                    revision.setStatus(MergeStatus.CONFLICTING);
                } else if (mergeStatus.equals(MergeStatus.FAST_FORWARD.toString())) {
                    revision.setStatus(MergeStatus.FAST_FORWARD);
                } else if (mergeStatus.equals(MergeStatus.NON_CONFLICTING.toString())) {
                    revision.setStatus(MergeStatus.NON_CONFLICTING);
                } else if (mergeStatus.equals(MergeStatus.OCTOPUS.toString())) {
                    revision.setStatus(MergeStatus.OCTOPUS);
                }

            }
        }

        return revision;
    }

    public Revision selectAllByRevisionId(Long revisionId) throws SQLException {
        Revision revision = this.selectByRevisionId(revisionId);
        ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(database);

        revision.setConflictingFiles(conflictingFileDAO.selectAllByRevisionId(revision.getId()));

        return revision;
    }
}
