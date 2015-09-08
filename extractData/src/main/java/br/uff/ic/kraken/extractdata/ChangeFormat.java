/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.utils.FileManager;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ChangeFormat {

    public static void main(String[] args) {

        createSHA(args[0]);
    }

    private static int countRevisions(String projectPath) {
        int revisions = 0;

        File projectDirectory = new File(projectPath);

        for (File file : projectDirectory.listFiles()) {

            if (file.isDirectory()) {
                revisions += file.listFiles().length;
            }
        }

        return revisions;
    }

    public static List<String> getSHAs(String projectPath) {

        List<String> SHAs = new ArrayList<>();
        
        int revisionsNumber = countRevisions(projectPath);

        File projectDirectory = new File(projectPath);

        for (int i = 1; i <= revisionsNumber; i++) {
            File jsonFile = 
                    new File(projectDirectory, (i / 1000) + File.separator + projectDirectory.getName() + i);
            Revision revision = AutomaticAnalysis.readRevision(jsonFile.getAbsolutePath());
            SHAs.add(revision.getSha());
        }

        return SHAs;
    }

    public static void createSHA(String projectPath){
        List<String> SHAs = getSHAs(projectPath);
        
        File sha = new File(projectPath, "sha");
                
        Writer writer = FileManager.createWriter(sha.getAbsolutePath());
        
        for (String SHA : SHAs) {
            FileManager.write(SHA+"\n", writer);
        }
        
        FileManager.closeWriter(writer);
        
    }
    
}
