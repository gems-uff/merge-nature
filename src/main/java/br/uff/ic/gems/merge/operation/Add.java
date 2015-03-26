/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.merge.operation;

/**
 *
 * @author gleiph
 */
public class Add extends Operation implements OperationInterface{

    public Add() {
        super();
        super.setType(OperationType.ADD);
    }

}
