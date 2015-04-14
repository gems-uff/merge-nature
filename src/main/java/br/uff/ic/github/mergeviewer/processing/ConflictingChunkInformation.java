/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.merge.repositioning.Repositioning;
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
        String pathConflict = pathMergedRepository + pathRelativeFile;
        String pathSolution = pathDeveloperMergedRepository + pathRelativeFile;

        List<String> fileConflict = MergeUtils.fileToLines(pathConflict);
        //Getting solution file
        List<String> fileSolution = MergeUtils.fileToLines(pathSolution);

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

        //Get the following data from the conflict:
        //- beginContext and endContext
        //- beginConflict and endConflict
        //- separator (?)
        //- initialVersion and finalVersion
        int context1bOriginal, context1eOriginal, context2bOriginal, context2eOriginal;
        int separator, begin, end;
        String initialFile, finalFile;

        context1bOriginal = beginConflict + 1;
        context1eOriginal = getConflictChunk().getBegin();
        context2bOriginal = getConflictChunk().getEnd() + 1;
        context2eOriginal = endConflict;
        begin = beginConflict;
        end = endConflict;
        separator = begin + cpe.getSeparator() - cpe.getBegin();

        initialFile = pathConflict;
        finalFile = pathSolution;

        Repositioning repositioning = new Repositioning(pathMergedRepository);

        int context1 = -1, context2 = fileSolution.size() + 1;
        int context1Original = -1, context2Original = -1;

        //Context 1
        boolean changed = false;
        for (int i = context1bOriginal; i <= context1eOriginal; i++) {
            context1 = repositioning.repositioning(initialFile, finalFile, i);

            if (context1 != -1) {
                context1Original = i;
                changed = true;
                break;
            }
        }

        if (context1 == -1 && !changed) {
            int c1a0 = -1, c1b0 = -1;
            int c1a = -1, c1b = -1;

            for (int i = begin; i < separator; i++) {
                c1a = repositioning.repositioning(initialFile, finalFile, i);

                if (c1a != -1) {
                    c1a0 = i;
                    break;
                }
            }

            for (int i = separator + 1; i < end - 1; i++) {
                c1b = repositioning.repositioning(initialFile, finalFile, i);

                if (c1b != -1) {
                    c1b0 = i;
                    break;
                }
            }

            if (c1a != -1 || c1b != -1) {
                if (c1a == -1) {
                    context1 = c1b;
                    context1Original = c1b0;
                } else if (c1b == -1) {
                    context1 = c1a;
                    context1Original = c1a0;
                } else if (c1a < c1b) {
                    context1 = c1a;
                    context1Original = c1a0;
                } else {
                    context1 = c1b;
                    context1Original = c1b0;
                }
            }

        }

        //Context 2
        changed = false;
        for (int i = context2eOriginal; i >= context2bOriginal; i--) {
            context2 = repositioning.repositioning(initialFile, finalFile, i);

            if (context2 != -1) {
                context2Original = i;
                changed = true;
                break;
            }
        }

        if (context2 == fileSolution.size() && !changed) {

            int c2a0 = -1, c2b0 = -1;
            int c2a = -1, c2b = -1;

            for (int i = separator - 1; i > begin; i--) {
                c2a = repositioning.repositioning(initialFile, finalFile, i);

                if (c2a != -1) {
                    c2a0 = i;
                    break;
                }
            }

            for (int i = end - 1; i > separator; i--) {
                c2b = repositioning.repositioning(initialFile, finalFile, i);

                if (c2b != -1) {
                    c2b0 = i;
                    break;
                }
            }

            if (c2a != -1 || c2b != -1) {
                if (c2a == -1) {
                    context2 = c2b;
                    context2Original = c2b0;
                } else if (c2b == -1) {
                    context2 = c2a;
                    context2Original = c2a0;
                } else if (c2a < c2b) {
                    context2 = c2b;
                    context2Original = c2b0;
                } else {
                    context2 = c2a;
                    context2Original = c2a0;
                }
            }

        }

        if (context1 < 1) {
            context1 = 1;
        }
        if (context2 > fileSolution.size()) {
            context2 = fileSolution.size();
        }


//        System.out.println(context1Original + " => " + context1);
//        if (context1Original >= 1) {
//            System.out.println("\t" + fileConflict.get(context1Original - 1));
//        } else {
//            System.out.println("\t Empty!");
//        }
//        if (context1 != -1) {
//            System.out.println("\t" + fileSolution.get(context1 - 1));
//        } else {
//            System.out.println("\t" + "Deleted");
//        }
//
//        System.out.println(context2Original + " => " + context2);
//        if (context2Original >= 1) {
//            System.out.println("\t" + fileConflict.get(context2Original - 1));
//        } else {
//            System.out.println("\t Empty!");
//        }
//        if (context2 != -1) {
//            System.out.println("\t" + fileSolution.get(context2 - 1));
//        } else {
//            System.out.println("\t" + "Deleted");
//        }

        List<String> solutionArea = fileSolution.subList(context1 - 1, context2);

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
