/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github.parser;

import br.uff.ic.github.github.data.Project;
import br.uff.ic.github.github.file.WriteFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Gleiph
 */
public class ReportReader {

    public static void getJavaProjects(String reportPath, String outputPath) throws IOException {

//        String name = "Name:";
//        String fullName = "FullName:";
        String url = "URL:";
        String htmlUrl = "HtmlURL:";
//        String contributors = "Contributors:";
        String java = "JAVA:";

        List<String> readLines = FileUtils.readLines(new File(reportPath));

        String urlApi = null;
        double percentage = 0;

        WriteFile writeFile = new WriteFile(outputPath, false);
        writeFile.open();
        
        for (String line : readLines) {

            if (line.contains(url) && !line.contains(htmlUrl)) {
                if (urlApi != null && percentage > 50.0) {

                    GithubAPI.init("maparao", "fake1234");
                    Project project = GithubAPI.project(urlApi);
                    System.out.println(project.toString());
                    System.out.println("\t" + percentage);
                    
                    writeFile.writeln(project.getName()+", "+project.getCreatedAt()
                            +", "+project.getUpdatedAt()+", "+project.getUrl()
                            +", "+project.getHtmlUrl()+", "+project.isPriva()+", "+percentage);
                }

                urlApi = getContent(line);
                percentage = 0;
            } else if (line.toUpperCase().contains(java)) {
                percentage = Double.parseDouble(getContent(line));

            }

        }
        writeFile.close();
    }

    public static String getContent(String line) {
        String[] split = line.split(": ");
        return split[1].replaceAll(" ", "");
    }
    
}
