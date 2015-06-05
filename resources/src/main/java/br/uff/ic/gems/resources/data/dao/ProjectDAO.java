/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class ProjectDAO {

    public Project save(Project project) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (project.getId() == null) {
                manager.persist(project);
            } else {

                if (!manager.contains(project)) {
                    if (manager.find(Project.class, project.getId()) == null) {
                        throw new Exception("Error during Local persistence!");
                    }
                }
                manager.merge(project);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        }

        return project;
    }

    public void remove(Project project) {
        EntityManager manager = DatabaseManager.getManager();

        if (project.getId() != null) {
            Project projectRemove = manager.find(Project.class, project.getId());

            try {
                manager.getTransaction().begin();
                manager.remove(projectRemove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    public Project getProjectById(Long id){
        Project project = null;
        EntityManager manager = DatabaseManager.getManager();
        
        try{
            project = manager.find(Project.class, id);
        } catch(Exception e){
            throw e;
        }
        
        return project;
    }
    
}
