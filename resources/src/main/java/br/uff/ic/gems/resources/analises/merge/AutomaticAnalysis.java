/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.github.parser.GithubAPI;
import br.uff.ic.gems.resources.utils.FileManager;
import br.uff.ic.gems.resources.vcs.Git;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class AutomaticAnalysis {

    public static void analyze(String repositoriesDirectoryPath, String projectURL, String outputDirectory) throws Exception{

        String githubURL = projectURL.replace("https://github.com/", "https://api.github.com/repos/");
        ProjectAnalyzer projectAnalyzer = new ProjectAnalyzer();

        if (githubURL.endsWith("/")) {
            githubURL = githubURL.substring(0, githubURL.length() - 1);
        }

        Project project;
        GithubAPI.init();

        project = GithubAPI.project(githubURL);

        File repositoriesDirectory = new File(repositoriesDirectoryPath);
        if (!repositoriesDirectory.isDirectory()) {
            repositoriesDirectory.mkdirs();
        }

        Git git = new Git(repositoriesDirectoryPath);
        git.clone(project.getHtmlUrl());

        String projectPath;
        if (!repositoriesDirectoryPath.endsWith(File.separator)) {
            projectPath = repositoriesDirectoryPath + File.separator + project.getName();
        } else {
            projectPath = repositoriesDirectoryPath + project.getName();
        }

        project.setRepositoryPath(projectPath);

        Date beginDate = new Date();
        project = projectAnalyzer.analyze(project, false);
        Date endDate = new Date();

        System.out.println("Begin: " + beginDate + "\nEnd: " + endDate);

        Gson gson = new Gson();

        String toJson = gson.toJson(project);

        if (!outputDirectory.endsWith(File.separator)) {
            outputDirectory = outputDirectory.concat(File.separator);
        }

        File outputFileDirectory = new File(outputDirectory);
        if (!outputFileDirectory.isDirectory()) {
            outputFileDirectory.mkdirs();
        }

        Writer writer = FileManager.createWriter(outputDirectory + project.getName());
        FileManager.write(toJson, writer);
        FileManager.closeWriter(writer);
        
        try {
            //Removing repository
            System.out.println("Deleting "+repositoriesDirectoryPath+"...");
            FileUtils.deleteDirectory(new File(repositoriesDirectoryPath));
        } catch (IOException ex) {
            Logger.getLogger(AutomaticAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    
    public static Project reader(String JSONPath){
        Project project = null;
        Reader reader = FileManager.createReader(JSONPath);
        
        Gson gson = new Gson();
        project = gson.fromJson(reader, Project.class);
        
        return project;
    }
}
