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
import br.uff.ic.gems.resources.data.dao.ConflictingChunkDAO;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import br.uff.ic.gems.resources.states.MergeStatus;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import br.uff.ic.kraken.projectviewer.utils.DataTypes;
import br.uff.ic.kraken.projectviewer.utils.ProjectAnalyses;
import br.uff.ic.kraken.projectviewer.utils.TreeTableNode;
import java.io.Serializable;
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
public class AnalysisViewerBean implements Serializable{

    private Long projectId;
    private List<Revision> revisions;
    private TreeNode root;

    private String selectedId;
    private String dataType;
    private ConflictingChunk selectedConflictingChunk;
    
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

        ProjectDAO projectDAO = new ProjectDAO();
        Project projectById = projectDAO.getById(projectId);

        revisions = projectById.getRevisions();

        if (getRevisions() != null && !revisions.isEmpty() && getRevisions().get(0) != null) {

            root = new DefaultTreeNode("root", null);

            for (Revision revision : revisions) {
                TreeNode rev = new DefaultTreeNode(new TreeTableNode(revision.getSha(), revision.getId(), DataTypes.REVISION), root);

                for (ConflictingFile conflictingFile : revision.getConflictingFiles()) {
                    TreeNode cf = new DefaultTreeNode(new TreeTableNode(conflictingFile.getName(), conflictingFile.getId(), DataTypes.CONFLICTING_FILE), rev);

                    for (ConflictingChunk conflictingChunk : conflictingFile.getConflictingChunks()) {
                        TreeNode cc = new DefaultTreeNode(new TreeTableNode(conflictingChunk.getIdentifier(), conflictingChunk.getId(), DataTypes.CONFLICTING_CHUNK), cf);
                    }
                }
            }

            return PagesName.showConflicts;
        } else {
            return null;
        }
    }

    public String analyze(){
        
        ProjectDAO projectDAO = new ProjectDAO();
        Project project = projectDAO.getById(projectId);

        String repositoriesPath = "/Users/gleiph/Desktop/repositories";
//        String repositoriesPath = "/home/gmenezes/repositories";
        
        ProjectAnalyses projectAnalyses = new ProjectAnalyses();
        projectAnalyses.analyze(repositoriesPath, project);
        
        return null;
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

        if (dataType.equals(DataTypes.CONFLICTING_CHUNK)) {
            ConflictingChunkDAO conflictingChunkDAO = new ConflictingChunkDAO();

            selectedConflictingChunk = conflictingChunkDAO.getById(Long.parseLong(selectedId));

            return PagesName.showConflicts;
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

}
