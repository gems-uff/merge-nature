/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.utils;

/**
 *
 * @author gleiph
 */
public class ProjectsOverview extends ProjectOverview{

    private String projectName;

    public ProjectsOverview(String projectName, String sha1, String fileName, String conflictingChunkIdentifier, 
            String developerDecision, Long conflictingChunkId, String generalKindConflictOutmost, int locLeft, 
            int locRight, int amountLanguageConstructs) {
        super(sha1, fileName, conflictingChunkIdentifier, developerDecision, conflictingChunkId, locLeft, locRight, generalKindConflictOutmost, amountLanguageConstructs);
        this.projectName = projectName;
        
    }

    public ProjectsOverview() {
        super();
        this.projectName = "";
    }

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
