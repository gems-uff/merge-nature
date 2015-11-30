/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 *
 * @author gmenezes
 */
public class MyAttributeDeclaration {

    private VariableDeclarationFragment fieldDeclaration;
    private Location location;

    public MyAttributeDeclaration(VariableDeclarationFragment fieldDeclaration, Location location) {
        this.fieldDeclaration = fieldDeclaration;
        this.location = location;
    }

    /**
     * @return the fieldDeclaration
     */
    public VariableDeclarationFragment getFieldDeclaration() {
        return fieldDeclaration;
    }

    /**
     * @param fieldDeclaration the fieldDeclaration to set
     */
    public void setFieldDeclaration(VariableDeclarationFragment fieldDeclaration) {
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

    public IVariableBinding resolveBinding() {
        return this.fieldDeclaration.resolveBinding();
    }

    public ITypeBinding resolveTypeBinding() {
        ASTNode parent = this.getFieldDeclaration().getParent();

        if (parent instanceof FieldDeclaration) {
            FieldDeclaration type = (FieldDeclaration) parent;
            return type.getType().resolveBinding();
        }
        
        return null;
    }

}
