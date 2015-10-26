/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.builder;

/**
 *
 * @author gleiph
 */
public interface Builder {

    boolean compile();

    boolean task(String task);

    boolean test();
    
}
