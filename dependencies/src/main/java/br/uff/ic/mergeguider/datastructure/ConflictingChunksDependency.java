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
    
    private int reference;
    private int dependsOn;
    private DependencyType type;

    public ConflictingChunksDependency(int reference, int dependsOn, DependencyType type) {
        this.reference = reference;
        this.dependsOn = dependsOn;
        this.type = type;
    }

    /**
     * @return the reference
     */
    public int getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(int reference) {
        this.reference = reference;
    }

    /**
     * @return the dependsOn
     */
    public int getDependsOn() {
        return dependsOn;
    }

    /**
     * @param dependsOn the dependsOn to set
     */
    public void setDependsOn(int dependsOn) {
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
