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

    static int global;

    public static void main(String[] args) {

        int b = 0, f, h, ghiotto;

        int[] a = {1, 2, 3, 4, 5};

        int[] c = new int[6];

        global = 30;

        a[0] = 1;
        a[1] = b;
        a[2] = 3;

        b = 5;

        c[0] = 10;

        //Comment
        a[3] = b;

        System.out.println(a[2]);
    }

    @Override
    public String toString() {
        return "ASTTypesTest{" + '}';
    }

    public @interface CommonsLog {

        /**
         * Sets the category of the constructed Logger. By default, it will use
         * the type where the annotation is placed.
         */
        String topic() default "";
    }
}
