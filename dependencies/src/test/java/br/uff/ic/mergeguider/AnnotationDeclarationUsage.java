/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.ProjectAST;
import br.uff.ic.mergeguider.languageConstructs.MyAnnotationDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyAnnotationUsage;
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
public class AnnotationDeclarationUsage {

    public AnnotationDeclarationUsage() {
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
    public void annotationRelation() {
        boolean result = true;
        ProjectAST projectAST = new ProjectAST("src/test/java/project1");

        for (ClassLanguageContructs classesLanguageConstruct : projectAST.getClassesLanguageConstructs()) {
            List<MyAnnotationDeclaration> annotationDeclarations = classesLanguageConstruct.getAnnotationDeclarations();
            if (!classesLanguageConstruct.getAnnotationDeclarations().isEmpty()) {
                MyAnnotationDeclaration annotationDeclaration = annotationDeclarations.get(0);

                for (ClassLanguageContructs classesLanguageConstruct1 : projectAST.getClassesLanguageConstructs()) {
                    for (MyAnnotationUsage annotationUsage : classesLanguageConstruct1.getAnnotationUsages()) {
                        if (annotationDeclaration.resolveTypeBinding().equals(annotationUsage.resolveTypeBinding())) {
                            result = false;
                        }
                    }
                }

            }
        }
        assertFalse(result);
    }
}
