/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.javaparser;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 *
 * @author gleiph
 */
public class Dependencies {

    private String path;
    private List<LanguageConstructsByLogicalClass> languageConstructsByClasses;

    public Dependencies(String path) {
        this.path = path;
        JavaParser javaParser = new JavaParser();
        languageConstructsByClasses = javaParser.parser(path);
    }

    public List<MethodInvocation> getCalers(MethodDeclaration methodDeclaration) {
        List<MethodInvocation> invocations = new ArrayList<>();

        IMethodBinding methodDeclarationBinding = methodDeclaration.resolveBinding();
        if (methodDeclarationBinding == null) {
            System.out.println("\t\tMethod does not have binding! ");
            return null;
        }

        for (LanguageConstructsByLogicalClass languageConstructsByClass : languageConstructsByClasses) {
            for (MethodInvocation methoInvocation : languageConstructsByClass.getMethodInvocations()) {

                IMethodBinding methodInvocationBinding = methoInvocation.resolveMethodBinding();

                if (methodInvocationBinding == null) {
//                    System.out.println("Method invocation " + methodDeclaration.getName().getIdentifier() + " does not have binding! ");
                    continue;
                }

                if (methodDeclarationBinding.equals(methodInvocationBinding)) {
                    invocations.add(methoInvocation);
                    System.out.println("\t\tIs called in class " + languageConstructsByClass.getQualifiedName());
                }

            }
        }

        return invocations;
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

    /**
     * @return the languageConstructsByClasses
     */
    public List<LanguageConstructsByLogicalClass> getLanguageConstructsByClasses() {
        return languageConstructsByClasses;
    }

    /**
     * @param languageConstructsByClasses the languageConstructsByClasses to set
     */
    public void setLanguageConstructsByClasses(List<LanguageConstructsByLogicalClass> languageConstructsByClasses) {
        this.languageConstructsByClasses = languageConstructsByClasses;
    }

    public static void main(String[] args) {

        Dependencies dependencies = new Dependencies("/Users/gleiph/Dropbox/doutorado/repositories/lombok");

        for (LanguageConstructsByLogicalClass languageConstructsByClass : dependencies.getLanguageConstructsByClasses()) {

            String className = languageConstructsByClass.getQualifiedName();
            List<MethodDeclaration> methodDeclarations = languageConstructsByClass.getMethodDeclarations();

            System.out.println(className);
            for (MethodDeclaration methodDeclaration : methodDeclarations) {

                System.out.println("\t" + methodDeclaration.getName().getFullyQualifiedName());
                List<MethodInvocation> calers = dependencies.getCalers(methodDeclaration);

            }

        }

    }
}
