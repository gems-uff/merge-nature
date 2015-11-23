package br.uff.ic.mergeguider;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.JavaParser;
import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodInvocation;
import java.util.List;
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
public class MethodDeclarationCall {

    public MethodDeclarationCall() {
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
    public void Callers() {
        JavaParser javaParser = new JavaParser();
        List<ClassLanguageContructs> languageContructs = javaParser.parser("src/test/java/project1");

        for (ClassLanguageContructs languageContruct : languageContructs) {
            if (languageContruct.getMethodDeclarations() != null
                    && !languageContruct.getMethodDeclarations().isEmpty()) {
                for (MyMethodDeclaration methodDeclaration : languageContruct.getMethodDeclarations()) {
                    List<MyMethodInvocation> calls = methodDeclaration.getCalls(languageContructs);

                    if (methodDeclaration.getMethodDeclaration().getName().getIdentifier().equals("methodA")) {
                        
                        assertFalse(calls.isEmpty());
                        
                    }
                }
            }
        }

    }
}
