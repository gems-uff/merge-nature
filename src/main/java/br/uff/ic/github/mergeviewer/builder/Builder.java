/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.builder;

/**
 *
 * @author gleiph
 */
public interface Builder {

    public boolean compile();

    public boolean test();

    public boolean task(String task);

}
