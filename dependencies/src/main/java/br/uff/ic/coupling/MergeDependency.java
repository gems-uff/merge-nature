/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cristiane
 */
public class MergeDependency {
    
    private int ChunksAmount;
    private List<ChunksDependency> ChunksDependencies;
    private List<ChunkInformation> cis;

    public MergeDependency() {
        ChunksAmount = 0;
        ChunksDependencies = new ArrayList<>();
        cis = new ArrayList<>();
    }

    
    
    /**
     * @return the ChunksAmount
     */
    public int getChunksAmount() {
        return ChunksAmount;
    }

    /**
     * @param ChunksAmount the ChunksAmount to set
     */
    public void setChunksAmount(int ChunksAmount) {
        this.ChunksAmount = ChunksAmount;
    }

    /**
     * @return the ChunksDependencies
     */
    public List<ChunksDependency> getChunksDependencies() {
        return ChunksDependencies;
    }

    /**
     * @param ChunksDependencies the ChunksDependencies to set
     */
    public void setChunksDependencies(List<ChunksDependency> ChunksDependencies) {
        this.ChunksDependencies = ChunksDependencies;
    }

    /**
     * @return the ccis
     */
    public List<ChunkInformation> getCis() {
        return cis;
    }

    /**
     * @param cis the ccis to set
     */
    public void setCis(List<ChunkInformation> cis) {
        this.cis = cis;
    }

}
