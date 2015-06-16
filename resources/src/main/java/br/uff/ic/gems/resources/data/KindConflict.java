/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data;

import br.uff.ic.gems.resources.ast.ASTTypes;
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
@SequenceGenerator(name="KC_SEQ", sequenceName = "KC_SEQ", 
        initialValue = 1, allocationSize = 1)
    
public class KindConflict implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KC_SEQ")
    private Long id;
    private int beginLine;
    private int endLine;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LanguageConstruct> languageConstructs;

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
     * @return the languageConstructs
     */
    public List<LanguageConstruct> getLanguageConstructs() {
        return languageConstructs;
    }

    /**
     * @param languageConstructs the languageConstructs to set
     */
    public void setLanguageConstructs(List<LanguageConstruct> languageConstructs) {
        this.languageConstructs = languageConstructs;
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

    public List<LanguageConstruct> getFilteredLanguageConstructs() {
        List<LanguageConstruct> result = new ArrayList<>();
        int currentIndex = this.beginLine;

        List<LanguageConstruct> copyLanguageConstructs = new ArrayList<>();
        
        for (LanguageConstruct languageConstruct : this.getLanguageConstructs()) {
            copyLanguageConstructs.add(languageConstruct);
        }
        
        while (currentIndex <= this.endLine) {
            int size = 0;
            LanguageConstruct currentLanguageConstruct = new LanguageConstruct();
            for (LanguageConstruct languageConstruct : copyLanguageConstructs) {
                if (languageConstruct.getBeginLine() == currentIndex
                        && size < languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1) {
                    currentLanguageConstruct = languageConstruct;
                    size = languageConstruct.getEndLine() - languageConstruct.getBeginLine() + 1;
                }
            }

            if (currentLanguageConstruct.getId() == null) {
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.METHOD_DECLARATION)
                    && currentLanguageConstruct.getEndLine() > this.endLine) {
                currentLanguageConstruct = new LanguageConstruct(ASTTypes.METHOD_SIGNATURE, currentIndex, currentIndex);
                result.add(currentLanguageConstruct);
                currentIndex++;
            } else if (currentLanguageConstruct.getName().equals(ASTTypes.RETURN_STATEMENT)) {
                result.add(currentLanguageConstruct);
                copyLanguageConstructs.remove(currentLanguageConstruct);
            } else {
                result.add(currentLanguageConstruct);
                currentIndex += currentLanguageConstruct.getEndLine() - currentLanguageConstruct.getBeginLine() + 1;
            }
        }

        return result;
    }
}
