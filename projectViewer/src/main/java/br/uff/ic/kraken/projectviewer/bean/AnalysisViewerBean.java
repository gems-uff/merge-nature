/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingChunkJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingFileJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.KindConflictJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.RevisionJDBCDAO;
import br.uff.ic.gems.resources.states.MergeStatus;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import br.uff.ic.kraken.projectviewer.utils.DataTypes;
import br.uff.ic.kraken.projectviewer.utils.DatabaseConfiguration;
import br.uff.ic.kraken.projectviewer.utils.ProjectAnalyses;
import br.uff.ic.kraken.projectviewer.utils.ProjectOverview;
import br.uff.ic.kraken.projectviewer.utils.ProjectsOverview;
import br.uff.ic.kraken.projectviewer.utils.TreeTableNode;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author gleiph
 */
@Named(value = "analysisViewerBean")
@RequestScoped
public class AnalysisViewerBean implements Serializable {

    private Long projectId;
    private Long conflictingChunkId;
    private List<Revision> revisions;
    private TreeNode root;

    private String selectedId;
    private String dataType;
    private ConflictingChunk selectedConflictingChunk;

    private List<ProjectOverview> projectSummarization;
    private List<ProjectsOverview> projectsSummarization;
    private String projectName;

    /**
     * @return the projectId
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the revisions
     */
    public List<Revision> getRevisions() {
        return revisions;
    }

    /**
     * @param revisions the revisions to set
     */
    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }

    public String actionNavigator() {

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            Project projectById;
            try {
                projectById = projectDAO.selectAllByProjectId(projectId);
            } catch (SQLException ex) {
                Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            revisions = projectById.getRevisions();

            if (getRevisions() != null && !revisions.isEmpty() && getRevisions().get(0) != null) {

                root = new DefaultTreeNode("root", null);

                for (Revision revision : revisions) {
                    TreeNode rev = new DefaultTreeNode(new TreeTableNode(revision.getSha(), revision.getId(), DataTypes.REVISION), root);

                    for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
                        TreeNode cf = new DefaultTreeNode(new TreeTableNode(conflictingFile.getName(), conflictingFile.getId(), DataTypes.CONFLICTING_FILE), rev);

                        for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
                            TreeNode cc = new DefaultTreeNode(new TreeTableNode(conflictingChunk.getIdentifier() + "(" + conflictingChunk.getDeveloperDecision().toString() + ")", conflictingChunk.getId(), DataTypes.CONFLICTING_CHUNK), cf);
                        }
                    }
                }

                return PagesName.showConflicts + "?faces-redirect=true";
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    public String showConflictingChunk() {

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);
            try {
                selectedConflictingChunk = conflictingChunkDAO.selectAllByConflictingChunkId(conflictingChunkId);
            } catch (SQLException ex) {
                Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            return PagesName.showConflictingChunk;
        } catch (SQLException ex) {
            Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String analyze() {

        System.out.println("Begin: " + new Date());
        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);

            Project project = null;
            try {
                project = projectDAO.selectAllByProjectId(projectId);
            } catch (SQLException ex) {
                Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            String repositoriesPath = "/Users/gleiph/Desktop/repositories";
//        String repositoriesPath = "/home/gmenezes/repositories";

            ProjectAnalyses projectAnalyses = new ProjectAnalyses();
            projectAnalyses.analyze(repositoriesPath, project);

            System.out.println("End: " + new Date());
        } catch (SQLException ex) {
            Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String overview() {

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            System.out.println(new Date());
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(connection);
            ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(connection);
            ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);
            KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO(connection);

            Project project = null;
            try {
//                project = projectDAO.selectAllByProjectId(projectId);

                project = projectDAO.selectByProjectId(projectId);

                List<Revision> revisions = revisionDAO.selectByProjectId(projectId);

                for (Revision revision : revisions) {
                    List<ConflictingFile> conflictingFiles = conflictingFileDAO.selectByRevisionId(revision.getId());

                    for (ConflictingFile conflictingFile : conflictingFiles) {
                        List<ConflictingChunk> conflictingChunks = conflictingChunkDAO.selectByConflictingFileId(conflictingFile.getId());

//                        for (ConflictingChunk conflictingChunk : conflictingChunks) {
//                            List<KindConflict> kindConflicts = kindConflictDAO.selectAllByConflictingChunkId(conflictingChunk.getId());
//                            conflictingChunk.setLeftKindConflict(kindConflicts.get(0));
//                            conflictingChunk.setRightKindConflict(kindConflicts.get(1));
//                        }
                        conflictingFile.setConflictingChunks(conflictingChunks);
                    }

                    revision.setConflictingFiles(conflictingFiles);
                }
                project.setRevisions(revisions);

            } catch (SQLException ex) {
                Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            projectSummarization = new ArrayList<>();

            projectName = project.getName();
            String sha1 = "";
            String fileName = "";
            String ccIdentifier = "";
            String developerDecision = "";
//            String kindConflict = "";

            for (Revision revision : project.getRevisions()) {
                sha1 = revision.getSha();

                for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
                    fileName = conflictingFile.getName();

                    for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
                        ccIdentifier = conflictingChunk.getIdentifier();
                        developerDecision = conflictingChunk.getDeveloperDecision().toString();
//                        List<String> generalKindConflict = conflictingChunk.generalKindConflict();
//
//                        kindConflict = "";
//
//                        for (int i = 0; i < generalKindConflict.size(); i++) {
//                            String get = generalKindConflict.get(i);
//
//                            if (i < generalKindConflict.size() - 1) {
//                                kindConflict += get + ", ";
//                            } else {
//                                kindConflict += get;
//                            }
//                        }

                        //Extracting LOC
                        int locLeft = conflictingChunk.getSeparatorLine() - conflictingChunk.getBeginLine() - 1;
                        int locRight = conflictingChunk.getEndLine() - conflictingChunk.getSeparatorLine() - 1;

                        String generalkindConflictOutmost = conflictingChunk.getGeneralKindConflictOutmost();
                        int amountLanguageConstructs = generalkindConflictOutmost.split(",").length;

                        projectSummarization.add(new ProjectOverview(sha1, fileName, ccIdentifier, developerDecision, conflictingChunk.getId(),
                                locLeft, locRight, generalkindConflictOutmost, amountLanguageConstructs));
                    }
                }

            }
            System.out.println(new Date());

        } catch (SQLException ex) {
            Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return PagesName.projectOverview + "?faces-redirect=true";
    }

    public String allProjectsOverview() {

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            System.out.println(new Date());
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);
            RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(connection);
            ConflictingFileJDBCDAO conflictingFileDAO = new ConflictingFileJDBCDAO(connection);
            ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);
            KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO(connection);

            List<Project> projects = null;
            try {
//                project = projectDAO.selectAllByProjectId(projectId);

                projects = projectDAO.select();

                for (Project project : projects) {

                    List<Revision> revisions = revisionDAO.selectByProjectId(project.getId());

                    for (Revision revision : revisions) {
                        List<ConflictingFile> conflictingFiles = conflictingFileDAO.selectByRevisionId(revision.getId());

                        for (ConflictingFile conflictingFile : conflictingFiles) {
                            List<ConflictingChunk> conflictingChunks = conflictingChunkDAO.selectByConflictingFileId(conflictingFile.getId());
                            conflictingFile.setConflictingChunks(conflictingChunks);
                        }
                        revision.setConflictingFiles(conflictingFiles);
                    }
                    project.setRevisions(revisions);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            projectsSummarization = new ArrayList<>();

            for (Project project : projects) {

                System.out.println(projects.indexOf(project)+ "/"+projects.size() + " - "+project.getName() + " ("+ (new Date()) + ")");
                
                projectName = project.getName();
                String sha1 = "";
                String fileName = "";
                String ccIdentifier = "";
                String developerDecision = "";
//            String kindConflict = "";

                for (Revision revision : project.getRevisions()) {
                    sha1 = revision.getSha();

                    for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
                        fileName = conflictingFile.getName();

                        if (!fileName.toLowerCase().endsWith(".java")) {
                            continue;
                        }

                        for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
                            ccIdentifier = conflictingChunk.getIdentifier();
                            developerDecision = conflictingChunk.getDeveloperDecision().toString();
                            //Extracting LOC
                            int locLeft = conflictingChunk.getSeparatorLine() - conflictingChunk.getBeginLine() - 1;
                            int locRight = conflictingChunk.getEndLine() - conflictingChunk.getSeparatorLine() - 1;

                            String generalkindConflictOutmost = conflictingChunk.getGeneralKindConflictOutmost();
                            int amountLanguageConstructs = generalkindConflictOutmost.split(",").length;

                            projectsSummarization.add(new ProjectsOverview(projectName, sha1, fileName, ccIdentifier, developerDecision,
                                    conflictingChunk.getId(), generalkindConflictOutmost, locLeft, locRight, amountLanguageConstructs));
                        }
                    }

                }
            }
            System.out.println(new Date());

        } catch (SQLException ex) {
            Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return PagesName.projectsOverview + "?faces-redirect=true";
    }

    public String getStyle(MergeStatus mergeStatus) {
        if (mergeStatus == MergeStatus.CONFLICTING) {
            return "color: red";
        } else {
            return "color: green";
        }
    }

    public String format(String input) {
        if (input.length() > 30) {
            return input.substring(0, 29);
        } else {
            return input;
        }
    }

    /**
     * @return the root
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public String presenter() {

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            if (dataType.equals(DataTypes.CONFLICTING_CHUNK)) {
                ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);

                try {
                    selectedConflictingChunk = conflictingChunkDAO.selectAllByConflictingChunkId(Long.parseLong(selectedId));
                } catch (SQLException ex) {
                    Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
                }

                return PagesName.showConflicts;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AnalysisViewerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the selectedConflictingChunk
     */
    public ConflictingChunk getSelectedConflictingChunk() {
        return selectedConflictingChunk;
    }

    /**
     * @param selectedConflictingChunk the selectedConflictingChunk to set
     */
    public void setSelectedConflictingChunk(ConflictingChunk selectedConflictingChunk) {
        this.selectedConflictingChunk = selectedConflictingChunk;
    }

    /**
     * @return the selectedId
     */
    public String getSelectedId() {
        return selectedId;
    }

    /**
     * @param selectedId the selectedId to set
     */
    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the projectSummarization
     */
    public List<ProjectOverview> getProjectSummarization() {
        return projectSummarization;
    }

    /**
     * @param projectSummarization the projectSummarization to set
     */
    public void setProjectSummarization(List<ProjectOverview> projectSummarization) {
        this.projectSummarization = projectSummarization;
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
     * @return the conflictingChunkId
     */
    public Long getConflictingChunkId() {
        return conflictingChunkId;
    }

    /**
     * @param conflictingChunkId the conflictingChunkId to set
     */
    public void setConflictingChunkId(Long conflictingChunkId) {
        this.conflictingChunkId = conflictingChunkId;
    }

    /**
     * @return the projectsSummarization
     */
    public List<ProjectsOverview> getProjectsSummarization() {
        return projectsSummarization;
    }

    /**
     * @param projectsSummarization the projectsSummarization to set
     */
    public void setProjectsSummarization(List<ProjectsOverview> projectsSummarization) {
        this.projectsSummarization = projectsSummarization;
    }

}
