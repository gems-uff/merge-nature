/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.states;

/**
 *
 * @author gleiph
 */
public enum DeveloperDecision {

    VERSION1, VERSION2, CONCATENATION, COMBINATION, MANUAL;

    @Override
    public String toString() {
        switch (this) {
            case VERSION1:
                return "Version 1";
            case VERSION2:
                return "Version 2";
            case CONCATENATION:
                return "Concatenation";
            case COMBINATION:
                return "Combination";
            case MANUAL:
                return "Manual";
        }

        return null;
        
    }

}
