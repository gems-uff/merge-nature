/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.github.github.data.Project;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author gleiph
 */
@Named(value = "projectBean")
@RequestScoped
public class ProjectBean {

    /**
     * Creates a new instance of Project
     */
    public ProjectBean() {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("Local");
        EntityManager manager = factory.createEntityManager();
        projects = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {
            Project project = Project.getProject(Long.parseLong(i + ""), manager);

            if (project != null) {
                projects.add(project);
            }
        }
    }

    private List<Project> projects;

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
