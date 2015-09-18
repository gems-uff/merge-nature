/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.RevisionJDBCDAO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class ImportData {

    public static void main(String[] args) throws IOException {

        ProjectJDBCDAO projectDAO = new ProjectJDBCDAO();
        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO();
        
        String dir = args[0];
//        String dir = "/Users/gleiph/Desktop/teste/out1";

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
                    projectDAO.insertAll(project);
                    
                    
                    System.out.println("Importing " + project.getName() + "...");
                    for (String line : lines) {
                        int index = lines.indexOf(line) + 1;

                        System.out.println(lines.indexOf(line) + "/" + lines.size() + "(" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000 + ")");
                        File currentRevisionPath = new File(projectDirectory, (index / 1000) + File.separator + line);
                        Revision currentRevision = AutomaticAnalysis.readRevision(currentRevisionPath.getAbsolutePath());

                        revisionDAO.insertAll(currentRevision, project.getId());



                    }

                }
            } catch (Exception e) {
                
                e.printStackTrace();
                System.out.println("Problem during " + projectDirectory.getName() + " importation...");
            }
        }

        System.out.println("Done!");

    }
}
