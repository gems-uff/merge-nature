/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 *
 * @author gleiph
 */
public class MyTypeDeclaration {
    
    private TypeDeclaration typeDeclaration;
    private Location location;

    public MyTypeDeclaration(TypeDeclaration typeDeclaration, Location location) {
        this.typeDeclaration = typeDeclaration;
        this.location = location;
    }

    /**
     * @return the typeDeclaration
     */
    public TypeDeclaration getTypeDeclaration() {
        return typeDeclaration;
    }

    /**
     * @param typeDeclaration the typeDeclaration to set
     */
    public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }
    
}
