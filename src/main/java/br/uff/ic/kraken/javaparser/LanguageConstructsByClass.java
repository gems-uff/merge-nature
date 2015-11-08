/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.javaparser;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 *
 * @author gleiph
 */
public class LanguageConstructsByClass {

    private String qualifiedName;

    private List<MethodDeclaration> methodDeclarations;
    private List<MethodInvocation> methoInvocations;

    public LanguageConstructsByClass(String qualifiedName) {
        this.qualifiedName = qualifiedName;

        methodDeclarations = new ArrayList<>();
        methoInvocations = new ArrayList<>();
        
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
    public List<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    /**
     * @param methodDeclarations the methodDeclarations to set
     */
    public void setMethodDeclarations(List<MethodDeclaration> methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
    }

    public boolean addMethodDeclaration(MethodDeclaration methodDeclaration){
        return methodDeclarations.add(methodDeclaration);
    }
    
    /**
     * @return the methoInvocations
     */
    public List<MethodInvocation> getMethoInvocations() {
        return methoInvocations;
    }

    /**
     * @param methoInvocations the methoInvocations to set
     */
    public void setMethoInvocations(List<MethodInvocation> methoInvocations) {
        this.methoInvocations = methoInvocations;
    }

    public boolean addMethodInvocation(MethodInvocation methodInvocation){
        return methoInvocations.add(methodInvocation);
    }
    
}
