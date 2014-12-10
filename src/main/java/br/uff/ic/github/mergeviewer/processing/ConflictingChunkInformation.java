/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.merge.utils.MergeUtils;
import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.ShowCase;
import br.uff.ic.github.mergeviewer.ast.ASTAuxiliar;
import br.uff.ic.github.mergeviewer.util.ConflictPartsExtractor;
import br.uff.ic.github.mergeviewer.util.ConflictingChunk;
import br.uff.ic.github.mergeviewer.util.ContextFinder;
import br.uff.ic.github.mergeviewer.util.DeveloperDecision;
import br.uff.ic.github.mergeviewer.util.Information;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class ConflictingChunkInformation implements Runnable {

    private final String pathLeftRepository;
    private final String pathRightRepository;
    private final String pathMergedRepository;
    private final String pathDeveloperMergedRepository;
    private final String pathRelativeFile;
    private ConflictingChunk conflictChunk;
    private final int context;

    public ConflictingChunkInformation(String pathLeftRepository, String pathRightRepository, String pathMergedRepository,
            String pathDeveloperMergedRepository, String pathRelativeFile, ConflictingChunk conflictChunk, int context) {
        this.pathLeftRepository = pathLeftRepository;
        this.pathRightRepository = pathRightRepository;
        this.pathMergedRepository = pathMergedRepository;
        this.pathDeveloperMergedRepository = pathDeveloperMergedRepository;
        this.pathRelativeFile = pathRelativeFile;
        this.conflictChunk = conflictChunk;
        this.context = context;
    }

    @Override
    public void run() {

        ASTAuxiliar.cloneRepositories(pathMergedRepository, pathDeveloperMergedRepository, pathLeftRepository, pathRightRepository);

        //Checking out revisions
        GitCMD.checkout(pathDeveloperMergedRepository, Information.DEVELOPER_MERGE_REVISION);
        GitCMD.checkout(pathLeftRepository, Information.LEFT_REVISION);
        GitCMD.checkout(pathRightRepository, Information.RIGHT_REVISION);

        //Getting conflicting file
        List<String> fileConflict = MergeUtils.fileToLines(
                pathMergedRepository + pathRelativeFile);
        //Getting solution file
        List<String> fileSolution = MergeUtils.fileToLines(
                pathDeveloperMergedRepository + pathRelativeFile);

        //Getting conflict area
        int beginConflict, endConflict;
        beginConflict = ASTAuxiliar.getConflictLowerBound(getConflictChunk(), context);
        endConflict = ASTAuxiliar.getConflictUpperBound(getConflictChunk(), context, fileConflict);
        List<String> conflictingArea = fileConflict.subList(beginConflict, endConflict);

        //Getting parts of conflict area
        ConflictPartsExtractor cpe = new ConflictPartsExtractor(conflictingArea);
        cpe.extract();

        List<String> beginContext = cpe.getBeginContext();
        List<String> endContext = cpe.getEndContext();

        List<String> solutionArea = ContextFinder.getSolution(beginContext, endContext, fileSolution, beginConflict, endConflict);

        String leftSS = null;

        String rightSS = null;

        if (pathRelativeFile.contains(".java")) {
            try {
                leftSS = ASTAuxiliar.getSyntacticStructures(cpe.getLeftConflict(), pathLeftRepository, pathRelativeFile);
                rightSS = ASTAuxiliar.getSyntacticStructures(cpe.getRightConflict(), pathRightRepository, pathRelativeFile);
            } catch (IOException ex) {
                Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String dd = DeveloperDecision.getDeveloperDecision(cpe, solutionArea).toString();

        ShowCase showCase = new ShowCase(conflictingArea, solutionArea, leftSS, rightSS, dd);
        showCase.setVisible(true);
    }

    /**
     * @return the conflictChunk
     */
    public ConflictingChunk getConflictChunk() {
        return conflictChunk;
    }

    /**
     * @param conflictChunk the conflictChunk to set
     */
    public void setConflictChunk(ConflictingChunk conflictChunk) {
        this.conflictChunk = conflictChunk;
    }

}
