/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.strategy;

import java.io.File;

/**
 *
 * @author gleiph
 */
public class NodeDependency {
    
    private File file;
    private int begin;
    private int end;
    private int separator;
    private int dependencies;
    private int dependent;
    private boolean visited;
    private int contextAware;
    
    public NodeDependency() {
        this.contextAware = 0;
    }

    public NodeDependency(NodeDependency node) {
        this.file = node.getFile();
        this.begin = node.getBegin();
        this.end = node.getEnd();
        this.separator = node.getSeparator();
        this.dependencies = node.getDependencies();
        this.dependent = node.getDependent();
        this.visited = node.isVisited();
        this.contextAware = node.getContextAware();
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the begin
     */
    public int getBegin() {
        return begin;
    }

    /**
     * @param begin the begin to set
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * @return the separator
     */
    public int getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public void setSeparator(int separator) {
        this.separator = separator;
    }

    /**
     * @return the dependencies
     */
    public int getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies the dependencies to set
     */
    public void setDependencies(int dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "(" + file + "\n(" + begin + "," + separator + ", " + end + ")\n"
                + "dependencies=" + dependencies 
                + "\ndependent=" + dependent;
    }

    /**
     * @return the dependent
     */
    public int getDependent() {
        return dependent;
    }

    /**
     * @param dependent the dependent to set
     */
    public void setDependent(int dependent) {
        this.dependent = dependent;
    }

    /**
     * @return the visited
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * @param visited the visited to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * @return the contextAware
     */
    public int getContextAware() {
        return contextAware;
    }

    /**
     * @param contextAware the contextAware to set
     */
    public void setContextAware(int contextAware) {
        this.contextAware = contextAware;
    }
    
}
