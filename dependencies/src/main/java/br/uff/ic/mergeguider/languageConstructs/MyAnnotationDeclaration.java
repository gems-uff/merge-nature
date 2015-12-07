/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 *
 * @author gleiph
 */
public class MyAnnotationDeclaration {
    
    private AnnotationTypeDeclaration annotationTypeDeclaration;
    private Location location;

    public MyAnnotationDeclaration(AnnotationTypeDeclaration annotationTypeDeclaration, Location location) {
        this.annotationTypeDeclaration = annotationTypeDeclaration;
        this.location = location;
    }

    /**
     * @return the annotationTypeDeclaration
     */
    public AnnotationTypeDeclaration getAnnotationTypeDeclaration() {
        return annotationTypeDeclaration;
    }

    /**
     * @param annotationTypeDeclaration the annotationTypeDeclaration to set
     */
    public void setAnnotationTypeDeclaration(AnnotationTypeDeclaration annotationTypeDeclaration) {
        this.annotationTypeDeclaration = annotationTypeDeclaration;
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

    public ITypeBinding resolveTypeBinding() {
        return annotationTypeDeclaration.resolveBinding();
    }

}
