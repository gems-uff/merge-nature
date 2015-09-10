/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import br.uff.ic.gems.resources.utils.FileManager;
import br.uff.ic.kraken.extractdata.bd.Migration;
import java.io.File;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ScriptGenerator {

    public static void main(String[] args) throws ParseException {

        String outputScripts = "/Users/gleiph/Desktop/scripts/v2/";
        File scriptsDir = new File(outputScripts);
        if (!scriptsDir.isDirectory()) {
            scriptsDir.mkdirs();
        }

        ProjectDAO projectDAO = new ProjectDAO();
        Date beginRecover = new Date();
        List<Project> all = projectDAO.getJavaProjects();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date endRecover = new Date();

        System.out.println("Time to recover data: " + beginRecover + " - " + endRecover + " => " + (endRecover.getTime() - beginRecover.getTime()) / 1000);

        System.out.println("Size: " + all.size());
        int count = 0;

        Date beginAnalysis = new Date();

        List<String> result = new ArrayList<>();
        int scriptCount = 1;
        for (Project project : all) {
            count++;

            if (Migration.isMainJava(project)) {
                try {
                    String updatedAt = project.getUpdatedAt();

                    if (updatedAt == null) {
                        continue;
                    }
                    Date updated = formatter.parse(updatedAt);

                    if (updated.after(formatter.parse("2015-01-01T00:00:00Z"))) {
                        String line = project.getId() + " " + project.getName() + " " + project.getHtmlUrl() + " " + project.getDevelopers() + "\n";
                        System.out.print(line);
                        result.add(project.getHtmlUrl());

                        if (result.size() == 100) {
                            writeScript(outputScripts + "script" + (scriptCount++) + ".sh", result);

                            result = new ArrayList<>();
                        }
                    }
                } catch (ParseException ex) {
                    System.out.println("----------------------------------------------------------------------------");
                    System.out.println(project.getName() + " (" + project.getId() + ")");
                    System.out.println("----------------------------------------------------------------------------");

                }
            }
        }

        writeScript(outputScripts + "script" + (scriptCount++) + ".sh", result);

        Date endAnalysis = new Date();

        System.out.println("Time to recover data: " + beginRecover + " - " + endRecover + " => " + (endRecover.getTime() - beginRecover.getTime()) / 1000);
        System.out.println("Time to Analyze data: " + beginAnalysis + " - " + endAnalysis + " => " + (endAnalysis.getTime() - beginAnalysis.getTime()) / 1000);
    }

    public static void writeScript(String path, List<String> content) {
        Writer writer = FileManager.createWriter(path);

        FileManager.write("#!/bin/bash\n", writer);
        FileManager.write("\n", writer);
        FileManager.write("rep=\"/home/kraken/Desktop/rep\"\n", writer);
        FileManager.write("out=\"/home/kraken/Desktop/out\"\n", writer);

        for (String l : content) {
            FileManager.write("java -jar extract.jar $rep " + l + " $out \n", writer);
            FileManager.write("rm -rf $rep \n", writer);

        }
        FileManager.closeWriter(writer);

    }

}
