/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import br.uff.ic.gems.resources.utils.FileManager;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class Extraction {

    public static void main(String[] args) {
        boolean persist = false;

        if (args.length == 3) {
            try {

                String repositoriesDirectoryPath = args[0];
                String projectURL = args[1];
                String outputDirectoryPath = args[2];

                AutomaticAnalysis.analyze(repositoriesDirectoryPath, projectURL, outputDirectoryPath, persist);

                Writer createWriter = FileManager.createWriter(outputDirectoryPath + File.separator + "controler.txt", true);
                FileManager.write("\n" + projectURL + ", " + "OK", createWriter);
                FileManager.closeWriter(createWriter);

                System.out.println("Analyzed!");
            } catch (Exception ex) {
                Logger.getLogger(Extraction.class.getName()).log(Level.SEVERE, null, ex);
                Writer createWriter = FileManager.createWriter(args[2] + File.separator + "controler.txt", true);
                FileManager.write("\n" + args[1] + ", " + "FAIL", createWriter);
                FileManager.closeWriter(createWriter);
            } finally {
                if (persist) {
                    DatabaseManager.closeManager();
                }
            }

        } else {
            System.out.println("java -jar AutomaticAnalysis repositoriesDirectoryPath projectURL outputDirectoryPath");
        }
    }
//    public static void main(String[] args) {
////        preProcessingProject("/Users/gleiph/Desktop/teste/out/processing-js");
//        preProcessingProject(args[0]);
//    }
//
    private static void preProcessingProject(String pathProjectOutput) {

        List<String> SHAs = null;

        if (!pathProjectOutput.endsWith(File.separator)) {
            pathProjectOutput += File.separator;
        }

        String pathSHA = pathProjectOutput+"sha";
        try {

            boolean error = false;

            SHAs = FileUtils.readLines(new File(pathSHA));

            int currentRevision = 1;

            File projectDir = new File(pathProjectOutput);
            List<File> subdirectories = new ArrayList<>();

            String projectName = projectDir.getName();

            for (File file : projectDir.listFiles()) {
                if (file.isDirectory()) {
                    subdirectories.add(file);
                }
            }

            List<String> SHAConfirmed = new ArrayList<>();

            for (int i = 0; i < subdirectories.size(); i++) {

                File subdirectory = new File(projectDir, i + "");

                String[] jsonFiles = subdirectory.list();

                for (int j = 0; j < jsonFiles.length; j++) {

                    File jsonFile = new File(subdirectory, projectName + currentRevision);

                    Revision revision = AutomaticAnalysis.readRevision(jsonFile.getAbsolutePath());

                    if (!SHAs.get(currentRevision - 1).equals(revision.getSha())) {
//                        System.out.println("Error!!!! "+ currentRevision + " - " + revision.getSha());
                        error = true;
                        break;
                    } else {
//                        System.out.println("OK!!!! "+ currentRevision + " - " + revision.getSha());
                        SHAConfirmed.add(revision.getSha());
                    }
                    currentRevision++;
                }

                if (error) {
                    break;
                }
            }

            if (!SHAConfirmed.equals(SHAs)) {
                System.out.println("Error");
                System.out.println("currentRevision = " + currentRevision);
                System.out.println("SHAConfirmedSize = " + SHAConfirmed.size());
                System.out.println("SHAs size = " + SHAs.size());
                System.out.println("Current SHA = " + SHAConfirmed.get(SHAConfirmed.size() - 1));
            } else {
                System.out.println("OK!!!");
            }

        } catch (IOException ex) {
            Logger.getLogger(Extraction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
