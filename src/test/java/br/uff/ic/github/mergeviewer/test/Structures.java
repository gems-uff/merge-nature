/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.test;



/**
 *
 * @author gleiph
 */


public class Structures {
    
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
    
}
