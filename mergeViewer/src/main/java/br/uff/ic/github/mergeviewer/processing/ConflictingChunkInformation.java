/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.resources.analises.merge.ConflictingFileAnalyzer;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.github.mergeviewer.ShowCase;
import br.uff.ic.gems.resources.ast.ASTAuxiliar;
import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.utils.ConflictPartsExtractor;
import br.uff.ic.gems.resources.analises.merge.DeveloperDecisionAnalyzer;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.LanguageConstruct;
import br.uff.ic.gems.resources.utils.Information;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

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
        Git.checkout(pathDeveloperMergedRepository, Information.DEVELOPER_MERGE_REVISION);
        Git.checkout(pathLeftRepository, Information.LEFT_REVISION);
        Git.checkout(pathRightRepository, Information.RIGHT_REVISION);

        String pathConflict = pathMergedRepository + pathRelativeFile;
        String pathSolution = pathDeveloperMergedRepository + pathRelativeFile;
        String pathLeft = pathLeftRepository + pathRelativeFile;
        String pathRight = pathRightRepository + pathRelativeFile;

        //Getting conflicting file
        List<String> fileConflict = null;
        //Getting solution file
        List<String> fileSolution = null;
        //Getting left file
        List<String> leftFile = null;
        //Getting Right file
        List<String> rightFile = null;

        try {
            fileConflict = FileUtils.readLines(new File(pathConflict));
            fileSolution = FileUtils.readLines(new File(pathSolution));
            leftFile = FileUtils.readLines(new File(pathLeft));
            rightFile = FileUtils.readLines(new File(pathRight));
        } catch (IOException ex) {
            Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Getting conflict area
        int beginConflict, endConflict;
        beginConflict = ASTAuxiliar.getConflictLowerBound(getConflictChunk(), context);
        endConflict = ASTAuxiliar.getConflictUpperBound(getConflictChunk(), context, fileConflict);
        List<String> conflictingArea = fileConflict.subList(beginConflict, endConflict);

        //Getting parts of conflict area
        ConflictPartsExtractor cpe = new ConflictPartsExtractor(conflictingArea);
        cpe.extract();

        KindConflict leftKindConflict = new KindConflict();
        KindConflict rightKindConflict = new KindConflict();

        int beginLine = conflictChunk.getBeginLine();
        int separatorLine = (conflictChunk.getBeginLine()) + (cpe.getSeparator() - cpe.getBegin());
        int endLine = conflictChunk.getEndLine() - 1;

        String left = fileConflict.get(beginLine);
        String right = fileConflict.get(endLine);

        String leftRelativePath = ConflictingFileAnalyzer.getMove(left);
        String rightRelativePath = ConflictingFileAnalyzer.getMove(right);

        if (leftRelativePath == null) {
            leftRelativePath = pathRelativeFile;
        }

        if (rightRelativePath == null) {
            rightRelativePath = pathRelativeFile;
        }

//        System.out.println(fileConflict.get(beginLine));//<<<<<<HEAD
//        System.out.println(fileConflict.get(separatorLine));//===========
//        System.out.println(fileConflict.get(endLine));//>>>>>>>>>

        if (pathRelativeFile.contains(".java")) {
            try {
                leftKindConflict = ASTAuxiliar.getLanguageConstructsJava(beginLine, separatorLine, pathMergedRepository, pathConflict, pathLeft);
                rightKindConflict = ASTAuxiliar.getLanguageConstructsJava(separatorLine, endLine, pathMergedRepository, pathConflict, pathRight);
            } catch (IOException ex) {
                Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            leftKindConflict = ASTAuxiliar.getLanguageConstructsAny(beginLine + 1, separatorLine - 1, pathMergedRepository, pathConflict, pathLeft);
            rightKindConflict = ASTAuxiliar.getLanguageConstructsAny(separatorLine + 1, endLine, pathMergedRepository, pathConflict, pathRight);
        }

        //Get the following data from the conflict:
        //- beginContext and endContext
        //- beginConflict and endConflict
        //- separator (?)
        //- initialVersion and finalVersion
        int context1bOriginal, context1eOriginal, context2bOriginal, context2eOriginal;
        int separator, begin, end;

        context1bOriginal = beginConflict + 1;
        context1eOriginal = getConflictChunk().getBeginLine();
        context2bOriginal = getConflictChunk().getEndLine() + 1;
        context2eOriginal = endConflict;

        if (context2bOriginal > context2eOriginal) {
            context2bOriginal = context2eOriginal;
        }
        begin = beginConflict;
        end = endConflict;
        separator = begin + cpe.getSeparator() - cpe.getBegin();

        Repositioning repositioning = new Repositioning(pathMergedRepository);

        int context1 = ConflictingChunk.checkContext1(context1bOriginal, context1eOriginal, repositioning, pathConflict, pathSolution, begin, separator, end);

        int context2 = ConflictingChunk.checkContext2(fileSolution, fileConflict, context2eOriginal, context2bOriginal, repositioning, pathConflict, pathSolution, separator, begin, end);

//        try {
//            System.out.println(context1Original + " => " + context1);
//            System.out.println("\t" + fileConflict.get(context1Original - 1));
//            System.out.println("\t" + fileSolution.get(context1 - 1));
//
//            System.out.println(context2Original + " => " + context2);
//            System.out.println("\t" + fileConflict.get(context2Original - 1));
//            System.out.println("\t" + fileSolution.get(context2 - 1));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        List<String> solutionArea = fileSolution.subList(context1 - 1, context2);

        String dd = DeveloperDecisionAnalyzer.getDeveloperDecision(cpe, solutionArea, context).toString();

        ShowCase showCase = new ShowCase(conflictingArea, solutionArea, LanguageConstruct.toString(leftKindConflict.getLanguageConstructs()),
                LanguageConstruct.toString(rightKindConflict.getLanguageConstructs()), dd);
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
