/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Revision;
import java.sql.SQLException;

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

        return DefaultOperations.insert(insertSQL);

    }

    public Long insertAll(Revision revision, Long projectId) throws SQLException {
        Long revision_id = this.insert(revision, projectId);

        //Adding conflicting files
        ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO();

        for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
            conflictingFileDAO.insertAll(conflictingFile, revision_id);
        }

        return revision_id;
    }
}
