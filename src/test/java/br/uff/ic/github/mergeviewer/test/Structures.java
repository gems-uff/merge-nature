/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.test;

import java.nio.file.FileVisitResult;
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

    public Structures() {
    }

    public Structures(String name, int age, double grade) {
        this.name = name;
        this.age = age;
        this.grade = grade;
    }

    public void forStatement() {
        for (int i = 0; i < 10; i++) {
            System.out.println("i = " + i);
        }
    }

    public void whileStatement() {
        int i;
        i = 0;
        while (i < 10) {
            System.out.println("i = " + i);
            i++;

            if (i == 0) {
                break;
            } else {
                continue;
            }
        }
    }

    public void ifStatement() {
        int i;
        i = 0;
        if (i < 10) {
            System.out.println("i = " + i);
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
        int v;
        v = returnStatement();

        return j + k + v;
    }

    public void throwStatement() throws Exception {
        throw new Exception("Testing AST");
    }

    public void enhancedForStatement() {
        List list = new ArrayList();
        list.add("a");

        for (Object l : list) {
            System.out.println(l);
        }
    }

    public void switchCase() {
        int i = 0;

        switch (i) {
            case 1:
                System.out.println("a");
        }
    }

    public void tryStatement() {
        try {
            System.out.println("test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void annotation() {
        System.out.println("test annotation");
    }

    public @interface test {

        String topic() default "";
    }

    public void constructor() {
        Structures s = new Structures("john", 21, 9.5);
    }

    public void doStatement() {
        String nullable = null;
        
        do {
            System.out.println("test");
        } while (true);
    }
}
