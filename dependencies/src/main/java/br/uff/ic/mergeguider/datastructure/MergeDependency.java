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
    private List<ConflictingChunkInformation> ccis;

    public MergeDependency() {
        conflictingChunksAmount = 0;
        conflictingChunksDependencies = new ArrayList<>();
        ccis = new ArrayList<>();
    }

    public List<ConflictingChunkInformation> getDependencies(ConflictingChunkInformation chunk){
        List<ConflictingChunkInformation> result = new ArrayList<>();
        for (ConflictingChunksDependency conflictingChunksDependency : conflictingChunksDependencies) {
            if(conflictingChunksDependency.getReference().equals(chunk)){
                result.add(conflictingChunksDependency.getDependsOn());
            }
        }
        
        return result;
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

    /**
     * @return the ccis
     */
    public List<ConflictingChunkInformation> getCcis() {
        return ccis;
    }

    /**
     * @param ccis the ccis to set
     */
    public void setCcis(List<ConflictingChunkInformation> ccis) {
        this.ccis = ccis;
    }

}
