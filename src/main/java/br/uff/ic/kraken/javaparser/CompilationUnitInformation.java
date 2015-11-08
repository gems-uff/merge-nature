/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.javaparser;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 *
 * @author gleiph
 */
public class CompilationUnitInformation {
    
    private String className;
    private CompilationUnit cu;

    public CompilationUnitInformation(String className, CompilationUnit cu) {
        this.className = className;
        this.cu = cu;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the cu
     */
    public CompilationUnit getCu() {
        return cu;
    }

    /**
     * @param cu the cu to set
     */
    public void setCu(CompilationUnit cu) {
        this.cu = cu;
    }
    
}
