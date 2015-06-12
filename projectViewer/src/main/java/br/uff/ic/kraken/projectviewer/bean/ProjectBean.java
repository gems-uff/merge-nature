/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author gleiph
 */
@Named(value = "projectBean")
@RequestScoped
public class ProjectBean implements Serializable{

    private List<Project> projects;
    private Project project;
    private int id;
    private String repositoryUrl;

    /**
     * Creates a new instance of Project
     */
    public ProjectBean() {

        ProjectDAO projectDAO = new ProjectDAO();
        
        projects = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {
            Project project = projectDAO.getById(Long.parseLong(i + ""));

            if (project != null) {
                projects.add(project);
            }
        }
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    public String getProjectName() {
        return project.getName();
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
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

}
