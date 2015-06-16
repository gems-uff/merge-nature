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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author gleiph
 */
@Entity
@SequenceGenerator(name = "CF_SEQ", sequenceName = "CF_SEQ",
        initialValue = 1, allocationSize = 1)

public class ConflictingFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CF_SEQ")
    private Long id;

    private String name;
    private String fileType;
    private String path;

    @OneToMany
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

}
