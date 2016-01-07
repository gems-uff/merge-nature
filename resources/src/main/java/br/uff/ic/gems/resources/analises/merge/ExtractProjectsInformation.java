/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.data.Fork;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.sql.ForkJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.RevisionJDBCDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class ExtractProjectsInformation {

    public static List<Project> extractProjectsInformation(Connection connection) throws SQLException {

        List<Project> mergedProjects = new ArrayList<>();

        ProjectJDBCDAO projectJDBCDAO = new ProjectJDBCDAO(connection);
        ForkJDBCDAO forkJDBCDAO = new ForkJDBCDAO(connection);

        List<Project> mainProjects = projectJDBCDAO.selectAnalyzedMainProjects();

        for (Project mainProject : mainProjects) {

            System.out.println("Analyzing " + mainProject.getName());
            List<Fork> forks = forkJDBCDAO.selectAllByProjectId(mainProject.getId());

            List<Project> projectForks = new ArrayList<>();

            for (Fork fork : forks) {
                Project projectFork = projectJDBCDAO.selectByProjectId(fork.getForkId());
                if (projectFork.isAnalyzed()) {
                    projectForks.add(projectFork);
                }
            }

            Project mergedProject = mergeProjects(mainProject, projectForks, connection);

            mergedProjects.add(mergedProject);

        }

        return mergedProjects;
    }

    private static Project mergeProjects(Project mainProject, List<Project> forks, Connection connection) throws SQLException {

        Project mergedProject = new Project();
        mergedProject.setAnalyzed(mainProject.isAnalyzed());
        mergedProject.setCreatedAt(mainProject.getCreatedAt());
        mergedProject.setDevelopers(mainProject.getDevelopers());
        mergedProject.setFork(mainProject.isFork());
        mergedProject.setForks(mainProject.getForks());
        mergedProject.setHtmlUrl(mainProject.getHtmlUrl());
        mergedProject.setId(mainProject.getId());
        mergedProject.setLanguages(mainProject.getLanguages());
        mergedProject.setMessage(mainProject.getMessage());
        mergedProject.setName(mainProject.getName());
        mergedProject.setNumberConflictingMerges(mainProject.getNumberConflictingMerges());
        mergedProject.setNumberMergeRevisions(mainProject.getNumberMergeRevisions());
        mergedProject.setNumberRevisions(mainProject.getNumberRevisions());
        mergedProject.setPriva(mainProject.isPriva());
        mergedProject.setRepositoryPath(mainProject.getRepositoryPath());
        mergedProject.setRevisions(mainProject.getRevisions());
        mergedProject.setSearchUrl(mainProject.getSearchUrl());
        mergedProject.setUpdatedAt(mainProject.getUpdatedAt());

        RevisionJDBCDAO revisionJDBCDAO = new RevisionJDBCDAO(connection);

        List<String> shas = new ArrayList<>();
        List<Revision> mainProjectRevisions = revisionJDBCDAO.selectByProjectId(mainProject.getId());
        mergedProject.setRevisions(mainProjectRevisions);

        for (Revision revision : mainProjectRevisions) {
            shas.add(revision.getSha());
        }

        if (!forks.isEmpty()) {
            //Identify duplicated revisions
            for (Project fork : forks) {
                List<Revision> revisions = revisionJDBCDAO.selectByProjectId(fork.getId());

                for (Revision revision : revisions) {
                    if (!shas.contains(revision.getSha())) {
                        mergedProject.getRevisions().add(revision);
                        mergedProject.setNumberMergeRevisions(mergedProject.getNumberMergeRevisions() + 1);
                        if (revision.isConflict()) {
                            mergedProject.setNumberConflictingMerges(mergedProject.getNumberConflictingMerges() + 1);
                        }
                    }
                }
            }
        }

        return mergedProject;
    }

    public static void main(String[] args) {

        String database = "automaticAnalysis";

        try (Connection connection = (new JDBCConnection()).getConnection(database)) {
            extractProjectsInformation(connection);
        } catch (SQLException ex) {
            Logger.getLogger(ExtractProjectsInformation.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
