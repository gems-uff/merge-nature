/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.dependency.DependencyType;

/**
 *
 * @author Gleiph, Cristiane
 */
public class ChunksDependency {
    
    private ChunkInformation reference;
    private ChunkInformation dependsOn;
    private DependencyType type;

    public ChunksDependency(ChunkInformation reference, ChunkInformation dependsOn, DependencyType type) {
        this.reference = reference;
        this.dependsOn = dependsOn;
        this.type = type;
    }

    /**
     * @return the reference
     */
    public ChunkInformation getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(ChunkInformation reference) {
        this.reference = reference;
    }

    /**
     * @return the dependsOn
     */
    public ChunkInformation getDependsOn() {
        return dependsOn;
    }

    /**
     * @param dependsOn the dependsOn to set
     */
    public void setDependsOn(ChunkInformation dependsOn) {
        this.dependsOn = dependsOn;
    }

    /**
     * @return the type
     */
    public DependencyType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DependencyType type) {
        this.type = type;
    }
    
}

