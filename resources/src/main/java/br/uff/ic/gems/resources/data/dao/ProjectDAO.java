/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author gleiph
 */
public class ProjectDAO {

    public Project saveGithub(Project project) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (manager.contains(project)) {
                manager.merge(project);
            } else {
                manager.persist(project);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw e;
        }

        return project;
    }

    public Project save(Project project) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (project.getId() == null) {
                manager.persist(project);
            } else {

                if (!manager.contains(project)) {
                    if (manager.find(Project.class, project.getId()) == null) {
                        throw new Exception("Error during Project persistence!");
                    }
                }

                project = manager.merge(project);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        }

        return project;
    }

    public Project importAutomaticAnalyses(Project project) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            Project projectBD = this.getByURL(project.getHtmlUrl());

            manager.getTransaction().begin();

            if (projectBD == null) {
                for (Revision revision : project.getRevisions()) {
                    manager.persist(revision);
                }

                manager.persist(project);
            } else {

                for (Revision revision : project.getRevisions()) {
                    manager.persist(revision);
                }

                project.setId(projectBD.getId());
                project = manager.merge(project);
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

    public Project getById(Long id) {
        Project project = null;
        EntityManager manager = DatabaseManager.getManager();

        try {
            project = manager.find(Project.class, id);
        } catch (Exception e) {
            throw e;
        }

        return project;
    }

    public long getLastId() {

        EntityManager manager = DatabaseManager.getManager();

        //Jpa manager
        String sql = "SELECT MAX(id) FROM Project p";
        Query query = manager.createQuery(sql);

        List result = query.getResultList();

        if (result != null && !result.isEmpty() && result.get(0) != null) {
            return (long) result.get(0);
        } else {
            return -1;
        }

    }

    public Project getByURL(String url) {

        EntityManager manager = DatabaseManager.getManager();

        //Jpa manager
        String sql = "SELECT p FROM Project p WHERE p.htmlUrl = \'" + url + "\'";
        Query query = manager.createQuery(sql);

        List result = query.getResultList();

        if (result != null && !result.isEmpty() && result.get(0) != null) {
            return (Project) result.get(0);
        } else {
            return null;
        }

    }

    public List<Project> getAll() {
        List<Project> projects = null;
        EntityManager manager = DatabaseManager.getManager();

        try {
            Query query = manager.createQuery("SELECT p FROM Project p");
            projects = query.getResultList();
        } catch (Exception e) {
            throw e;
        }

        return projects;
    }

    public List<Long> getIDs() {

        EntityManager manager = DatabaseManager.getManager();

        //Jpa manager
        String sql = "SELECT p.id FROM Project p ORDER BY p.id";
        Query query = manager.createQuery(sql);

        List<Long> result = query.getResultList();

        return result;

    }

    public List<Project> getJavaProjects() {

        EntityManager manager = DatabaseManager.getManager();

        //Jpa manager
        String sql = "SELECT \n"
                   + "          distinct(p)\n"
                   + "FROM \n"
                   + "          Project p join p.languages pl "                   
                   + "WHERE \n"
                   + "          UPPER(pl.name) = 'JAVA'";
        
        Query query = manager.createQuery(sql);

        List<Project> result = query.getResultList();

        return result;

    }
}
