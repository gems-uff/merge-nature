/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import br.uff.ic.kraken.projectviewer.utils.DatabaseConfiguration;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author gleiph
 */
@Named(value = "projectBean")
@RequestScoped
public class ProjectBean implements Serializable {

    private List<Project> projects;
    private Project project;
    private int id;
    private String repositoryUrl;

    /**
     * Creates a new instance of Project
     */
    public ProjectBean() {

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
        ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);

        projects = new ArrayList<>();

        List<Project> all = null;
        try {
            all = projectDAO.select();
        } catch (SQLException ex) {
            Logger.getLogger(ProjectBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        projects.addAll(all);
        } catch (SQLException ex) {
            Logger.getLogger(ProjectBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        
        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            projects = projectDAO.select();
            return projects;
        } catch (SQLException ex) {
            Logger.getLogger(ProjectBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
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

    public String githubProject() {
        return PagesName.addGithubProject;
    }
}
