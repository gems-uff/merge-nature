/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.FieldDeclaration;

/**
 *
 * @author gmenezes
 */
public class MyAttribute {
    
    private FieldDeclaration fieldDeclaration;
    private Location location;

    public MyAttribute(FieldDeclaration fieldDeclaration, Location location) {
        this.fieldDeclaration = fieldDeclaration;
        this.location = location;
    }

    /**
     * @return the fieldDeclaration
     */
    public FieldDeclaration getFieldDeclaration() {
        return fieldDeclaration;
    }

    /**
     * @param fieldDeclaration the fieldDeclaration to set
     */
    public void setFieldDeclaration(FieldDeclaration fieldDeclaration) {
        this.fieldDeclaration = fieldDeclaration;
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
