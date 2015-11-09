/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.datastructure;

import br.uff.ic.gems.resources.analises.merge.ConflictingFileAnalyzer;
import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class ConflictingChunkInformation {

    //Information base of any conflicting chunk
    private String filePath;
    private int begin;
    private int separator;
    private int end;

    //Information related to the left repository
    private int leftBegin;
    private int leftEnd;

    //Information related to the right repository
    private int rightBegin;
    private int rightEnd;

    public ConflictingChunkInformation(String filePath, int begin, int separator, int end) {
        this.filePath = filePath;
        this.begin = begin;
        this.separator = separator;
        this.end = end;
    }

    public static List<ConflictingChunkInformation> extractConflictingChunksInformation(String filePath) throws IOException {
        List<ConflictingChunkInformation> result = new ArrayList<>();

        ConflictingChunkInformation conflictingChunkInformation;

        List<String> conflictedFileContent = FileUtils.readLines(new File(filePath));

        List<ConflictingChunk> conflictingChunks = ConflictingFileAnalyzer.getConflictingChunks(conflictedFileContent);

        for (ConflictingChunk conflictingChunk : conflictingChunks) {

            int beginLine = conflictingChunk.getBeginLine();
            int separatorLine = conflictingChunk.getSeparatorLine();
            int endLine = conflictingChunk.getEndLine();

            conflictingChunkInformation = new ConflictingChunkInformation(filePath, beginLine, separatorLine, endLine);

            result.add(conflictingChunkInformation);
        }

        return result;
    }

    public static List<ConflictingChunkInformation> extractConflictingChunksInformation(List<String> filePaths) throws IOException {
        List<ConflictingChunkInformation> result = new ArrayList<>();

        for (String filePath : filePaths) {
            List<ConflictingChunkInformation> cci = extractConflictingChunksInformation(filePath);
            result.addAll(cci);
        }

        return result;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public boolean isLeftEmpty() {
        return begin + 1 == separator;
    }

    public boolean isRightEmpty() {
        return separator + 1 == end;
    }

    @Override
    public String toString() {

        String v1, v2;

        if (this.isLeftEmpty()) {
            v1 = "Left empty";
        } else {
            v1 = "Left not empty";

        }

        if (this.isRightEmpty()) {
            v2 = "Right empty";
        } else {
            v2 = "Right not empty";

        }

        return filePath + "(" + begin + ", " + separator + ", " + end + ") \n\t->" + v1 + "\n\t->" + v2 + 
                "\n\t\trepositioning left: ("+ leftBegin + ", " + leftEnd + ") " +
                "\n\t\trepositioning right: ("+ rightBegin + ", " + rightEnd + ") ";
    }

    public void reposition(String baseFilePath, String leftFilePath, String rightFilePath) {
        Repositioning repositioning = new Repositioning(null);

        if (!isLeftEmpty()) {

            //Adding two 1 to change the index of array to file and another to take the following line
            //of begin mark
            setLeftBegin(repositioning.repositioning(baseFilePath, leftFilePath, begin + 1 + 1));
            //No displacement, it is necessary to add 1 to change the index of array to file 
            //and remove one to take the following line of separator mark
            setLeftEnd(repositioning.repositioning(baseFilePath, leftFilePath, separator));
        }

        if (!isRightEmpty()) {

            //Adding two 1 to change the index of array to file and another to take the following line
            //of begin mark
            setRightBegin(repositioning.repositioning(baseFilePath, rightFilePath, separator + 1 + 1));
            //No displacement, it is necessary to add 1 to change the index of array to file 
            //and remove one to take the following line of separator mark
            setRightEnd(repositioning.repositioning(baseFilePath, rightFilePath, end));
        }
    }

    /**
     * @return the leftBegin
     */
    public int getLeftBegin() {
        return leftBegin;
    }

    /**
     * @param leftBegin the leftBegin to set
     */
    public void setLeftBegin(int leftBegin) {
        this.leftBegin = leftBegin;
    }

    /**
     * @return the leftEnd
     */
    public int getLeftEnd() {
        return leftEnd;
    }

    /**
     * @param leftEnd the leftEnd to set
     */
    public void setLeftEnd(int leftEnd) {
        this.leftEnd = leftEnd;
    }

    /**
     * @return the rightBegin
     */
    public int getRightBegin() {
        return rightBegin;
    }

    /**
     * @param rightBegin the rightBegin to set
     */
    public void setRightBegin(int rightBegin) {
        this.rightBegin = rightBegin;
    }

    /**
     * @return the rightEnd
     */
    public int getRightEnd() {
        return rightEnd;
    }

    /**
     * @param rightEnd the rightEnd to set
     */
    public void setRightEnd(int rightEnd) {
        this.rightEnd = rightEnd;
    }
}
