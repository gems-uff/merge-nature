/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import br.uff.ic.gems.resources.states.MergeStatus;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import java.util.List;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */
@Named(value = "analysisViewerBean")
@RequestScoped
public class AnalysisViewerBean {

    private Long projectId;
    private List<Revision> revisions;

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

    public String getStyle(MergeStatus mergeStatus) {
        if (mergeStatus == MergeStatus.CONFLICTING) {
            return "color: red";
        } else {
            return "color: green";
        }
    }
}
