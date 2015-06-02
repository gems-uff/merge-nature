package br.uff.ic.github.github.parser;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.github.github.file.FileManager;
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

        String url = "URL:";
        String htmlUrl = "HtmlURL:";
        String java = "JAVA:";

        List<String> readLines = FileUtils.readLines(new File(reportPath));

        String urlApi = null;
        double percentage = 0;

        FileManager writeFile = new FileManager(outputPath, false);
        writeFile.open();
        
        for (String line : readLines) {

            if (line.contains(url) && !line.contains(htmlUrl)) {
                if (urlApi != null && percentage > 50.0) {

                    GithubAPI.init(/*"maparao", "fake1234"*/);
                    Project project = GithubAPI.project(urlApi);
                    System.out.println(project.toString());
                    System.out.println("\t" + percentage);
                    
                    writeFile.writeln(project.getName()+", "+project.getCreatedAt()
                            +", "+project.getUpdatedAt()+", "+project.getSearchUrl()
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
