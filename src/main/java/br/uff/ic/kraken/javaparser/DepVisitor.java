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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 *
 * @author gleiph
 */
public class DepVisitor extends ASTVisitor {

    private final CompilationUnit cu;

    private List<LanguageConstructsByLogicalClass> languageConstructsByLogicalClasses;

    private LanguageConstructsByLogicalClass languageConstructsByLogicalClass;

    public DepVisitor(CompilationUnit cuArg) {

        this.cu = cuArg;

        this.languageConstructsByLogicalClasses = new ArrayList<>();
        this.languageConstructsByLogicalClass = null;

    }

    @Override
    public boolean visit(MethodInvocation node) {

        languageConstructsByLogicalClass.getMethodInvocations().add(node);

//        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
//        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
//        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
//        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {

        languageConstructsByLogicalClass.getMethodDeclarations().add(node);

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
    public boolean visit(TypeDeclaration node) {

        String className = null;
        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }

        if (languageConstructsByLogicalClass != null) {
            getLanguageConstructsByLogicalClasses().add(languageConstructsByLogicalClass);
        }

        languageConstructsByLogicalClass = new LanguageConstructsByLogicalClass(className);

        return true;
    }

    @Override
    public boolean visit(EnumDeclaration node) {

        String className = null;

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }
        return false;
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {

        String className = null;

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }

        languageConstructsByLogicalClass = new LanguageConstructsByLogicalClass(className);

        return true;
    }

    /**
     * @return the languageConstructsByLogicalClasses
     */
    public List<LanguageConstructsByLogicalClass> getLanguageConstructsByLogicalClasses() {
        return languageConstructsByLogicalClasses;
    }

    /**
     * @param languageConstructsByLogicalClasses the languageConstructsByLogicalClasses to set
     */
    public void setLanguageConstructsByLogicalClasses(List<LanguageConstructsByLogicalClass> languageConstructsByLogicalClasses) {
        this.languageConstructsByLogicalClasses = languageConstructsByLogicalClasses;
    }

}
