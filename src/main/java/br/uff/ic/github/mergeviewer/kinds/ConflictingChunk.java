/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.kinds;

import br.uff.ic.github.mergeviewer.util.DeveloperChoice;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ConflictingChunk {

    private int begin;
    private int separator;
    private int end;
    private String identifier;
    private List<String> leftKindConflict;
    private List<String> rightKindConflict;
    private List<String> generalKindConflict;
    private DeveloperChoice developerChoice;
    
    
    
    /**
     * @return the begin
     */
    public int getBegin() {
        return begin;
    }

    /**
     * @param begin the begin to set
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
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
     * @return the leftKindConflict
     */
    public List<String> getLeftKindConflict() {
        return leftKindConflict;
    }

    /**
     * @param leftKindConflict the leftKindConflict to set
     */
    public void setLeftKindConflict(List<String> leftKindConflict) {
        this.leftKindConflict = leftKindConflict;
    }

    /**
     * @return the rightKindConflict
     */
    public List<String> getRightKindConflict() {
        return rightKindConflict;
    }

    /**
     * @param rightKindConflict the rightKindConflict to set
     */
    public void setRightKindConflict(List<String> rightKindConflict) {
        this.rightKindConflict = rightKindConflict;
    }

    /**
     * @return the generalKindConflict
     */
    public List<String> getGeneralKindConflict() {
        return generalKindConflict;
    }

    /**
     * @param generalKindConflict the generalKindConflict to set
     */
    public void setGeneralKindConflict(List<String> generalKindConflict) {
        this.generalKindConflict = generalKindConflict;
    }

    /**
     * @return the separator
     */
    public int getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public void setSeparator(int separator) {
        this.separator = separator;
    }

    /**
     * @return the developerChoice
     */
    public DeveloperChoice getDeveloperChoice() {
        return developerChoice;
    }

    /**
     * @param developerChoice the developerChoice to set
     */
    public void setDeveloperChoice(DeveloperChoice developerChoice) {
        this.developerChoice = developerChoice;
    }

}
