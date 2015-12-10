/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

/**
 *
 * @author gleiph
 */
public class Fork {

    /**
     * id of the fork created from project with id projectId
     */
    private Long forkId;
    private String forkURL;

    private Long projectId;

    /**
     * @return the projectId
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the forkId
     */
    public Long getForkId() {
        return forkId;
    }

    /**
     * @param forkId the forkId to set
     */
    public void setForkId(Long forkId) {
        this.forkId = forkId;
    }

    /**
     * @return the forkURL
     */
    public String getForkURL() {
        return forkURL;
    }

    /**
     * @param forkURL the forkURL to set
     */
    public void setForkURL(String forkURL) {
        this.forkURL = forkURL;
    }

}
