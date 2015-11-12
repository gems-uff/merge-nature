/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.languageConstructs;

import org.eclipse.jdt.core.dom.SimpleName;

/**
 *
 * @author gleiph
 */
public class MySimpleName {
    
    private SimpleName simpleName;
    private Location location;

    public MySimpleName(SimpleName simpleName, Location location) {
        this.simpleName = simpleName;
        this.location = location;
    }

    /**
     * @return the simpleName
     */
    public SimpleName getSimpleName() {
        return simpleName;
    }

    /**
     * @param simpleName the simpleName to set
     */
    public void setSimpleName(SimpleName simpleName) {
        this.simpleName = simpleName;
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

}
