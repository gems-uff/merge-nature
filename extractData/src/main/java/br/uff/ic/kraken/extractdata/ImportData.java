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

    public static final String NOT_FOUND = "Not Found";

    public static void main(String[] args) throws IOException {

        String dir = args[0];
        String database = args[1];
//        String dir = "/Users/gleiph/Downloads/analisis/analysis/out126";
//        String database = "importingTestDelete";

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

                        List<String> shas = new ArrayList<>();
                        if (shaFile.isFile()) {
                            shas = FileUtils.readLines(shaFile);
                        }

                        File projectDescriptorFile = new File(projectDirectory, projectDirectory.getName());

                        if (!projectDescriptorFile.isFile()) {
                            System.out.println("****************** Error **********************************************");
                            System.out.println("Project " + projectDescriptorFile.getName() + " was not imported...");
                            System.out.println("****************** Error **********************************************");
                            continue;
                        }

                        /*
                         Reading project from file
                         */
                        Project projectFromFile = AutomaticAnalysis.readProject(projectDescriptorFile);

                        System.out.println("Importing " + projectFromFile.getName());

                        //Setting analyzed to true, because it was analyzed
                        projectFromFile.setAnalyzed(false);

                        //Getting fork information
                        GithubAPI.init();
                        Project projectAux = GithubAPI.project(projectFromFile.getSearchUrl(), false, false, true);
                        projectFromFile.setFork(projectAux.isFork());

                        projectFromFile.setMessage(projectAux.getMessage());

                        //Treating when the project is not on Github anymore
                        if (projectFromFile.getMessage() != null && projectFromFile.getMessage().equals(NOT_FOUND)) {
                            projectFromFile.setMainProjectId(projectFromFile.getId());
                        } else {
                            projectFromFile.setMainProjectId(projectAux.getMainProjectId());
                        }

                        Project ProjectFromDatabase = projectDAO.selectByProjectId(projectFromFile.getId());

                        if(ProjectFromDatabase.isAnalyzed()){
                            System.out.println("The project " + ProjectFromDatabase.getName() + " was already analyzed");
                            continue;
                            
                        }
                        
                        if (ProjectFromDatabase.getId() != null) {
                            projectDAO.update(projectFromFile);
                        } else {
                            projectDAO.insertAll(projectFromFile);
                        }

                        System.out.println("Importing " + projectFromFile.getName() + "...");
                        for (String sha : shas) {
                            int index = shas.indexOf(sha) + 1;

                            if (index % 10 == 0) {
                                System.out.println(shas.indexOf(sha) + "/" + shas.size() + "(" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000 + ")");
                            }

                            File currentRevisionFilePath = new File(projectDirectory, (index / 1000) + File.separator + sha);
                            Revision currentRevision = AutomaticAnalysis.readRevision(currentRevisionFilePath.getAbsolutePath());

                            List<Revision> selectByProjectIdSHA = revisionDAO.selectByProjectIdSHA(projectFromFile.getId(), currentRevision.getSha());

                            if (selectByProjectIdSHA.isEmpty()) {

                                //Getting outmost kind of conflict
                                for (ConflictingFile conflictingFile : currentRevision.getConflictingFiles()) {
                                    for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {

                                        List<String> generalKindConflict = conflictingChunk.generalKindConflict();
                                        String kindConflictOutmost = "";
                                        for (int i = 0; i < generalKindConflict.size() - 1; i++) {
                                            kindConflictOutmost += generalKindConflict.get(i) + ", ";
                                        }
                                        kindConflictOutmost += generalKindConflict.get(generalKindConflict.size() - 1);

                                        conflictingChunk.setGeneralKindConflictOutmost(kindConflictOutmost);
                                    }
                                }

                                revisionDAO.insertAll(currentRevision, projectFromFile.getId());
//                                System.out.println("Revision " + currentRevision.getSha() + " stored.");
                            } 
//                            else {
//                                System.out.println("Revision " + currentRevision.getSha() + " not stored.");
//                            }
                        }

                        projectFromFile.setAnalyzed(true);
                        projectDAO.update(projectFromFile);
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
