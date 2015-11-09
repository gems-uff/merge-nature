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

    private String path;
    private final CompilationUnit cu;

    private List<ClassLanguageContructs> languageConstructsByLogicalClasses;

    private ClassLanguageContructs languageConstructsByLogicalClass;

    public DepVisitor(CompilationUnit cuArg, String path) {

        this.cu = cuArg;
        this.path = path;

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

        languageConstructsByLogicalClass = new ClassLanguageContructs(className, path);

        return true;
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        getLanguageConstructsByLogicalClasses().add(languageConstructsByLogicalClass);
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

        languageConstructsByLogicalClass = new ClassLanguageContructs(className, path);

        return false;
    }

    @Override
    public void endVisit(EnumDeclaration node) {
        getLanguageConstructsByLogicalClasses().add(languageConstructsByLogicalClass);
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

        languageConstructsByLogicalClass = new ClassLanguageContructs(className, path);

        return true;
    }

    @Override
    public void endVisit(AnnotationTypeDeclaration node) {
        getLanguageConstructsByLogicalClasses().add(languageConstructsByLogicalClass);
    }

    /**
     * @return the languageConstructsByLogicalClasses
     */
    public List<ClassLanguageContructs> getLanguageConstructsByLogicalClasses() {
        return languageConstructsByLogicalClasses;
    }

    /**
     * @param languageConstructsByLogicalClasses the
     * languageConstructsByLogicalClasses to set
     */
    public void setLanguageConstructsByLogicalClasses(List<ClassLanguageContructs> languageConstructsByLogicalClasses) {
        this.languageConstructsByLogicalClasses = languageConstructsByLogicalClasses;
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

}
