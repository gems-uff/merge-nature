/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author gleiph
 */
@Entity
public class LanguageConstruct implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int beginLine;
    private int endLine;
    @Transient
    private String identifier;

    public LanguageConstruct() {
    }

    public LanguageConstruct(String name, int beginLine, int endLine) {
        this.name = name;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.identifier = null;
    }

    public LanguageConstruct(String name, int beginLine, int endLine, String identifier) {
        this.name = name;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.identifier = identifier;
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
     * @return the beginLine
     */
    public int getBeginLine() {
        return beginLine;
    }

    /**
     * @param beginLine the beginLine to set
     */
    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    /**
     * @return the endLine
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * @param endLine the endLine to set
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get kind of conflict from the language constructs
     *
     * @param begin
     * @param end
     * @param languageConstructs
     * @return
     */
    public static List<LanguageConstruct> getLanguageConstructs(int begin, int end, List<LanguageConstruct> languageConstructs) {
        List<LanguageConstruct> result = new ArrayList<>();

        for (LanguageConstruct languageConstruct : languageConstructs) {
            if (!((languageConstruct.getBeginLine() < begin && languageConstruct.getEndLine() < begin)
                    || (languageConstruct.getBeginLine() > end && languageConstruct.getEndLine() > end))) {
                if (!result.contains(languageConstruct)) {
                    result.add(languageConstruct);
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.beginLine + ", " + this.endLine + ")";
    }

    public static String toString(List<LanguageConstruct> languageConstructs) {
        List<String> result = new ArrayList<>();

        for (LanguageConstruct languageConstruct : languageConstructs) {
            if (!result.contains(languageConstruct.getName())) {
                result.add(languageConstruct.getName());
            }
        }

        return result.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + this.beginLine;
        hash = 29 * hash + this.endLine;
        hash = 29 * hash + Objects.hashCode(this.identifier);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LanguageConstruct other = (LanguageConstruct) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.beginLine != other.beginLine) {
            return false;
        }
        if (this.endLine != other.endLine) {
            return false;
        }
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        return true;
    }

    @Override
    public LanguageConstruct clone(){

        if(this == null)
            return null;
        
        LanguageConstruct result = new LanguageConstruct();
        
        result.setBeginLine(this.getBeginLine());
        result.setEndLine(this.getEndLine());
        result.setId(this.getId());
        result.setIdentifier(this.getIdentifier());
        result.setName(this.getName());
        
        return result;
    }
}
