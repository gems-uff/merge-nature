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
        
        String dir = "/Users/gleiph/Desktop/outputJson";
        
        File directory = new File(dir);
        
        File[] listFiles = directory.listFiles();
        
        for (File listFile : listFiles) {
            Project reader = AutomaticAnalysis.reader(listFile.getAbsolutePath());
            try {
                projectDAO.importAutomaticAnalyses(reader);
            } catch (Exception ex) {
                Logger.getLogger(ImportData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
