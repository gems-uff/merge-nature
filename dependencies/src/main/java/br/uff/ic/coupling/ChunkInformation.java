/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Gleiph, Cristiane
 */
public class ChunkInformation {

    //Information base of any chunk
    private String filePath;
    private List<Operation> operations;
    private List<Integer> repositionBase;

    //Verify whether files were renamed
    private boolean renamed;
    private String relativePathLeft;
    private String relativePathRight;

    public ChunkInformation(String filePath, List<Operation> operations) {
        this.filePath = filePath;
        this.operations = operations;

        //Treating rename
        renamed = false;
        relativePathLeft = null;
        relativePathRight = null;

    }

    public ChunkInformation(ChunkInformation ChunkInformation) {
        this.filePath = ChunkInformation.getFilePath();
        this.operations = ChunkInformation.getOperations();
        this.renamed = ChunkInformation.isRenamed();
        this.relativePathLeft = ChunkInformation.getRelativePathLeft();
        this.relativePathRight = ChunkInformation.getRelativePathRight();
    }

    public static List<ChunkInformation> extractChunksInformation(String projectPath, String filePath, String SHAmergeBase, String SHAParent, String branch) throws IOException {
        List<ChunkInformation> result = new ArrayList<>();

        ChunkInformation ChunkInformation;

        List<String> fileDiff = Git.fileDiff(projectPath, filePath, SHAmergeBase, SHAParent);

        List<Chunk> Chunks = FileAnalyzer.getChunks(fileDiff, filePath, branch); //get modified lines

        if (Chunks == null) {
            return result;
        }
        for (Chunk Chunk : Chunks) {
            List<Operation> operations = Chunk.getOperations();

            ChunkInformation = new ChunkInformation(filePath, operations);

            //Treating rename Preciso tratar arquivo renomeado?
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

    public void reposition(String filePath, String mergeBaseFilePath) {
        Repositioning repositioning = new Repositioning(null);

        int position;

        List<Integer> repositionBase = new ArrayList<>();
        for (Operation operation : operations) {
            int line = operation.getLine();
            position = repositioning.repositioning(mergeBaseFilePath, filePath, line);
            repositionBase.add(position);
        }
        
        setReposition(repositionBase);
        
       /* Operation operation = GitTranslator.getOperations((operationsCluster.get(i).getType()), (operationsCluster.get(i).getSize()), result);

            repositionOperations.add(operation);
        
         operationsCluster = gitTranslator.cluster(operations);*/

        //List<Operation> Op = repositioning.repositioning(baseFilePath, leftFilePath, operations);
        //setReposition(Op);
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

}
