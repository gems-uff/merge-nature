/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.RevisionDAO;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import java.util.List;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */
@Named(value = "conflictingFileBean")
@RequestScoped
public class ConflictingFileBean {

    private List<ConflictingFile> conflictingFiles;
    private Long revisionId;

    /**
     * @return the conflictingFiles
     */
    public List<ConflictingFile> getConflictingFiles() {
        return conflictingFiles;
    }

    /**
     * @param conflictingFiles the conflictingFiles to set
     */
    public void setConflictingFiles(List<ConflictingFile> conflictingFiles) {
        this.conflictingFiles = conflictingFiles;
    }

    /**
     * @return the revisionId
     */
    public Long getRevisionId() {
        return revisionId;
    }

    /**
     * @param revisionId the revisionId to set
     */
    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public String actionNavigation() {
        RevisionDAO revisionDAO = new RevisionDAO();
        Revision revision = null;

        if (revisionId != null) {
            revision = revisionDAO.getById(revisionId);
        }

        if (revision == null) {
            return null;
        } else {
            conflictingFiles = revision.getConflictingFiles();
            return PagesName.conflictingFiles;
        }

    }

}
