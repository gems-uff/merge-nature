/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.JavaParser;
import br.uff.ic.mergeguider.javaparser.ProjectAST;
import br.uff.ic.mergeguider.languageConstructs.MyAttributeCall;
import br.uff.ic.mergeguider.languageConstructs.MyAttributeDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyTypeDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyVariableDeclaration;
import java.util.List;
import org.eclipse.jdt.core.dom.SimpleName;
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
public class TypeDeclarationUsage {

    public TypeDeclarationUsage() {
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
    public void attributeCallers() {

        boolean result = true;
        ProjectAST projectAST = new ProjectAST("src/test/java/project1");

        List<ClassLanguageContructs> classesLanguageConstructs = projectAST.getClassesLanguageConstructs();

        for (ClassLanguageContructs classesLanguageConstruct : classesLanguageConstructs) {
            List<MyTypeDeclaration> typeDeclarations = classesLanguageConstruct.getTypeDeclarations();

            for (MyTypeDeclaration typeDeclaration : typeDeclarations) {

                String name = typeDeclaration.getTypeDeclaration().getName().getIdentifier();
                if (name.equals("ClassA")) {
                    List<MyAttributeDeclaration> attributeCalls = typeDeclaration.getAttributeCalls(classesLanguageConstructs);
                    if (attributeCalls.size() == 1 && attributeCalls.get(0).getFieldDeclaration().getName().getIdentifier().equals("attribute")) {
                        result = false;
                    }
                }
            }

        }
        assertFalse(result);

    }

    @Test
    public void variableCallers() {

        boolean result = true;
        ProjectAST projectAST = new ProjectAST("src/test/java/project1");

        List<ClassLanguageContructs> classesLanguageConstructs = projectAST.getClassesLanguageConstructs();

        for (ClassLanguageContructs classesLanguageConstruct : classesLanguageConstructs) {
            List<MyTypeDeclaration> typeDeclarations = classesLanguageConstruct.getTypeDeclarations();

            for (MyTypeDeclaration typeDeclaration : typeDeclarations) {

                String name = typeDeclaration.getTypeDeclaration().getName().getIdentifier();
                if (name.equals("ClassA")) {
                    List<MyVariableDeclaration> variableDeclarations = typeDeclaration.getVariableCalls(classesLanguageConstructs);
                    if (variableDeclarations.size() == 1 && variableDeclarations.get(0).getName().equals("classA")) {
                        result = false;
                    }
                }
            }

        }
        assertFalse(result);

    }
    
}
