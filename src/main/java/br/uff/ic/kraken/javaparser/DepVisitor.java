/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.javaparser;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 *
 * @author gleiph
 */
public class DepVisitor extends ASTVisitor {

    private final CompilationUnit cu;

    private List<MethodDeclaration> methodDeclarations;

    private List<MethodInvocation> methodInvocations;

    private List<SimpleName> simpleNames;

    private String className;

    public DepVisitor(CompilationUnit cuArg) {

        this.cu = cuArg;

        this.methodDeclarations = new ArrayList<>();

        this.methodInvocations = new ArrayList<>();

        this.simpleNames = new ArrayList<>();

    }

    @Override
    public boolean visit(MethodInvocation node) {

        methodInvocations.add(node);

//        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
//        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
//        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {

        methodDeclarations.add(node);

//        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
//        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
//        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());
//
//        Block body = node.getBody();
//        LanguageConstructInformation languageConstructInformation;
//        if (body == null) {
//            languageConstructInformation = new LanguageConstructInformation(elementLineBegin, elementColumnBegin, elementLineEnd, elementColumnEnd);
//        } else {
//            int bodyLineBegin = cu.getLineNumber(body.getStartPosition());
//            int bodyLineEnd = cu.getLineNumber(body.getStartPosition() + body.getLength());
//            int bodyColumnBegin = cu.getColumnNumber(body.getStartPosition());
//            int bodyColumnEnd = cu.getColumnNumber(body.getStartPosition() + body.getLength());
//
//            languageConstructInformation = new LanguageConstructInformation(elementLineBegin, elementColumnBegin, elementLineEnd, elementColumnEnd,
//                    bodyLineBegin, bodyColumnBegin, bodyLineEnd, bodyColumnEnd);
//
//        }
//
//        methodDeclarationInformation.add(languageConstructInformation);
        return true;
    }

    @Override
    public boolean visit(SimpleName node) {

        simpleNames.add(node);
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }
        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }
        return true;
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

    /**
     * @return the methodInvocations
     */
    public List<MethodInvocation> getMethodInvocations() {
        return methodInvocations;
    }

    /**
     * @param methodInvocations the methodInvocations to set
     */
    public void setMethodInvocations(List<MethodInvocation> methodInvocations) {
        this.methodInvocations = methodInvocations;
    }

    /**
     * @return the simpleNames
     */
    public List<SimpleName> getSimpleNames() {
        return simpleNames;
    }

    /**
     * @param simpleNames the simpleNames to set
     */
    public void setSimpleNames(List<SimpleName> simpleNames) {
        this.simpleNames = simpleNames;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

}
