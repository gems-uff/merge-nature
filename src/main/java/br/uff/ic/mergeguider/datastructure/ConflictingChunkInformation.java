/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.datastructure;

import br.uff.ic.gems.resources.analises.merge.ConflictingFileAnalyzer;
import br.uff.ic.gems.resources.data.ConflictingChunk;
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

    private String filePath;
    private int begin;
    private int separator;
    private int end;

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

        return filePath + "(" + begin + ", " + separator + ", " + end + ") \n\t->" + v1 + "\n\t->" + v2 ;
    }

}
