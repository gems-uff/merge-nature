/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 *
 * @author gleiph
 */
public class MyVariableDeclaration {

    private VariableDeclarationFragment variableDeclaration;
    private SingleVariableDeclaration singleVariableDeclaration;

    private Location location;

    public MyVariableDeclaration(VariableDeclarationFragment variableDeclaration, Location location) {
        this.variableDeclaration = variableDeclaration;
        this.location = location;
    }

    public MyVariableDeclaration(SingleVariableDeclaration singleVariableDeclaration, Location location) {
        this.singleVariableDeclaration = singleVariableDeclaration;
        this.location = location;
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

    /**
     * @return the variableDeclaration
     */
    public VariableDeclarationFragment getVariableDeclaration() {
        return variableDeclaration;
    }

    /**
     * @param variableDeclaration the variableDeclaration to set
     */
    public void setVariableDeclaration(VariableDeclarationFragment variableDeclaration) {
        this.variableDeclaration = variableDeclaration;
    }

    /**
     * @return the singleVariableDeclaration
     */
    public SingleVariableDeclaration getSingleVariableDeclaration() {
        return singleVariableDeclaration;
    }

    /**
     * @param singleVariableDeclaration the singleVariableDeclaration to set
     */
    public void setSingleVariableDeclaration(SingleVariableDeclaration singleVariableDeclaration) {
        this.singleVariableDeclaration = singleVariableDeclaration;
    }

    public IVariableBinding resolveBinding() {
        if (this.singleVariableDeclaration != null) {
            return this.singleVariableDeclaration.resolveBinding();
        } else if (this.variableDeclaration != null) {
            return this.variableDeclaration.resolveBinding();
        } else {
            return null;
        }
    }

    public ITypeBinding resolveTypeBinding() {

        Type type = null;

        if (this.singleVariableDeclaration != null) {
            type = this.singleVariableDeclaration.getType();
        } else if (this.variableDeclaration != null) {
            ASTNode parent = this.variableDeclaration.getParent();

            if (parent instanceof VariableDeclarationStatement) {
                VariableDeclarationStatement variable = (VariableDeclarationStatement) parent;
                type = variable.getType();
            } else if (parent instanceof VariableDeclarationExpression) {
                VariableDeclarationExpression variable = (VariableDeclarationExpression) parent;
                type = variable.getType();
            }
        }

        if (type != null) {
            return type.resolveBinding();
        } else {
            return null;
        }
    }

    public String getName() {
        if (this.singleVariableDeclaration != null) {
            return this.singleVariableDeclaration.getName().getIdentifier();
        } else if (this.variableDeclaration != null) {
            return this.variableDeclaration.getName().getIdentifier();
        } else {
            return null;
        }
    }

}
