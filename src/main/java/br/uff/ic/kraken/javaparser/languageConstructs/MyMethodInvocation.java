/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.javaparser.languageConstructs;

import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 *
 * @author gleiph
 */
public class MyMethodInvocation {

    private MethodInvocation methodInvocation;
    private Location location;

    public MyMethodInvocation(MethodInvocation methodInvocation, Location location) {
        this.methodInvocation = methodInvocation;
        this.location = location;
    }

    /**
     * @return the methodInvocation
     */
    public MethodInvocation getMethodInvocation() {
        return methodInvocation;
    }

    /**
     * @param methodInvocation the methodInvocation to set
     */
    public void setMethodInvocation(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return methodInvocation.getName().getIdentifier() + " " + location.toString();
    }

    
    
}
