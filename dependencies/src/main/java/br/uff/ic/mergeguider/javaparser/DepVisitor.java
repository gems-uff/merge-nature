/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.javaparser;

import br.uff.ic.mergeguider.languageConstructs.Location;
import br.uff.ic.mergeguider.languageConstructs.MyAttributeDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyAttributeCall;
import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodInvocation;
import br.uff.ic.mergeguider.languageConstructs.MyTypeDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyVariableCall;
import br.uff.ic.mergeguider.languageConstructs.MyVariableDeclaration;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 *
 * @author gleiph
 */
public class DepVisitor extends ASTVisitor {

    private String path;
    private final CompilationUnit cu;

    private List<ClassLanguageContructs> classesLanguageConstructs;

    private ClassLanguageContructs classLanguageContructs;

    private List<ClassLanguageContructs> classLanguageConstructsList;

    private List<List<SimpleName>> simpleNamesList;
    private List<SimpleName> simpleNames;

    public DepVisitor(CompilationUnit cuArg, String path) {

        this.cu = cuArg;
        this.path = path;

        this.classesLanguageConstructs = new ArrayList<>();
        this.classLanguageContructs = null;
        this.classLanguageConstructsList = new ArrayList<>();

        this.simpleNames = new ArrayList<>();
        this.simpleNamesList = new ArrayList<>();

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //  Treating Language Constructs
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //Treating method calls
    @Override
    public boolean visit(MethodInvocation node) {

        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        Location location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

        MyMethodInvocation myMethodInvocation = new MyMethodInvocation(node, location);

        classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getMethodInvocations().add(myMethodInvocation);

        return true;
    }

    //Treating method declarations
    @Override
    public boolean visit(MethodDeclaration node) {

        Location location;

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

        classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getMethodDeclarations().add(myMethodDeclaration);

        return true;
    }

    //Treating variables
    @Override
    public boolean visit(VariableDeclarationFragment node) {
        return true;
    }

    //Treating attributes
    @Override
    public boolean visit(FieldDeclaration node) {

        Location location;

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {

            int elementLineBegin = cu.getLineNumber(fragment.getStartPosition());
            int elementLineEnd = cu.getLineNumber(fragment.getStartPosition() + node.getLength());
            int elementColumnBegin = cu.getColumnNumber(fragment.getStartPosition());
            int elementColumnEnd = cu.getColumnNumber(fragment.getStartPosition() + node.getLength());

            location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

            MyAttributeDeclaration myAttribute = new MyAttributeDeclaration(fragment, location);

            classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getAttributes().add(myAttribute);

        }

        return true;
    }

    //Treating SimpleName that can be: Attribute or Variable
    @Override
    public boolean visit(SimpleName node) {

        IBinding binding = node.resolveBinding();

        if (binding instanceof IVariableBinding) {
            simpleNamesList.get(simpleNamesList.size() - 1).add(node);
        }
//        else if (binding instanceof IAnnotationBinding) {
//            System.out.println("Not trated yet!");
//        } else if (binding instanceof IMemberValuePairBinding) {
//            System.out.println("Not trated yet!");
//        } else if (binding instanceof IMethodBinding) {
//            System.out.println("Not trated yet!");
//        } else if (binding instanceof IPackageBinding) {
//            System.out.println("Not trated yet!");
//        } else if (binding instanceof ITypeBinding) {
//            simpleNamesList.get(simpleNamesList.size() - 1).add(node);
//        }

        return true;
    }

    public void treatingSimpleNames(List<SimpleName> nodes) {
        for (SimpleName node : nodes) {
            treatingSimpleName(node);
        }
    }

    public void treatingSimpleName(SimpleName node) {
        IBinding simpleNameBinding = node.resolveBinding();

        if (simpleNameBinding == null) {
            return;
        }

        //Treating Variable or Attribute
        if (simpleNameBinding instanceof IVariableBinding
                || simpleNameBinding instanceof ITypeBinding) {

            //Treating Attribute
            for (MyAttributeDeclaration attribute : classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getAttributes()) {

                IVariableBinding attributeBinding = attribute.getFieldDeclaration().resolveBinding();
                if (attributeBinding == null) {
                    continue;
                }

                if (simpleNameBinding.equals(attributeBinding)) {

                    int elementLineBegin = cu.getLineNumber(node.getStartPosition());
                    int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
                    int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
                    int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

                    Location location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

                    if (!attribute.getLocation().contains(location)) {

                        MyAttributeCall myAttributeCall = new MyAttributeCall(node, location);

                        classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getAttributeCalls().add(myAttributeCall);
                    }
                }

            }

            //Treating variables
            for (MyVariableDeclaration variableDeclaration : classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getVariableDeclarations()) {

                IVariableBinding attributeBinding = variableDeclaration.resolveBinding();
                if (attributeBinding == null) {
                    continue;
                }

                if (simpleNameBinding.equals(attributeBinding)) {

                    int elementLineBegin = cu.getLineNumber(node.getStartPosition());
                    int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
                    int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
                    int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

                    Location location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

                    if (!variableDeclaration.getLocation().contains(location)) {

                        MyVariableCall myVariableCall = new MyVariableCall(node, location);

                        classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getVariableCalls().add(myVariableCall);
                    }
                }

            }
        }
    }

    //Treating imports
    @Override
    public boolean visit(ImportDeclaration node) {
        //Treating cases of imports to static attributes
        return false;
    }

    //Treating annotations like @javax.xml.bind.annotation.XmlSchema(namespace = "http://gov.nasa.arc.mct", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
    @Override
    public boolean visit(NormalAnnotation node) {
        return false;
    }

    //Treating Variables 
    @Override
    public boolean visit(SingleVariableDeclaration node) {
        Location location;

        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

        MyVariableDeclaration myVariableDeclaration = new MyVariableDeclaration(node, location);

        classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getVariableDeclarations().add(myVariableDeclaration);

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {

        Location location;

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {

            int elementLineBegin = cu.getLineNumber(fragment.getStartPosition());
            int elementLineEnd = cu.getLineNumber(fragment.getStartPosition() + node.getLength());
            int elementColumnBegin = cu.getColumnNumber(fragment.getStartPosition());
            int elementColumnEnd = cu.getColumnNumber(fragment.getStartPosition() + node.getLength());

            location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

            MyVariableDeclaration myVariableDeclaration = new MyVariableDeclaration(fragment, location);

            classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getVariableDeclarations().add(myVariableDeclaration);

        }

        return true;

    }
    
    @Override
    public boolean visit(VariableDeclarationStatement node) {

        Location location;

        List<VariableDeclarationFragment> fragments = node.fragments();

        for (VariableDeclarationFragment fragment : fragments) {

            int elementLineBegin = cu.getLineNumber(fragment.getStartPosition());
            int elementLineEnd = cu.getLineNumber(fragment.getStartPosition() + node.getLength());
            int elementColumnBegin = cu.getColumnNumber(fragment.getStartPosition());
            int elementColumnEnd = cu.getColumnNumber(fragment.getStartPosition() + node.getLength());

            location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

            MyVariableDeclaration myVariableDeclaration = new MyVariableDeclaration(fragment, location);

            classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getVariableDeclarations().add(myVariableDeclaration);

        }

        return true;

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //  Treating cases where a new type, annotation, or Enum declaration starts
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @Override
    public boolean visit(TypeDeclaration node) {

        //Treating class name
        String className;
        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }

        classLanguageContructs = new ClassLanguageContructs(className, path);

        classLanguageConstructsList.add(classLanguageContructs);

        simpleNames = new ArrayList<>();
        simpleNamesList.add(simpleNames);

        //Treating location and structure
        Location location;

        int elementLineBegin = cu.getLineNumber(node.getStartPosition());
        int elementLineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
        int elementColumnBegin = cu.getColumnNumber(node.getStartPosition());
        int elementColumnEnd = cu.getColumnNumber(node.getStartPosition() + node.getLength());

        location = new Location(elementLineBegin, elementLineEnd, elementColumnBegin, elementColumnEnd);

        MyTypeDeclaration typeDeclaration = new MyTypeDeclaration(node, location);

        classLanguageConstructsList.get(classLanguageConstructsList.size() - 1).getTypeDeclarations().add(typeDeclaration);

        return true;
    }

    @Override
    public void endVisit(TypeDeclaration node) {

        treatingSimpleNames(simpleNamesList.remove(simpleNamesList.size() - 1));

        if (!classLanguageConstructsList.isEmpty()) {
            getClassesLanguageConstructs().add(classLanguageConstructsList.remove(classLanguageConstructsList.size() - 1));
        }

    }

    @Override
    public boolean visit(EnumDeclaration node) {

        String className;

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }

        classLanguageContructs = new ClassLanguageContructs(className, path);
        classLanguageConstructsList.add(classLanguageContructs);

        simpleNames = new ArrayList<>();
        simpleNamesList.add(simpleNames);

        return true;
    }

    @Override
    public void endVisit(EnumDeclaration node) {

        treatingSimpleNames(simpleNamesList.remove(simpleNamesList.size() - 1));

        if (!classLanguageConstructsList.isEmpty()) {
            getClassesLanguageConstructs().add(classLanguageConstructsList.remove(classLanguageConstructsList.size() - 1));
        }

    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {

        String className;

        PackageDeclaration aPackage = cu.getPackage();
        if (aPackage != null) {
            String packageName = aPackage.getName().getFullyQualifiedName();
            className = packageName + "." + node.getName().getIdentifier();
        } else {
            className = null;
        }

        classLanguageContructs = new ClassLanguageContructs(className, path);
        classLanguageConstructsList.add(classLanguageContructs);

        simpleNames = new ArrayList<>();
        simpleNamesList.add(simpleNames);

        return true;
    }

    @Override
    public void endVisit(AnnotationTypeDeclaration node) {

        treatingSimpleNames(simpleNamesList.remove(simpleNamesList.size() - 1));

        if (!classLanguageConstructsList.isEmpty()) {
            getClassesLanguageConstructs().add(classLanguageConstructsList.remove(classLanguageConstructsList.size() - 1));
        }

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //  Getters and Setters
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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
