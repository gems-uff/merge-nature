/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import java.io.File;
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

/**
 *
 * @author gleiph
 */
@Entity
public class ConflictingFile implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String fileType;
    private String path;
    private boolean removed;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "ConflictingFile_ConflictingChunk", 
            joinColumns = @JoinColumn(name = "ConflictingFile_ID"),
            inverseJoinColumns = @JoinColumn(name = "ConflictingChunk_ID"))
    private List<ConflictingChunk> conflictingChunks;

    public ConflictingFile() {
        conflictingChunks = new ArrayList<>();
    }

    public ConflictingFile(String path) {
        this.path = path;

        String[] split = path.split(File.separator);
        this.name = split[split.length - 1];

        split = this.name.split("\\.");
        this.fileType = split[split.length - 1];

        this.conflictingChunks = new ArrayList<>();

    }

    public boolean isJava() {
        return this.getFileType().toUpperCase().equals("JAVA");
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
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType the fileType to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the conflictingChunks
     */
    public List<ConflictingChunk> getConflictingChunks() {
        return conflictingChunks;
    }

    /**
     * @param conflictingChunks the conflictingChunks to set
     */
    public void setConflictingChunks(List<ConflictingChunk> conflictingChunks) {
        this.conflictingChunks = conflictingChunks;
    }

    /**
     * @return the removed
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * @param removed the removed to set
     */
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

}
