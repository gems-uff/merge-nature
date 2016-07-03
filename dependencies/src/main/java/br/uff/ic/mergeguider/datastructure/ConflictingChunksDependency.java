/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.datastructure;

import br.uff.ic.mergeguider.dependency.DependencyType;

/**
 *
 * @author gleiph
 */
public class ConflictingChunksDependency {
    
    private ConflictingChunkInformation reference;
    private ConflictingChunkInformation dependsOn;
    private DependencyType type;

    public ConflictingChunksDependency(ConflictingChunkInformation reference, ConflictingChunkInformation dependsOn, DependencyType type) {
        this.reference = reference;
        this.dependsOn = dependsOn;
        this.type = type;
    }

    /**
     * @return the reference
     */
    public ConflictingChunkInformation getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(ConflictingChunkInformation reference) {
        this.reference = reference;
    }

    /**
     * @return the dependsOn
     */
    public ConflictingChunkInformation getDependsOn() {
        return dependsOn;
    }

    /**
     * @param dependsOn the dependsOn to set
     */
    public void setDependsOn(ConflictingChunkInformation dependsOn) {
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
