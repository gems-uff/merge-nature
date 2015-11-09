/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.javaparser;

import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodInvocation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ClassLanguageContructs {

    private String path;
    private String qualifiedName;

    private List<MyMethodDeclaration> methodDeclarations;
    private List<MyMethodInvocation> methodInvocations;

    public ClassLanguageContructs(String qualifiedName, String path) {
        this.qualifiedName = qualifiedName;
        this.path = path;
        
        methodDeclarations = new ArrayList<>();
        methodInvocations = new ArrayList<>();
        
    }

    /**
     * @return the qualifiedName
     */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * @param qualifiedName the qualifiedName to set
     */
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    /**
     * @return the methodDeclarations
     */
    public List<MyMethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    /**
     * @param methodDeclarations the methodDeclarations to set
     */
    public void setMethodDeclarations(List<MyMethodDeclaration> methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
    }

    public boolean addMethodDeclaration(MyMethodDeclaration methodDeclaration){
        return methodDeclarations.add(methodDeclaration);
    }
    
    /**
     * @return the methoInvocations
     */
    public List<MyMethodInvocation> getMethodInvocations() {
        return methodInvocations;
    }

    /**
     * @param methoInvocations the methoInvocations to set
     */
    public void setMethodInvocations(List<MyMethodInvocation> methoInvocations) {
        this.methodInvocations = methoInvocations;
    }

    public boolean addMethodInvocation(MyMethodInvocation methodInvocation){
        return methodInvocations.add(methodInvocation);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
    
    
}
