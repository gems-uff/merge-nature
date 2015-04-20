/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.kinds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ConflictingFile {

    private String name;
    private String kind;
    private String path;
    private List<ConflictingChunk> conflictingChunks;

    public ConflictingFile(String path) {
        this.path = path;
        
        String[] split = path.split(File.separator);
        this.name = split[split.length - 1];
        
        split = this.name.split("\\.");
        this.kind = split[split.length - 1];
        
        this.conflictingChunks = new ArrayList<>();
        
    }

    public boolean isJava(){
        return this.getKind().toUpperCase().equals("JAVA");
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
     * @return the kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(String kind) {
        this.kind = kind;
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
