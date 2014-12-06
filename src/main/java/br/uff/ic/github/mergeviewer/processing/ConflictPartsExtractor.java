/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ConflictPartsExtractor {

    //Input
    private List<String> conflictRegion;
    
    
    //Output
    private List<String> beginContext;
    private List<String> endContext;
    private List<String> leftConflict;
    private List<String> rightConflict;

    public ConflictPartsExtractor(List<String> conflictRegion) {
        this.conflictRegion = conflictRegion;
        this.beginContext = new ArrayList<>();
        this.endContext = new ArrayList<>();
        this.leftConflict = new ArrayList<>();
        this.rightConflict = new ArrayList<>();
    }

    public void extract(){
        int begin = 0, separator = 0, end = 0;
        
        
        
        for (int i = 0; i < getConflictRegion().size(); i++) {
            String line = getConflictRegion().get(i);
            
            if(line.contains("<<<<<<<"))
                begin = i;
            else if(line.contains("======"))
                separator = i;
            else if(line.contains(">>>>>>>"))
                end = i;
        }
        
        setBeginContext(getConflictRegion().subList(0, begin));
        setLeftConflict(getConflictRegion().subList(begin + 1, separator));
        setRightConflict(getConflictRegion().subList(separator + 1, end));
        setEndContext(getConflictRegion().subList(end + 1, getConflictRegion().size()));
    }

    /**
     * @return the conflictRegion
     */
    public List<String> getConflictRegion() {
        return conflictRegion;
    }

    /**
     * @param conflictRegion the conflictRegion to set
     */
    public void setConflictRegion(List<String> conflictRegion) {
        this.conflictRegion = conflictRegion;
    }

    /**
     * @return the beginContext
     */
    public List<String> getBeginContext() {
        return beginContext;
    }

    /**
     * @param beginContext the beginContext to set
     */
    private void setBeginContext(List<String> beginContext) {
        this.beginContext = beginContext;
    }

    /**
     * @return the endContext
     */
    public List<String> getEndContext() {
        return endContext;
    }

    /**
     * @param endContext the endContext to set
     */
    private void setEndContext(List<String> endContext) {
        this.endContext = endContext;
    }

    /**
     * @return the leftConflict
     */
    public List<String> getLeftConflict() {
        return leftConflict;
    }

    /**
     * @param leftConflict the leftConflict to set
     */
    private void setLeftConflict(List<String> leftConflict) {
        this.leftConflict = leftConflict;
    }

    /**
     * @return the rightConflict
     */
    public List<String> getRightConflict() {
        return rightConflict;
    }

    /**
     * @param rightConflict the rightConflict to set
     */
    private void setRightConflict(List<String> rightConflict) {
        this.rightConflict = rightConflict;
    }
    
    
    
}
