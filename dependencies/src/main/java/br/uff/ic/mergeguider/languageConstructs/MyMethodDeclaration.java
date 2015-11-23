/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 *
 * @author gleiph
 */
public class MyMethodDeclaration {

    private MethodDeclaration methodDeclaration;
    private Location location;

    public MyMethodDeclaration(MethodDeclaration methodDeclaration, Location location) {
        this.methodDeclaration = methodDeclaration;
        this.location = location;
    }

    /**
     * @return the methodDeclaration
     */
    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    /**
     * @param methodDeclaration the methodDeclaration to set
     */
    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
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

    @Override
    public String toString() {
        return methodDeclaration.getName().getIdentifier() + " " + location.toString();
    }

    public List<MyMethodInvocation> getCalls(List<ClassLanguageContructs> languageContructs){
        
        List<MyMethodInvocation> calls = new ArrayList<>();
        
        for (ClassLanguageContructs languageContruct : languageContructs) {
            for (MyMethodInvocation methodInvocation : languageContruct.getMethodInvocations()) {
                if(methodInvocation.getMethodInvocation().resolveMethodBinding().equals(this.getMethodDeclaration().resolveBinding()))
                    calls.add(methodInvocation);
            }
        }
        
        return calls;
    }
    
}
