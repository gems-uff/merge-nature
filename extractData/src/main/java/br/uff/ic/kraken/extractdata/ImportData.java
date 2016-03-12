/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.RevisionJDBCDAO;
import br.uff.ic.gems.resources.github.parser.GithubAPI;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class ImportData {

    public static void main(String[] args) throws IOException {

        String dir = args[0];
        String database = args[1];
//        String dir = "/Users/gleiph/Desktop/outputs/out50/";
//        String database = "testImportingData";
        try (Connection connection = (new JDBCConnection()).getConnection(database)) {
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(connection);

            File directory = new File(dir);

            File[] listFiles = directory.listFiles();

            //Passing for all projects
            for (File projectDirectory : listFiles) {

                try {
                    if (projectDirectory.isDirectory()) {

                        File shaFile = new File(projectDirectory, "sha");

                        List<String> lines = new ArrayList<>();
                        if (shaFile.isFile()) {
                            lines = FileUtils.readLines(shaFile);
                        }

                        Project project = AutomaticAnalysis.readProject(new File(projectDirectory, projectDirectory.getName()));

                        Project selectByProjectId = projectDAO.selectByProjectId(project.getId());

                        if (selectByProjectId.getId() != null) {
                            System.out.println("Skiping " + selectByProjectId.getName());
                            continue;
                        }

                        //Setting analyzed to true, because it was analyzed
                        project.setAnalyzed(true);
                        
                        //Getting fork information
                        GithubAPI.init();
                        Project projectAux = GithubAPI.project(project.getSearchUrl(), false, false, true);
                        project.setFork(projectAux.isFork());
                        project.setMainProjectId(projectAux.getMainProjectId());
                        
                        projectDAO.insertAll(project);

                        System.out.println("Importing " + project.getName() + "...");
                        for (String line : lines) {
                            int index = lines.indexOf(line) + 1;

                            if (index % 10 == 0) {
                                System.out.println(lines.indexOf(line) + "/" + lines.size() + "(" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 + ")");
                            }

                            File currentRevisionPath = new File(projectDirectory, (index / 1000) + File.separator + line);
                            Revision currentRevision = AutomaticAnalysis.readRevision(currentRevisionPath.getAbsolutePath());

                            //Getting outmost kind of conflict
                            for (ConflictingFile conflictingFile : currentRevision.getConflictingFiles()) {
                                for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
                                    
//                                    System.out.println("Conflict content");
//                                    for (String conflictingContent : conflictingChunk.getConflictingContent()) {
//                                        System.out.println(conflictingContent);
//                                    }
//                                    System.out.println("");
//                                    System.out.println("Solution content");
//                                    for (String solutionContent : conflictingChunk.getSolutionContent()) {
//                                        System.out.println(solutionContent);
//                                    }
                                    
                                    List<String> generalKindConflict = conflictingChunk.generalKindConflict();
                                    String kindConflictOutmost = "";
                                    for (int i = 0; i < generalKindConflict.size() - 1; i++) {
                                        kindConflictOutmost += generalKindConflict.get(i) + ", ";
                                    }
                                    kindConflictOutmost += generalKindConflict.get(generalKindConflict.size() - 1);
                                    
                                    conflictingChunk.setGeneralKindConflictOutmost(kindConflictOutmost);
                                }
                            }
                            
                            revisionDAO.insertAll(currentRevision, project.getId());
                            
                        }

                    }
                } catch (IOException | SQLException e) {

                    e.printStackTrace();
                    System.out.println("Problem during " + projectDirectory.getName() + " importation...");
                }
            }

            System.out.println("Done!");

        } catch (SQLException ex) {
            Logger.getLogger(ImportData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
