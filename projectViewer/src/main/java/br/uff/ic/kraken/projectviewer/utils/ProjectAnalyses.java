/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.utils;

import br.uff.ic.gems.resources.analises.merge.ProjectAnalyzer;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;

/**
 *
 * @author gleiph
 */
public class ProjectAnalyses {

    public Project analyze(String repositoryPath, Project project) {

        ProjectDAO projectDAO = new ProjectDAO();
        ProjectAnalyzer pa = new ProjectAnalyzer();

        File repositoriesDirectory = new File(repositoryPath);
        if (!repositoriesDirectory.isDirectory()) {
            repositoriesDirectory.mkdirs();
        }

        Git git = new Git(repositoryPath);
        git.clone(project.getHtmlUrl());

        String projectPath;
        if (!repositoryPath.endsWith(File.separator)) {
            projectPath = repositoryPath + File.separator + project.getName();
        } else {
            projectPath = repositoryPath + project.getName();
        }

        project.setRepositoryPath(projectPath);
        
        return pa.analyze(project, true);
    }
}
