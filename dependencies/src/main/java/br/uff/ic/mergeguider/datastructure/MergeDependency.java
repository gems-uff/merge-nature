/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class MergeDependency {
    
    private int conflictingChunksAmount;
    private List<ConflictingChunksDependency> conflictingChunksDependencies;

    public MergeDependency() {
        conflictingChunksAmount = 0;
        conflictingChunksDependencies = new ArrayList<>();
    }

    
    
    /**
     * @return the conflictingChunksAmount
     */
    public int getConflictingChunksAmount() {
        return conflictingChunksAmount;
    }

    /**
     * @param conflictingChunksAmount the conflictingChunksAmount to set
     */
    public void setConflictingChunksAmount(int conflictingChunksAmount) {
        this.conflictingChunksAmount = conflictingChunksAmount;
    }

    /**
     * @return the conflictingChunksDependencies
     */
    public List<ConflictingChunksDependency> getConflictingChunksDependencies() {
        return conflictingChunksDependencies;
    }

    /**
     * @param conflictingChunksDependencies the conflictingChunksDependencies to set
     */
    public void setConflictingChunksDependencies(List<ConflictingChunksDependency> conflictingChunksDependencies) {
        this.conflictingChunksDependencies = conflictingChunksDependencies;
    }

}
