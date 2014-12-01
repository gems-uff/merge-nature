/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.test;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author gleiph
 */


public class Structures {
    
    
    String name;
    private int age;
    public double grade;
    
    public void forStatement() {
        for (int i = 0; i < 10; i++) {
            System.out.println("i = "+ i);
        }
    }
    
    public void whileStatement() {
        int i;
        i = 0;
        while (i < 10) {
            System.out.println("i = "+ i);
            i++;
        }
    }
    
    public void ifStatement() {
        int i;
        i = 0;
        if (i < 10) {
            System.out.println("i = "+ i);
            i++;
        }
    }

    public static int returnStatement() {
        return 5;
    }
    
    public static int variableDeclarationStatement() {
        int j;
        j = 8;
        int k = 0;
        int v = returnStatement();
        
        return j +k + v;
    }
    
    public void throwStatement() throws Exception{
        throw new Exception("Testing AST");
    }
    
    public void enhancedForStatement(){
        List list = new ArrayList();
        list.add("a");
        
        for (Object l : list) {
            System.out.println(l);
        }
    }
}
