/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ITypeBinding;
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

    public List<MyAttributeDeclaration> getAttributeCalls(List<ClassLanguageContructs> languageContructs) {

        List<MyAttributeDeclaration> calls = new ArrayList<>();

        for (ClassLanguageContructs languageContruct : languageContructs) {
            for (MyAttributeDeclaration attributeDeclaration : languageContruct.getAttributes()) {
                if (this.getTypeDeclaration().resolveBinding().equals(attributeDeclaration.resolveTypeBinding())) {
                    calls.add(attributeDeclaration);
                }
            }
        }

        return calls;
    }

    public List<MyVariableDeclaration> getVariableCalls(List<ClassLanguageContructs> languageContructs) {

        List<MyVariableDeclaration> calls = new ArrayList<>();

        for (ClassLanguageContructs languageContruct : languageContructs) {
            for (MyVariableDeclaration variableDeclaration : languageContruct.getVariableDeclarations()) {
                ITypeBinding typeBindingTypeDeclaration = this.getTypeDeclaration().resolveBinding();
                ITypeBinding typeBindingVariableDeclaration = variableDeclaration.resolveTypeBinding();
                if (typeBindingTypeDeclaration.equals(typeBindingVariableDeclaration)) {
                    calls.add(variableDeclaration);
                }
            }
        }

        return calls;
    }
}
