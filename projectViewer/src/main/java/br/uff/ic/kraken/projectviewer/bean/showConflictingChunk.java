/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.ConflictingChunk;

/**
 *
 * @author gleiph
 */
public class showConflictingChunk {
    
    private ConflictingChunk conflictingChunk;
    private Long conflictingChunkId;
    
    /**
     * @return the conflictingChunk
     */
    public ConflictingChunk getConflictingChunk() {
        return conflictingChunk;
    }

    /**
     * @param conflictingChunk the conflictingChunk to set
     */
    public void setConflictingChunk(ConflictingChunk conflictingChunk) {
        this.conflictingChunk = conflictingChunk;
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
    
    
    
}
