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

        String dir = args[0];
//        String dir = "/Users/gleiph/Desktop/teste/out";

        File directory = new File(dir);

        File[] listFiles = directory.listFiles();

        //Passing for all projects
        for (File file : listFiles) {
            try {

                if (file.isDirectory()) {

                    File[] subDirectories = file.listFiles();
                    int numberSubdirectories = 0;

                    for (File subDirectory : subDirectories) {
                        if (subDirectory.isDirectory()) {
                            numberSubdirectories++;
                        }
                    }

                    //Verifying if there are things to import
                    if (numberSubdirectories <= 0) {
                        System.out.println(file.getAbsoluteFile() + " is empty!");
                        continue;
                    }

                    //Reading project metadata
                    String projectName = file.getName();
                    System.out.println("    ");
                    System.out.println(projectName);
                    System.out.println("    ");

                    List<String> revisionsSHA = new ArrayList<String>();

                    for (int i = 0; i < numberSubdirectories; i++) {

                        File subDirectory = new File(file.getAbsoluteFile() + File.separator + i);

                        File[] revisions = subDirectory.listFiles();

                        if (i == 0) {
                            for (int j = 1; j <= revisions.length; j++) {
                                Revision revision = AutomaticAnalysis.readRevision(subDirectory.getAbsolutePath() + File.separator + projectName + j);
                                String sha = revision.getSha();
                                revisionsSHA.add(sha);
                            }
                        } else {
                            for (int j = i * 1000; j < i * 1000 + revisions.length; j++) {
                                Revision revision = AutomaticAnalysis.readRevision(subDirectory.getAbsolutePath() + File.separator + projectName + j);
                                String sha = revision.getSha();
                                revisionsSHA.add(sha);
                            }
                        }
                    }

                    Writer writer = FileManager.createWriter(file.getAbsolutePath() + File.separator + "sha");

                    for (String sha : revisionsSHA) {
                        FileManager.write(sha + "\n", writer);
                    }

                    FileManager.closeWriter(writer);
                }
            } catch (Exception ex) {
                System.out.println("The projects " + file.getName() + "was not imported well!");
            }
        }

        System.out.println("Done!");
        return;

    }

}
