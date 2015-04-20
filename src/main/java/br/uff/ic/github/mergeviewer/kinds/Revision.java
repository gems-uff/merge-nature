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
public class Revision {

    private String sha;
    private MergeStatus status;
    private String leftSha;
    private String rightSha;
    private String baseSha;
    private int numberConflictingFiles;
    private int numberJavaConflictingFiles;
    private List<ConflictingFile> conflictingFiles;

    public Revision() {
        this.conflictingFiles = new ArrayList<>();
    }

    public boolean isConflict() {
        return this.status == MergeStatus.CONFLICTING;
    }

    /**
     * @return the sha
     */
    public String getSha() {
        return sha;
    }

    /**
     * @param sha the sha to set
     */
    public void setSha(String sha) {
        this.sha = sha;
    }

    /**
     * @return the status
     */
    public MergeStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(MergeStatus status) {
        this.status = status;
    }

    /**
     * @return the leftSha
     */
    public String getLeftSha() {
        return leftSha;
    }

    /**
     * @param leftSha the leftSha to set
     */
    public void setLeftSha(String leftSha) {
        this.leftSha = leftSha;
    }

    /**
     * @return the rightSha
     */
    public String getRightSha() {
        return rightSha;
    }

    /**
     * @param rightSha the rightSha to set
     */
    public void setRightSha(String rightSha) {
        this.rightSha = rightSha;
    }

    /**
     * @return the baseSha
     */
    public String getBaseSha() {
        return baseSha;
    }

    /**
     * @param baseSha the baseSha to set
     */
    public void setBaseSha(String baseSha) {
        this.baseSha = baseSha;
    }

    /**
     * @return the numberConflictingFiles
     */
    public int getNumberConflictingFiles() {
        return numberConflictingFiles;
    }

    /**
     * @param numberConflictingFiles the numberConflictingFiles to set
     */
    public void setNumberConflictingFiles(int numberConflictingFiles) {
        this.numberConflictingFiles = numberConflictingFiles;
    }

    /**
     * @return the numberJavaConflictingFiles
     */
    public int getNumberJavaConflictingFiles() {
        return numberJavaConflictingFiles;
    }

    /**
     * @param numberJavaConflictingFiles the numberJavaConflictingFiles to set
     */
    public void setNumberJavaConflictingFiles(int numberJavaConflictingFiles) {
        this.numberJavaConflictingFiles = numberJavaConflictingFiles;
    }

    /**
     * @return the conflictingFiles
     */
    public List<ConflictingFile> getConflictingFiles() {
        return conflictingFiles;
    }

    /**
     * @param conflictingFiles the conflictingFiles to set
     */
    public void setConflictingFiles(List<ConflictingFile> conflictingFiles) {
        this.conflictingFiles = conflictingFiles;
    }

}
