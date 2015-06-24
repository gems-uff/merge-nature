/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.ast;

import br.uff.ic.gems.resources.data.LanguageConstruct;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author gleiph
 */
public class ASTTest {
    
    public ASTTest() {
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
    public void test() throws IOException{
        ASTExtractor aSTExtractor = new ASTExtractor("src/test/java/br/uff/ic/gems/resources/ast/ASTTypesTest.java");
        aSTExtractor.parser();
        List<LanguageConstruct> languageConstructs = aSTExtractor.getLanguageConstructs(0, 53);
        
        for (LanguageConstruct languageConstruct : languageConstructs) {
            System.out.println("languageConstruct = " + languageConstruct.toString());
        }
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
