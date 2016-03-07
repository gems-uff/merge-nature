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
    private String developerDecision;
    private Long conflictingChunkId;
    private String generalKindConflictOutmost;
    private int locLeft;
    private int locRight;
    private int amountLanguageConstructs;

    public ProjectOverview(String sha1, String fileName, String conflictingChunkIdentifier,
            String developerDecision, Long conflictingChunkId, int locLeft, int locRight,
            String generalKindConflictOutmost, int amountLanguageConstructs) {
        this.sha1 = sha1;
        this.fileName = fileName;
        this.conflictingChunkIdentifier = conflictingChunkIdentifier;
        this.developerDecision = developerDecision;
        this.conflictingChunkId = conflictingChunkId;
        this.locLeft = locLeft;
        this.locRight = locRight;
        this.generalKindConflictOutmost = generalKindConflictOutmost;
        this.amountLanguageConstructs = amountLanguageConstructs;
    }

    public ProjectOverview() {
        this.sha1 = "";
        this.fileName = "";
        this.conflictingChunkIdentifier = "";
        this.developerDecision = "";
        this.generalKindConflictOutmost = "";
        this.conflictingChunkId = null;
        this.amountLanguageConstructs = -1;
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
     * @return the amountLanguageConstructs
     */
    public int getAmountLanguageConstructs() {
        return amountLanguageConstructs;
    }

    /**
     * @param amountLanguageConstructs the amountLanguageConstructs to set
     */
    public void setAmountLanguageConstructs(int amountLanguageConstructs) {
        this.amountLanguageConstructs = amountLanguageConstructs;
    }
}
