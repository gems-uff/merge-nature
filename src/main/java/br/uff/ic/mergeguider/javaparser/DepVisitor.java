/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.javaparser;

import br.uff.ic.mergeguider.languageConstructs.Location;
import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodInvocation;
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
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 *
 * @author gleiph
 */
public class DepVisitor extends ASTVisitor {

    private String path;
    private final CompilationUnit cu;

    private List<ClassLanguageContructs> classesLanguageConstructs;

    private ClassLanguageContructs classLanguageContructs;

    public DepVisitor(CompilationUnit cuArg, String path) {

        this.cu = cuArg;
        this.path = path;

        this.classesLanguageConstructs = new ArrayList<>();
        this.classLanguageContructs = null;

    }

    @Override
    public boolean visit(MethodInvocation node) {

        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Location location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

        MyMethodInvocation myMethodInvocation = new MyMethodInvocation(node, location);

        classLanguageContructs.getMethodInvocations().add(myMethodInvocation);

        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {

        Location location = null;
        
        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Block body = node.getBody();
        if (body == null) {
            location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);
        } else {
            int bodyLineBegin = cu.getLineNumber(body.getStartPosition());
            int bodyLineEnd = cu.getLineNumber(body.getStartPosition() + body.getLength());
            int bodyColumnBegin = cu.getColumnNumber(body.getStartPosition());
            int bodyColumnEnd = cu.getColumnNumber(body.getStartPosition() + body.getLength());

            location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd, 
                    bodyLineBegin, bodyLineEnd, bodyColumnBegin, bodyColumnEnd);

        }
        
        MyMethodDeclaration myMethodDeclaration = new MyMethodDeclaration(node, location);

        classLanguageContructs.getMethodDeclarations().add(myMethodDeclaration);

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

        classLanguageContructs = new ClassLanguageContructs(className, path);

        return true;
    }

    @Override
    public void endVisit(TypeDeclaration node) {
        getClassesLanguageConstructs().add(classLanguageContructs);
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

        classLanguageContructs = new ClassLanguageContructs(className, path);

        return false;
    }

    @Override
    public void endVisit(EnumDeclaration node) {
        getClassesLanguageConstructs().add(classLanguageContructs);
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

        classLanguageContructs = new ClassLanguageContructs(className, path);

        return true;
    }

    @Override
    public void endVisit(AnnotationTypeDeclaration node) {
        getClassesLanguageConstructs().add(classLanguageContructs);
    }

    /**
     * @return the classesLanguageConstructs
     */
    public List<ClassLanguageContructs> getClassesLanguageConstructs() {
        return classesLanguageConstructs;
    }

    /**
     * @param languageConstructsByLogicalClasses the classesLanguageConstructs
     * to set
     */
    public void setClassesLanguageConstructs(List<ClassLanguageContructs> languageConstructsByLogicalClasses) {
        this.classesLanguageConstructs = languageConstructsByLogicalClasses;
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
