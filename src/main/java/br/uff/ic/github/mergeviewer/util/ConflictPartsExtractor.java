/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.util;

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
    private String leftFile;
    private String rightFile;
    private int separator;
    private int begin;
    private int end;

    public ConflictPartsExtractor(List<String> conflictRegion) {
        this.conflictRegion = conflictRegion;
        this.beginContext = new ArrayList<>();
        this.endContext = new ArrayList<>();
        this.leftConflict = new ArrayList<>();
        this.rightConflict = new ArrayList<>();
        this.leftFile = null;
        this.rightFile = null;
    }

    public void extract() {
        boolean takeBegin = false, takeSeparator = false, takeEnd = false;

        List<Integer> remove = new ArrayList<>();
        
        for (int i = 0; i < getConflictRegion().size(); i++) {
            String line = getConflictRegion().get(i);

            if (line.contains("<<<<<<<")) {
                if (!takeBegin) {
                    if (line.contains(":")) {
                        String[] split = line.split(":");
                        setLeftFile(split[split.length - 1]);
                    }
                    begin = i;
                    takeBegin = true;
                    takeSeparator = false;
                    takeEnd = false;
                }

            } else if (line.contains("======")) {
                if (!takeSeparator) {
                    separator = i;
                    takeSeparator = true;
                }
            } else if (line.contains(">>>>>>>")) {
                if (!takeEnd) {
                    end = i;
                    if (line.contains(":")) {
                        String[] split = line.split(":");
                        setRightFile(split[split.length - 1]);
                    }
                    takeEnd = true;
                }
            }
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

    /**
     * @return the leftFile
     */
    public String getLeftFile() {
        return leftFile;
    }

    /**
     * @param leftFile the leftFile to set
     */
    public void setLeftFile(String leftFile) {
        this.leftFile = leftFile;
    }

    /**
     * @return the rightFile
     */
    public String getRightFile() {
        return rightFile;
    }

    /**
     * @param rightFile the rightFile to set
     */
    public void setRightFile(String rightFile) {
        this.rightFile = rightFile;
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

}
