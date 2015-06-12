/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.dao.ConflictingFileDAO;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import java.util.List;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */
@Named(value = "conflictingChunkBean")
@RequestScoped
public class ConflictingChunkBean{

    private Long conflictingFileId;
    private List<ConflictingChunk> conflictingChunks;

    /**
     * @return the conflictingFileId
     */
    public Long getConflictingFileId() {
        return conflictingFileId;
    }

    /**
     * @param conflictingFileId the conflictingFileId to set
     */
    public void setConflictingFileId(Long conflictingFileId) {
        this.conflictingFileId = conflictingFileId;
    }

    /**
     * @return the conflictingChunks
     */
    public List<ConflictingChunk> getConflictingChunks() {
        return conflictingChunks;
    }

    /**
     * @param conflictingChunks the conflictingChunks to set
     */
    public void setConflictingChunks(List<ConflictingChunk> conflictingChunks) {
        this.conflictingChunks = conflictingChunks;
    }

    public String actionNavigation() {

        ConflictingFileDAO conflictingFileDAO = new ConflictingFileDAO();
        ConflictingFile conflictingFile = conflictingFileDAO.getById(conflictingFileId);

        if (conflictingFile == null) {
            return null;
        } else {
            conflictingChunks = conflictingFile.getConflictingChunks();
            return PagesName.conflictingChunks;
        }
    }
}
