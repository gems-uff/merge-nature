/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.dependency;

/**
 *
 * @author gleiph
 */
public enum DependencyType {
    METHOD_DECLARATION_CALL, 
    ATTRIBUTE_DECLARATION_USAGE, 
    VARIABLE_DECLARATION_USAGE, 
    TYPE_DECLARATION_USAGE, 
    TYPE_DELCARATION_INTERFACE
}
