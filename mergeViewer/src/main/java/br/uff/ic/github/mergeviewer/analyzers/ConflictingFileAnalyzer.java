/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.analyzers;

import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.ast.ASTAuxiliar;
import br.uff.ic.github.mergeviewer.kinds.ConflictingChunk;
import br.uff.ic.github.mergeviewer.kinds.ConflictingFile;
import br.uff.ic.github.mergeviewer.processing.ConflictingChunkInformation;
import br.uff.ic.github.mergeviewer.util.ConflictPartsExtractor;
import br.uff.ic.github.mergeviewer.util.ContextFinder;
import br.uff.ic.github.mergeviewer.util.DeveloperChoice;
import br.uff.ic.github.mergeviewer.util.DeveloperDecision;
import br.uff.ic.github.mergeviewer.util.Information;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class ConflictingFileAnalyzer {

    public static ConflictingFile analyze(String conflictingFilePath, String repositoryPath, String leftSHA, String rightSHA, String developerSolutionSHA) throws IOException {

        int context = 3;
        boolean hasSolution = true;

        System.out.println("\t" + conflictingFilePath);
        ConflictingFile conflictingFile = new ConflictingFile(conflictingFilePath);

        List<String> conflictingFileList;
        conflictingFileList = FileUtils.readLines(new File(conflictingFile.getPath()));
        List<ConflictingChunk> conflictingChunks = getConflictingChunks(conflictingFileList);

        String relativePath = conflictingFilePath.replace(repositoryPath, "");

        String leftRepository = repositoryPath + Information.LEFT_REPOSITORY_SUFIX;
        String rightRepository = repositoryPath + Information.RIGHT_REPOSITORY_SUFIX;
        String developerMergedRepository = repositoryPath + Information.DEVELOPER_MERGE_REPOSITORY_SUFIX;

        //Cloning 
        ASTAuxiliar.cloneRepositories(repositoryPath, leftRepository, rightRepository, developerMergedRepository);

        //Checking out revisions
        GitCMD.checkout(leftRepository, leftSHA);
        GitCMD.checkout(rightRepository, rightSHA);
        GitCMD.checkout(developerMergedRepository, developerSolutionSHA);

        List<String> ConflictingFile = FileUtils.readLines(new File(repositoryPath + relativePath));
//        List<String> leftConflictingFile = FileUtils.readLines(new File(leftRepository + relativePath));
//        List<String> rightConflictingFile = FileUtils.readLines(new File(rightRepository + relativePath));

        List<String> solutionFile = null;

        try {
            solutionFile = FileUtils.readLines(new File(developerMergedRepository + relativePath));
        } catch (IOException e) {
            hasSolution = false;
        }
        for (ConflictingChunk conflictingChunk : conflictingChunks) {

            System.out.println(conflictingChunk.getIdentifier());
            int conflictLowerBound = ASTAuxiliar.getConflictLowerBound(conflictingChunk, context);
            int conflictUpperBound = ASTAuxiliar.getConflictUpperBound(conflictingChunk, context, conflictingFileList);
            List<String> conflictingArea = ConflictingFile.subList(conflictLowerBound, conflictUpperBound);

            ConflictPartsExtractor cpe = new ConflictPartsExtractor(conflictingArea);
            cpe.extract();

            List<String> beginContext = cpe.getBeginContext();
            List<String> endContext = cpe.getEndContext();

            String leftSS = null;
            String rightSS = null;

            String leftRelativePath = relativePath;
            if (cpe.getLeftFile() != null) {
                leftRelativePath = File.separator + cpe.getLeftFile();
            }

            String rightRelativePath = relativePath;
            if (cpe.getRightFile() != null) {
                rightRelativePath = File.separator + cpe.getRightFile();
            }

            if (relativePath.contains(".java")) {
                try {
                    leftSS = ASTAuxiliar.getSyntacticStructures(cpe.getLeftConflict(), leftRepository, leftRelativePath);
                    rightSS = ASTAuxiliar.getSyntacticStructures(cpe.getRightConflict(), rightRepository, rightRelativePath);
                } catch (IOException ex) {
                    Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            List<String> solutionArea = null;
            if (hasSolution) {
                solutionArea = ContextFinder.getSolution(beginContext, endContext, solutionFile,
                        conflictingChunk.getBegin(), conflictingChunk.getEnd());
                DeveloperChoice developerDecision = DeveloperDecision.getDeveloperDecision(cpe, solutionArea, context);
                conflictingChunk.setDeveloperChoice(developerDecision);
            } else {
                conflictingChunk.setDeveloperChoice(DeveloperChoice.MANUAL);
            }

        }
        //TODO: Continue

        conflictingFile.setConflictingChunks(conflictingChunks);

        return conflictingFile;
    }

    private static List<ConflictingChunk> getConflictingChunks(List<String> fileList) {
        List<ConflictingChunk> result = new ArrayList<>();
        ConflictingChunk conflictingChunk = new ConflictingChunk();
        int begin = 0, separator = 0, end, identifier = 1;

        for (int i = 0; i < fileList.size(); i++) {
            String get = fileList.get(i);

            if (get.contains("<<<<<<<")) {
                conflictingChunk = new ConflictingChunk();
                begin = i;
            } else if (get.contains("=======")) {
                separator = i;
            } else if (get.contains(">>>>>>>")) {
                end = i;

                conflictingChunk.setBegin(begin);
                conflictingChunk.setSeparator(separator);
                conflictingChunk.setEnd(end);
                conflictingChunk.setIdentifier("Case " + (identifier++));

                result.add(conflictingChunk);
            }

        }

        return result;
    }
}
