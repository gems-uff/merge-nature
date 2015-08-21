/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.utils.FileManager;
import java.io.File;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class Extraction {

    public static void main(String[] args) {
        if (args.length == 3) {
            try {

                String repositoriesDirectoryPath = args[0];
                String projectURL = args[1];
                String outputDirectoryPath = args[2];
                AutomaticAnalysis.analyze(repositoriesDirectoryPath, projectURL, outputDirectoryPath);

                Writer createWriter = FileManager.createWriter(outputDirectoryPath + File.separator + "controler.txt", true);
                FileManager.write("\n" + projectURL + ", " + "OK", createWriter);
                FileManager.closeWriter(createWriter);
                
                System.out.println("Analyzed!");
            } catch (Exception ex) {
                Logger.getLogger(Extraction.class.getName()).log(Level.SEVERE, null, ex);
                Writer createWriter = FileManager.createWriter(args[2] + File.separator + "controler.txt", true);
                FileManager.write("\n" + args[1] + ", " + "OK", createWriter);
                FileManager.closeWriter(createWriter);
            }

        } else {
            System.out.println("java -jar AutomaticAnalysis repositoriesDirectoryPath projectURL outputDirectoryPath");
        }
    }
}
