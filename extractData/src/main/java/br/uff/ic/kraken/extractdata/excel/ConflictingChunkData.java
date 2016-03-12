/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata.excel;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingChunkJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingFileJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.RevisionJDBCDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class ConflictingChunkData {

    public static void main(String[] args) {

        String bdName = "automaticAnalysis";

        try (Connection connection = (new JDBCConnection()).getConnection(bdName)) {

            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(connection);
            ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(connection);
            ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);

            List<Project> projects = projectDAO.selectAnalyzedMainProjects();

            for (Project project : projects) {

                List<Revision> revisions = revisionDAO.selectByProjectId(project.getId());
                for (Revision revision : revisions) {

                    List<ConflictingFile> conflictingFiles = conflictingFileDAO.selectByRevisionId(revision.getId());
                    for (ConflictingFile conflictingFile : conflictingFiles) {

                        if(!conflictingFile.getName().toLowerCase().endsWith(".java"))
                            continue;
                        
                        List<ConflictingChunk> conflictingChunks = conflictingChunkDAO.selectByConflictingFileId(conflictingFile.getId());
                        for (ConflictingChunk conflictingChunk : conflictingChunks) {

                            System.out.print(project.getName() + ", ");
                            System.out.print(revision.getSha() + ", ");
                            System.out.print(conflictingFile.getName() + ", ");
                            System.out.print(conflictingChunk.getIdentifier() + ", ");

                            String generalKindConflictOutmost = conflictingChunk.getGeneralKindConflictOutmost();
                            System.out.print(generalKindConflictOutmost + ", ");

                            String[] languageConstructs = generalKindConflictOutmost.split(", ");
                            System.out.print(languageConstructs.length + ", ");

                            String developerDecision = conflictingChunk.getDeveloperDecision().toString();
                            System.out.print(developerDecision + ", ");

                            int locVersion1 = conflictingChunk.getSeparatorLine() - conflictingChunk.getBeginLine() - 1;
                            int locVersion2 = conflictingChunk.getEndLine() - conflictingChunk.getSeparatorLine() - 1;

                            System.out.println(locVersion1 + ", " + locVersion2);

                        }
                    }
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(ConflictingChunkData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
