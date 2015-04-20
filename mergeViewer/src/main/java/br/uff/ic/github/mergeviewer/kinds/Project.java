/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.kinds;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class Project {
    private String name;
    private String repositoryPath;
    private int numberRevisions;
    private int numberConflictingMerges;
    private List<Revision> revisions;

    public Project() {
        this.revisions = new ArrayList<>();
    }
    
    

    
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the repositoryPath
     */
    public String getRepositoryPath() {
        return repositoryPath;
    }

    /**
     * @param repositoryPath the repositoryPath to set
     */
    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    /**
     * @return the numberRevisions
     */
    public int getNumberRevisions() {
        return numberRevisions;
    }

    /**
     * @param numberRevisions the numberRevisions to set
     */
    public void setNumberRevisions(int numberRevisions) {
        this.numberRevisions = numberRevisions;
    }

    /**
     * @return the numberConflictingMerges
     */
    public int getNumberConflictingMerges() {
        return numberConflictingMerges;
    }

    /**
     * @param numberConflictingMerges the numberConflictingMerges to set
     */
    public void setNumberConflictingMerges(int numberConflictingMerges) {
        this.numberConflictingMerges = numberConflictingMerges;
    }

    /**
     * @return the revisions
     */
    public List<Revision> getRevisions() {
        return revisions;
    }

    /**
     * @param revisions the revisions to set
     */
    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
    }
    
    
}
