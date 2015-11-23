/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.datastructure;

/**
 *
 * @author gleiph
 */
public class CCDependency {
    
    private int reference;
    private int dependsOn;

    public CCDependency(int reference, int dependsOn) {
        this.reference = reference;
        this.dependsOn = dependsOn;
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
    
    
    
}
