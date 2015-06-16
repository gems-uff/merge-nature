/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author gleiph
 */
@Entity
@SequenceGenerator(name = "LC_SEQ", sequenceName = "LC_SEQ",
        initialValue = 1, allocationSize = 1)
public class LanguageConstruct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LC_SEQ")
    private Long id;
    private String name;
    private int beginLine;
    private int endLine;

    public LanguageConstruct() {
    }

    public LanguageConstruct(String name, int beginLine, int endLine) {
        this.name = name;
        this.beginLine = beginLine;
        this.endLine = endLine;
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
            if(!((languageConstruct.getBeginLine() < begin && languageConstruct.getEndLine() < begin) ||
                    (languageConstruct.getBeginLine() > end && languageConstruct.getEndLine() > end))){
                result.add(languageConstruct);
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
}
