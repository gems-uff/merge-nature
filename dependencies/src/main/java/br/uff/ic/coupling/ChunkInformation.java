/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.mergeguider.MergeGuider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Gleiph, Cristiane
 */
public class ChunkInformation {

    //Information base of any chunk
    private String filePath;
    private List<Operation> operations;
    private List<Operation> operationsBase;
    private List<Integer> repositionBase;

    //Verify whether files were renamed
    private boolean renamed;
    private String relativePathLeft;
    private String relativePathRight;
    private String relativePathLeftBase;
    private String relativePathRightBase;
    private int chunks;

    public ChunkInformation(String filePath, List<Operation> operations, List<Operation> operationsBase) {
        this.filePath = filePath;
        this.operations = operations;
        this.operationsBase = operationsBase;

        //Treating rename
        renamed = false;
        relativePathLeft = null;
        relativePathRight = null;
        relativePathLeftBase = null;
        relativePathRightBase = null;

    }

    public ChunkInformation(ChunkInformation ChunkInformation) {
        this.filePath = ChunkInformation.getFilePath();
        this.operations = ChunkInformation.getOperations();
        this.operationsBase = ChunkInformation.getOperationsBase();
        this.renamed = ChunkInformation.isRenamed();
        this.relativePathLeft = ChunkInformation.getRelativePathLeft();
        this.relativePathRight = ChunkInformation.getRelativePathRight();
        this.relativePathLeftBase = ChunkInformation.getRelativePathLeftBase();
        this.relativePathRightBase = ChunkInformation.getRelativePathRightBase();
    }

    public static List<ChunkInformation> extractChunksInformation(String projectPath, String filePath, String SHAmergeBase, String SHAParent, String branch) throws IOException {
        List<ChunkInformation> result = new ArrayList<>();

        GitTranslator gitTranslator = new GitTranslator();

        ChunkInformation ChunkInformation;

        List<String> fileDiff = Git.fileDiff(projectPath, filePath, SHAmergeBase, SHAParent);

        if (fileDiff == null) {
            return result;
        }

        List<Chunk> Chunks = FileAnalyzer.getChunks(fileDiff, filePath, branch); //get modified lines

        if (Chunks == null) {
            return result;
        }
        for (Chunk Chunk : Chunks) {
            List<Operation> operations = Chunk.getOperations();
            List<Operation> operationsBase = Chunk.getOperationsBase();
            ChunkInformation = new ChunkInformation(filePath, operations, operationsBase);

            String fromFile = "";
            String toFile = "";
            //Treating rename
            for (String line : fileDiff) {
                if (line.startsWith("---")) {
                    fromFile = line.substring(line.indexOf("a/") + 1);
                } else if (line.startsWith("+++")) {
                    toFile = line.substring(line.indexOf("b/") + 1);
                }

            }
            if (!fromFile.equals(toFile)) { //renamed file or new file
                List<String> diffList = Git.diff(projectPath, SHAmergeBase, SHAParent);
                String diffDelta = diffList.toString();
                List<String> pathFileMerge = gitTranslator.translateRenamed(diffDelta);

                if (pathFileMerge.isEmpty()) { //new file
                    ChunkInformation.setRelativePathLeftBase(fromFile); 
                    ChunkInformation.setRelativePathRightBase(toFile);

                } else if (!pathFileMerge.isEmpty()) { //renamed file
                    ChunkInformation.setRenamed(true);
                    ChunkInformation.setRelativePathLeftBase(pathFileMerge.get(0)); //renamed from
                    ChunkInformation.setRelativePathRightBase(pathFileMerge.get(1)); //renamed to
                }

            } else if (fromFile.equals(toFile)) {
                ChunkInformation.setRelativePathLeftBase(toFile); //fromFile
                ChunkInformation.setRelativePathRightBase(fromFile); //toFile
            }

            ChunkInformation.setRelativePathLeft(toFile); //fromFile
            ChunkInformation.setRelativePathRight(fromFile); //toFile

            result.add(ChunkInformation);
        }
        return result;
    }

    public static List<ChunkInformation> extractChunksInformation(String projectPath, List<String> filePaths, String SHAmergeBase, String SHALeft, String branch) throws IOException {
        List<ChunkInformation> result = new ArrayList<>();

        for (String filePath : filePaths) {
            List<ChunkInformation> ci = extractChunksInformation(projectPath, filePath, SHAmergeBase, SHALeft, branch);
            result.addAll(ci);
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
     * @return the operations
     */
    public List<Operation> getOperations() {
        return operations;
    }

    /**
     * @param operations the operations to set
     */
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    /**
     * @return the operationsBase
     */
    public List<Operation> getOperationsBase() {
        return operationsBase;
    }

    /**
     * @param operationsBase the operations to set
     */
    public void setOperationsBase(List<Operation> operationsBase) {
        this.operationsBase = operationsBase;
    }

    public void reposition(String filePath, String mergeBaseFilePath) {
        Repositioning repositioning = new Repositioning(null);

        GitTranslator gitTranslator = new GitTranslator();

        int position;

        List<Integer> repositionBase = new ArrayList<>();
        for (Operation operation : operations) {
            int line = operation.getLine();
            position = repositioning.repositioning(mergeBaseFilePath, filePath, line);
            repositionBase.add(position);
        }

        setReposition(repositionBase);
    }

    /**
     * @return the repositionOperations
     */
    public List<Integer> getReposition() {
        return repositionBase;
    }

    /**
     * @param repositionOperations the repositionOperations to set
     */
    public void setReposition(List<Integer> repositionBase) {
        this.repositionBase = repositionBase;
    }

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

    /**
     * @return the relativePathLeftBase
     */
    public String getRelativePathLeftBase() {
        return relativePathLeftBase;
    }

    /**
     * @param relativePathLeftBase the relativePathLeft to set
     */
    public void setRelativePathLeftBase(String relativePathLeftBase) {
        this.relativePathLeftBase = relativePathLeftBase;
    }

    /**
     * @return the relativePathRightBase
     */
    public String getRelativePathRightBase() {
        return relativePathRightBase;
    }

    /**
     * @param relativePathRightBase the relativePathRight to set
     */
    public void setRelativePathRightBase(String relativePathRightBase) {
        this.relativePathRightBase = relativePathRightBase;
    }
    
     /**
     * @return the chunks
     */
    public int getChunks() {
        return chunks;
    }

    /**
     * @param chunks the chunks to set
     */
    public void setchunks(int chunks) {
        this.chunks = chunks;
    }

}
