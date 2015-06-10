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
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import br.uff.ic.gems.resources.states.MergeStatus;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import java.util.List;
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
public class AnalysisViewerBean {

    private Long projectId;
    private List<Revision> revisions;

    private TreeNode root;

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

    public String analysisData() {

        ProjectDAO projectDAO = new ProjectDAO();
        Project projectById = projectDAO.getById(projectId);

        setRevisions(projectById.getRevisions());

        if (getRevisions() != null && !revisions.isEmpty() && getRevisions().get(0) != null) {
            return PagesName.projectAnalysis;
        } else {
            return null;
        }
    }

    public String actionNavigator() {

        ProjectDAO projectDAO = new ProjectDAO();
        Project projectById = projectDAO.getById(projectId);

        revisions = projectById.getRevisions();

        if (getRevisions() != null && !revisions.isEmpty() && getRevisions().get(0) != null) {

            setRoot(new DefaultTreeNode("root", null));

            for (Revision revision : revisions) {
                TreeNode rev = new DefaultTreeNode(revision.getSha(), getRoot());

                for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
                    TreeNode cf = new DefaultTreeNode(revision.getSha(), conflictingFile.getName(), rev);

                    for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
                        TreeNode cc = new DefaultTreeNode(conflictingFile.getName(), conflictingChunk.getIdentifier(), cf);
                    }
                }
            }

            return PagesName.showAll;
        } else {
            return null;
        }
    }

//    public String actionNavigator() {
//
//        ProjectDAO projectDAO = new ProjectDAO();
//        Project projectById = projectDAO.getById(projectId);
//
//        setRevisions(projectById.getRevisions());
//
//        if (getRevisions() != null && !revisions.isEmpty() && getRevisions().get(0) != null) {
//            return PagesName.showAll;
//        } else {
//            return null;
//        }
//    }
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

}
