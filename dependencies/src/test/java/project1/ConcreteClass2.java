/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1;

/**
 *
 * @author gleiph
 */
public class ConcreteClass2 implements Interface, Interface2{

    @Override
    public void printHello() {
        System.out.println("Hello!");
    }

    @Override
    public void HelloPortuguese() {
        System.out.println("Olá!");
    }
    
}
