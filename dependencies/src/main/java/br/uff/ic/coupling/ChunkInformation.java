/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

//import br.uff.ic.gems.resources.analises.merge.ConflictingFileAnalyzer;
//import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Cristiane
 */
public class ChunkInformation {
    

    //Information base of any conflicting chunk
    private String filePath;
    private int begin;
    private int separator;
    private int end;
    
    private List <Integer> added;
    private List <Integer> removed;

    //Information related to the left repository
    private int leftBegin;
    private int leftEnd;

    //Information related to the right repository
    private int rightBegin;
    private int rightEnd;

    //Verify whether files were renamed
    private boolean renamed;
    private String relativePathLeft;
    private String relativePathRight;

    
    public ChunkInformation(String filePath, int added, int removed) {
        this.filePath = filePath;
        this.begin = begin;
        this.separator = separator;

        //Treating rename
        renamed = false;
        relativePathLeft = null;
        relativePathRight = null;

    }
       public ChunkInformation(List <Integer> added, List <Integer> removed) {
        this.added = added;
        this.removed = removed;

        //Treating rename
        renamed = false;
        relativePathLeft = null;
        relativePathRight = null;

    }
    

    public ChunkInformation(ChunkInformation ChunkInformation) {
        this.filePath = ChunkInformation.getFilePath();
        this.begin = ChunkInformation.getBegin();
        this.separator = ChunkInformation.getSeparator();
      
        this.leftBegin = ChunkInformation.getLeftBegin();
        this.leftEnd = ChunkInformation.getLeftEnd();
        this.rightBegin = ChunkInformation.getRightBegin();
        this.rightEnd = ChunkInformation.getRightEnd();
        this.renamed = ChunkInformation.isRenamed();
        this.relativePathLeft = ChunkInformation.getRelativePathLeft();
        this.relativePathRight = ChunkInformation.getRelativePathRight();
    }
 
    
    public static List<ChunkInformation> extractChunksInformation(List <String> filePath) throws IOException {
        List<ChunkInformation> result = new ArrayList<>();

        ChunkInformation ChunkInformation;

        //List<String> FileContent = FileUtils.readLines(new File(filePath));

        //ver
        List<Chunk> Chunks = FileAnalyzer.getChunks(filePath); //get modified lines

        if(Chunks == null)
            return result;
        for (Chunk Chunk : Chunks) {

            List <Integer> addedLine = Chunk.getAddedLine();
            List <Integer> removedLine = Chunk.getRemovedLine();
            

            //ChunkInformation = new ChunkInformation(filePath, addedLine, removedLine);
            ChunkInformation = new ChunkInformation(addedLine, removedLine); //acho q tenho que colocar algum identificador, tipo o sharight ou left

            //Treating rename
           /* String beginLineContent = FileContent.get(addedLine);
            String endLineContent = FileContent.get(removedLine);

            if (beginLineContent.contains(":") || endLineContent.contains(":")) {
                ChunkInformation.setRenamed(true);

                String[] beginLineSplit = beginLineContent.split(":");
                if(beginLineSplit.length > 1){
                    ChunkInformation.setRelativePathLeft(beginLineSplit[beginLineSplit.length - 1]);
                }
                
                String[] endLineSplit = endLineContent.split(":");
                if(endLineSplit.length > 1){
                    ChunkInformation.setRelativePathRight(endLineSplit[endLineSplit.length - 1]);
                }
            }

           */ result.add(ChunkInformation);
        }

        return result;
    }

    /*public static List<ChunkInformation> extractChunksInformation(List<String> filePaths) throws IOException {
        List<ChunkInformation> result = new ArrayList<>();

        for (String filePath : filePaths) {
            List<ChunkInformation> ci = extractChunksInformation(filePath);
            result.addAll(ci);
        }

        return result;
    }*/

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
        return separator + 1 == begin; //estava end
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

        return filePath + "(" + begin + ", " + separator + ", " + end + ") \n\t->" + v1 + "\n\t->" + v2
                + "\n\t\trepositioning left: (" + leftBegin + ", " + leftEnd + ") "
                + "\n\t\trepositioning right: (" + rightBegin + ", " + rightEnd + ") ";
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

    /**
     * @return the renamed
     */
    public boolean isRenamed() {
        return renamed;
    }

    /**
     * @param renamed the renamed to set
     */
    public void setRenamed(boolean renamed) {
        this.renamed = renamed;
    }

    /**
     * @return the relativePathLeft
     */
    public String getRelativePathLeft() {
        return relativePathLeft;
    }

    /**
     * @param relativePathLeft the relativePathLeft to set
     */
    public void setRelativePathLeft(String relativePathLeft) {
        this.relativePathLeft = relativePathLeft;
    }

    /**
     * @return the relativePathRight
     */
    public String getRelativePathRight() {
        return relativePathRight;
    }

    /**
     * @param relativePathRight the relativePathRight to set
     */
    public void setRelativePathRight(String relativePathRight) {
        this.relativePathRight = relativePathRight;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChunkInformation other = (ChunkInformation) obj;
        if (!Objects.equals(this.filePath, other.filePath)) {
            return false;
        }
        if (this.begin != other.begin) {
            return false;
        }
        if (this.separator != other.separator) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (this.leftBegin != other.leftBegin) {
            return false;
        }
        if (this.leftEnd != other.leftEnd) {
            return false;
        }
        if (this.rightBegin != other.rightBegin) {
            return false;
        }
        if (this.rightEnd != other.rightEnd) {
            return false;
        }
        if (this.renamed != other.renamed) {
            return false;
        }
        if (!Objects.equals(this.relativePathLeft, other.relativePathLeft)) {
            return false;
        }
        if (!Objects.equals(this.relativePathRight, other.relativePathRight)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.filePath);
        hash = 47 * hash + this.begin;
        hash = 47 * hash + this.separator;
        hash = 47 * hash + this.end;
        hash = 47 * hash + this.leftBegin;
        hash = 47 * hash + this.leftEnd;
        hash = 47 * hash + this.rightBegin;
        hash = 47 * hash + this.rightEnd;
        hash = 47 * hash + (this.renamed ? 1 : 0);
        hash = 47 * hash + Objects.hashCode(this.relativePathLeft);
        hash = 47 * hash + Objects.hashCode(this.relativePathRight);
        return hash;
    }
}
    
