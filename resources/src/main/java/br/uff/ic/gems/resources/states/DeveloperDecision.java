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

    VERSION1, VERSION2, CONCATENATION, COMBINATION, MANUAL, NONE;

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
            case NONE:
                return "None";
        }

        return null;

    }

    public static DeveloperDecision toDeveloperDecision(String developerDecision){

        if (VERSION1.toString().equals(developerDecision)) {
            return VERSION1;
        } else if (VERSION2.toString().equals(developerDecision)) {
            return VERSION2;
        } else if (CONCATENATION.toString().equals(developerDecision)) {
            return CONCATENATION;
        } else if (COMBINATION.toString().equals(developerDecision)) {
            return COMBINATION;
        } else if (MANUAL.toString().equals(developerDecision)) {
            return MANUAL;
        } else if (NONE.toString().equals(developerDecision)) {
            return NONE;
        } else {
            System.out.println("Problem during conversion to Developer decision: " 
                    + developerDecision + "is not a developerDecision.");
            return null;
        }
    }

}
