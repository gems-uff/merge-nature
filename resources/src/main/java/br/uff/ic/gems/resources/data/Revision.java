/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import br.uff.ic.gems.resources.states.MergeStatus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author gleiph
 */
@Entity
public class Revision implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String sha;
    private MergeStatus status;
    private String leftSha;
    private String rightSha;
    private String baseSha;
    private int numberConflictingFiles;
    private int numberJavaConflictingFiles;

    @Transient
    private int numberChunks;
    @Transient
    private int numberJavaChunks;
    
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "Revision_ConflictingFile", 
            joinColumns = @JoinColumn(name = "Revision_ID"),
            inverseJoinColumns = @JoinColumn(name = "ConflictingFile_ID"))
    private List<ConflictingFile> conflictingFiles;

    public Revision() {
        this.conflictingFiles = new ArrayList<>();
    }

    public boolean isConflict() {
        return this.status == MergeStatus.CONFLICTING;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
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

    /**
     * @return the numberChunks
     */
    public int getNumberChunks() {
        return numberChunks;
    }

    /**
     * @param numberChunks the numberChunks to set
     */
    public void setNumberChunks(int numberChunks) {
        this.numberChunks = numberChunks;
    }

    /**
     * @return the numberJavaChunks
     */
    public int getNumberJavaChunks() {
        return numberJavaChunks;
    }

    /**
     * @param numberJavaChunks the numberJavaChunks to set
     */
    public void setNumberJavaChunks(int numberJavaChunks) {
        this.numberJavaChunks = numberJavaChunks;
    }

}
