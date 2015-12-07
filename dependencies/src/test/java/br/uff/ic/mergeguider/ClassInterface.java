/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.ProjectAST;
import br.uff.ic.mergeguider.languageConstructs.MyTypeDeclaration;
import java.util.List;
import org.eclipse.jdt.core.dom.SimpleType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gleiph
 */
public class ClassInterface {

    public ClassInterface() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getInterface() {

        boolean result = true;
        ProjectAST projectAST = new ProjectAST("src/test/java/project1");

        for (ClassLanguageContructs classesLanguageConstruct : projectAST.getClassesLanguageConstructs()) {
            List<MyTypeDeclaration> typeDeclarations = classesLanguageConstruct.getTypeDeclarations();

            for (MyTypeDeclaration typeDeclaration : typeDeclarations) {
                if (typeDeclaration.getTypeDeclaration().getName().getIdentifier().equals("ConcreteClass")) {
                    List<SimpleType> interfaces = typeDeclaration.getInterfaces();
                    if (interfaces.size() == 1 && interfaces.get(0).getName().getFullyQualifiedName().equals("Interface")) {
                        result = false;
                    }
                }
            }
        }

        assertFalse(result);
    }

    @Test
    public void getInterfaces() {

        boolean result = true;
        ProjectAST projectAST = new ProjectAST("src/test/java/project1");

        for (ClassLanguageContructs classesLanguageConstruct : projectAST.getClassesLanguageConstructs()) {
            List<MyTypeDeclaration> typeDeclarations = classesLanguageConstruct.getTypeDeclarations();

            for (MyTypeDeclaration typeDeclaration : typeDeclarations) {
                if (typeDeclaration.getTypeDeclaration().getName().getIdentifier().equals("ConcreteClass2")) {
                    List<SimpleType> interfaces = typeDeclaration.getInterfaces();
                    if (interfaces.size() == 2 && interfaces.get(0).getName().getFullyQualifiedName().equals("Interface")
                            && interfaces.get(1).getName().getFullyQualifiedName().equals("Interface2")) {
                        result = false;
                    }
                }
            }
        }

        assertFalse(result);
    }

    
    
}
