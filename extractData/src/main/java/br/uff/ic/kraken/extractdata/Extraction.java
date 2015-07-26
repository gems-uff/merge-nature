/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
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
                System.out.println("Analyzed!");
            } catch (Exception ex) {
                Logger.getLogger(Extraction.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("java -jar AutomaticAnalysis repositoriesDirectoryPath projectURL outputDirectoryPath");
        }
    }
}