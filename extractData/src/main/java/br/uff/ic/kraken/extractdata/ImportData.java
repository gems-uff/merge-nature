/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.data.Project;
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

        String dir = "/Users/gleiph/Desktop/automaticAnalyses/teste";

        File directory = new File(dir);

        File[] listFiles = directory.listFiles();

        for (File listFile : listFiles) {
            Project reader = AutomaticAnalysis.reader(listFile.getAbsolutePath());
            try {
                if (reader != null && reader.getId() != null) {
                    projectDAO.importAutomaticAnalyses(reader);
                    System.out.println(reader.getName() + " imported...");

                } else {
                    System.out.println("Error while importing file : " + listFile.getAbsolutePath());
                }
            } catch (Exception ex) {
                Logger.getLogger(ImportData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("Done!");
        return;

    }
}
