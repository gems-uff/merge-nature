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
public class ProjectsOverview {

    private String projectName;
    private String sha1;
    private String fileName;
    private String conflictingChunkIdentifier;
    private String developerDecision;
    private Long conflictingChunkId;
    private String generalKindConflictOutmost;
    private int locLeft;
    private int locRight;

    public ProjectsOverview(String projectName, String sha1, String fileName, String conflictingChunkIdentifier, String developerDecision, Long conflictingChunkId, String generalKindConflictOutmost, int locLeft, int locRight) {
        this.projectName = projectName;
        this.sha1 = sha1;
        this.fileName = fileName;
        this.conflictingChunkIdentifier = conflictingChunkIdentifier;
        this.developerDecision = developerDecision;
        this.conflictingChunkId = conflictingChunkId;
        this.generalKindConflictOutmost = generalKindConflictOutmost;
        this.locLeft = locLeft;
        this.locRight = locRight;
    }

    public ProjectsOverview() {
        this.projectName = "";
        this.sha1 = "";
        this.fileName = "";
        this.conflictingChunkIdentifier = "";
        this.developerDecision = "";
        this.conflictingChunkId = null;
        this.generalKindConflictOutmost = "";
        this.locLeft = -1;
        this.locRight = -1;
    }

    /**
     * @return the sha1
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * @param sha1 the sha1 to set
     */
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the conflictingChunkIdentifier
     */
    public String getConflictingChunkIdentifier() {
        return conflictingChunkIdentifier;
    }

    /**
     * @param conflictingChunkIdentifier the conflictingChunkIdentifier to set
     */
    public void setConflictingChunkIdentifier(String conflictingChunkIdentifier) {
        this.conflictingChunkIdentifier = conflictingChunkIdentifier;
    }

    /**
     * @return the developerDecision
     */
    public String getDeveloperDecision() {
        return developerDecision;
    }

    /**
     * @param developerDecision the developerDecision to set
     */
    public void setDeveloperDecision(String developerDecision) {
        this.developerDecision = developerDecision;
    }

    /**
     * @return the conflictingChunkId
     */
    public Long getConflictingChunkId() {
        return conflictingChunkId;
    }

    /**
     * @param conflictingChunkId the conflictingChunkId to set
     */
    public void setConflictingChunkId(Long conflictingChunkId) {
        this.conflictingChunkId = conflictingChunkId;
    }

    /**
     * @return the locLeft
     */
    public int getLocLeft() {
        return locLeft;
    }

    /**
     * @param locLeft the locLeft to set
     */
    public void setLocLeft(int locLeft) {
        this.locLeft = locLeft;
    }

    /**
     * @return the locRight
     */
    public int getLocRight() {
        return locRight;
    }

    /**
     * @param locRight the locRight to set
     */
    public void setLocRight(int locRight) {
        this.locRight = locRight;
    }

    /**
     * @return the generalKindConflictOutmost
     */
    public String getGeneralKindConflictOutmost() {
        return generalKindConflictOutmost;
    }

    /**
     * @param generalKindConflictOutmost the generalKindConflictOutmost to set
     */
    public void setGeneralKindConflictOutmost(String generalKindConflictOutmost) {
        this.generalKindConflictOutmost = generalKindConflictOutmost;
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
