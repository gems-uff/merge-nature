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
import br.uff.ic.gems.resources.analises.merge.DeveloperDecisionAnalyzer;
import br.uff.ic.gems.resources.ast.ASTExtractor;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.states.DeveloperDecision;
import br.uff.ic.gems.resources.utils.Information;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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

        int beginLine = conflictChunk.getBeginLine();
        int separatorLine = conflictChunk.getSeparatorLine();
        int endLine = conflictChunk.getEndLine();
        
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
            List<String> removedFiles = Git.removedFiles(pathMergedRepository, Information.LEFT_REVISION, Information.DEVELOPER_MERGE_REVISION);
            String replaceFirst = pathRelativeFile.replaceFirst(File.separator, "");
            if(removedFiles.contains(replaceFirst))
                JOptionPane.showMessageDialog(null, "The developer removed the solution file. Manual solution.");
            return;
        }

        //Getting conflict area
        int beginConflict, endConflict;
        beginConflict = ASTAuxiliar.getConflictLowerBound(beginLine, context);
        endConflict = ASTAuxiliar.getConflictUpperBound(endLine, context, fileConflict);
        List<String> conflictingArea = fileConflict.subList(beginConflict, endConflict);

        
        //Gettign remane
        KindConflict leftKindConflict = new KindConflict();
        KindConflict rightKindConflict = new KindConflict();

        

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
        /*----------------------------------------------------------------------
                       Getting language constructs
        ----------------------------------------------------------------------*/
        
        ASTExtractor leftAST = new ASTExtractor(pathLeft);
        
        
        ASTExtractor rightAST = new ASTExtractor(pathRight);
        try {
            leftAST.parser();
            rightAST.parser();
        } catch (IOException ex) {
            Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (pathRelativeFile.contains(".java")) {
            try {
                leftKindConflict = ASTAuxiliar.getLanguageConstructsJava(beginLine, separatorLine, pathMergedRepository, pathConflict, pathLeft, leftAST);
                rightKindConflict = ASTAuxiliar.getLanguageConstructsJava(separatorLine, endLine, pathMergedRepository, pathConflict, pathRight, rightAST);
            } catch (IOException ex) {
                Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            leftKindConflict = ASTAuxiliar.getLanguageConstructsAny(beginLine + 1, separatorLine - 1, pathMergedRepository, pathConflict, pathLeft);
            rightKindConflict = ASTAuxiliar.getLanguageConstructsAny(separatorLine + 1, endLine, pathMergedRepository, pathConflict, pathRight);
        }

        /*----------------------------------------------------------------------
                       Getting solution area
        ----------------------------------------------------------------------*/
        
        //Get the following data from the conflict:
        //- beginContext and endContext
        //- beginConflict and endConflict
        //- separator (?)
        //- initialVersion and finalVersion
        int context1bOriginal, context1eOriginal, context2bOriginal, context2eOriginal;
//        int separator, begin, end;

        
        //Changing to file reference( lines from 1 to n) instead of list reference (0..n-1)
        context1bOriginal = beginConflict + 1;
        context1eOriginal = getConflictChunk().getBeginLine();//+ 1 - 1
        context2bOriginal = getConflictChunk().getEndLine() + 1;
        context2eOriginal = endConflict;//+ 1 - 1

        if (context2bOriginal > context2eOriginal) {
            context2bOriginal = context2eOriginal;
        }

        Repositioning repositioning = new Repositioning(pathMergedRepository);

        int context1 = ConflictingChunk.checkContext1(context1bOriginal, context1eOriginal, 
                repositioning, pathConflict, pathSolution, beginLine, separatorLine, endLine);

        int context2 = ConflictingChunk.checkContext2(fileSolution, fileConflict, context2eOriginal, 
                context2bOriginal, repositioning, pathConflict, pathSolution, separatorLine, beginLine, endLine);

        List<String> solutionArea = fileSolution.subList(context1 - 1, context2);

        int beginConflictingArea, separatorConflictingArea, endConflictingArea;
        beginConflictingArea = beginLine - beginConflict;
        separatorConflictingArea = separatorLine - beginConflict;
        endConflictingArea = endLine - beginConflict;
        
//        String dd = DeveloperDecisionAnalyzer.getDeveloperDecision(cpe, solutionArea, context).toString();
        String dd;
        try {
            dd = DeveloperDecisionAnalyzer.developerDevision(conflictingArea, solutionArea, 
                    beginConflictingArea, separatorConflictingArea, endConflictingArea).toString();
        } catch (Exception ex) {
            dd = DeveloperDecision.MANUAL.toString();
            Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
        }

        ShowCase showCase = new ShowCase(conflictingArea, solutionArea, leftKindConflict.getFilteredLanguageConstructs().toString(),
                rightKindConflict.getFilteredLanguageConstructs().toString(), dd);
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
