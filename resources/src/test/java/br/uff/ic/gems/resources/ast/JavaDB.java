/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.ast;

import br.uff.ic.gems.resources.analises.merge.AutomaticAnalysis;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author gleiph
 */
public class JavaDB {

    public JavaDB() {
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
    public void hello() throws Exception {
        String repoDir = "/Users/gleiph/Desktop/teste/rep";
        String projURL = "https://github.com/dustin/java-memcached-client";
        String outDir = "/Users/gleiph/Desktop/teste/out";
    
        AutomaticAnalysis.analyze(repoDir, projURL, outDir);
    
    }
}
