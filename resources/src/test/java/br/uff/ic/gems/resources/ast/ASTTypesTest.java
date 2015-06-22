package br.uff.ic.gems.resources.ast;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gleiph
 */
public class ASTTypesTest {
    
    public static void main(String[] args) {
        
        int b = 0;
        
        int[] a = new int[5];
        
        a[0] = 1;
        a[1] = b;
        a[2] = 3;
        
        b = 5;
        
        a[3] = b;
        
    }
}
