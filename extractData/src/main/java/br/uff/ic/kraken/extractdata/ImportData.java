/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import java.io.File;

/**
 *
 * @author gleiph
 */
public class ImportData {

    public static void main(String[] args) {
        ProjectDAO projectDAO = new ProjectDAO();

        String dir = "/Users/gleiph/Desktop/teste/out";

        File directory = new File(dir);

        File[] listFiles = directory.listFiles();

        //Passing for all projects
        for (File file : listFiles) {
            try {

                if (file.isDirectory()) {

                    File[] subDirectories = file.listFiles();

                    //Verifying if there are things to import
                    if (subDirectories.length <= 0) {
                        System.out.println(file.getAbsoluteFile() + " is empty!");
                        continue;
                    }

                    //Reading project metadata
                    Project project = AutomaticAnalysis.readProject(subDirectories);

                    for (int i = 0; i < subDirectories.length - 1; i++) {

                        File subDirectory = new File(file.getAbsoluteFile() + File.separator + i);
                        File[] revisions = subDirectory.listFiles();

                        if (i == 0) {
                            for (int j = 1; j <= revisions.length; j++) {
                                Revision revision = AutomaticAnalysis.readRevision(subDirectory.getAbsolutePath() + File.separator + project.getName() + j);
                                project.getRevisions().add(revision);
                            }
                        } else {
                            for (int j = i * 1000; j < i * 1000 + revisions.length; j++) {
                                Revision revision = AutomaticAnalysis.readRevision(subDirectory.getAbsolutePath() + File.separator + project.getName() + j);
                                project.getRevisions().add(revision);
                            }
                        }

                        System.out.println(i + "/" + (subDirectories.length - 1));
                    }

                    if (project != null && project.getId() != null) {
                        projectDAO.importAutomaticAnalyses(project);
                        System.out.println(project.getName() + " imported...");

                    } else {
                        System.out.println("Error while importing file : " + file.getAbsolutePath());
                    }

                }
            } catch (Exception ex) {
                System.out.println("The projects " + file.getName() + "was not imported well!");
            }
        }

        System.out.println("Done!");
        return;

    }
}
