/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.analises.merge.ProjectAnalyzer;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;

/**
 *
 * @author gleiph
 */
@Named(value = "projectAnalysisBean")
@RequestScoped
public class ProjectAnalysisBean {

    private String repositoryUrl;
    private String repositoryPath;
    private String projectName;
    private Long id;

    public String cloneRepository() {

        Git git = new Git(repositoryPath);
        git.clone(repositoryUrl);

        return null;
    }

    public String analyze() {
    
        ProjectDAO projectDAO = new ProjectDAO();
        
        File repositoriesDirectory = new File(repositoryPath);
        if(!repositoriesDirectory.isDirectory())
            repositoriesDirectory.mkdirs();
        
        Git git = new Git(repositoryPath);
        git.clone(repositoryUrl);

        String projectPath;
        if (!repositoryPath.endsWith(File.separator)) {
            projectPath = repositoryPath + File.separator + projectName;
        } else {
            projectPath = repositoryPath + projectName;
        }

        ProjectAnalyzer pa = new ProjectAnalyzer();
        
        Project project = projectDAO.getById(id);
        project.setRepositoryPath(projectPath);
        
        
        Project analyze = pa.analyze(project);
        

        return null;
    }

    /**
     * @return the repositoryUrl
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * @param repositoryUrl the repositoryUrl to set
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /**
     * @return the repositoryPath
     */
    public String getRepositoryPath() {
        return repositoryPath;
    }

    /**
     * @param repositoryPath the repositoryPath to set
     */
    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

}
