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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class ImportData {

    public static void main(String[] args) {
        ProjectDAO projectDAO = new ProjectDAO();

        String dir = "/Users/gleiph/Dropbox/doutorado/implementation/production/out";

        File directory = new File(dir);

        File[] listFiles = directory.listFiles();

        for (File file : listFiles) {

            if (file.isDirectory()) {

                File[] subfiles = file.listFiles();
                
                if(subfiles.length <= 0){
                    System.out.println(file.getAbsoluteFile()+ " is empty!");
                    return;
                }
                
                Project project = AutomaticAnalysis.readeProject(subfiles[0].getAbsolutePath());
                
                for (int i = 1; i < subfiles.length; i++) {
                    Revision revision = AutomaticAnalysis.readeRevision(subfiles[0].getAbsolutePath()+i);
                    project.getRevisions().add(revision);
                }
                
                try {
                    if (project != null && project.getId() != null) {
                        projectDAO.importAutomaticAnalyses(project);
                        System.out.println(project.getName() + " imported...");

                    } else {
                        System.out.println("Error while importing file : " + file.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ImportData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        System.out.println("Done!");
        return;

    }
}
