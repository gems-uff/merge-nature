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
public class ProjectOverview {

    private String sha1;
    private String fileName;
    private String conflictingChunkIdentifier;
    private String kindConflict;
    private String developerDecision;
    private Long conflictingChunkId;

    public ProjectOverview(String sha1, String fileName, String conflictingChunkIdentifier, String kindConflict, String developerDecision, Long conflictingChunkId) {
        this.sha1 = sha1;
        this.fileName = fileName;
        this.conflictingChunkIdentifier = conflictingChunkIdentifier;
        this.kindConflict = kindConflict;
        this.developerDecision = developerDecision;
        this.conflictingChunkId = conflictingChunkId;
    }

        public ProjectOverview() {
        this.sha1 = "";
        this.fileName = "";
        this.conflictingChunkIdentifier = "";
        this.kindConflict = "";
        this.developerDecision = "";
        this.conflictingChunkId = null;
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
     * @return the kindConflict
     */
    public String getKindConflict() {
        return kindConflict;
    }

    /**
     * @param kindConflict the kindConflict to set
     */
    public void setKindConflict(String kindConflict) {
        this.kindConflict = kindConflict;
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
}
