/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.ast.ASTAuxiliar;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.dao.ConflictingChunkDAO;
import br.uff.ic.gems.resources.data.dao.KindConflictDAO;
import br.uff.ic.gems.resources.data.dao.LanguageConstructDAO;
import br.uff.ic.gems.resources.states.DeveloperDecision;
import br.uff.ic.gems.resources.utils.ConflictPartsExtractor;
import br.uff.ic.gems.resources.utils.Information;
import br.uff.ic.gems.resources.vcs.Git;
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
        LanguageConstructDAO languageConstructDAO = new LanguageConstructDAO();
        ConflictingChunkDAO conflictingChunkDAO = new ConflictingChunkDAO();
        KindConflictDAO kindConflictDAO = new KindConflictDAO();

        ConflictingFile conflictingFile = new ConflictingFile(conflictingFilePath);

        List<String> conflictingFileList;
        conflictingFileList = FileUtils.readLines(new File(conflictingFile.getPath()));
        List<ConflictingChunk> conflictingChunks = getConflictingChunks(conflictingFileList);

        String relativePath = conflictingFilePath.replace(repositoryPath, "");

        //Paths to repositories
        String leftRepository = repositoryPath + Information.LEFT_REPOSITORY_SUFIX;
        String rightRepository = repositoryPath + Information.RIGHT_REPOSITORY_SUFIX;
        String developerMergedRepository = repositoryPath + Information.DEVELOPER_MERGE_REPOSITORY_SUFIX;

        //Cloning
        ASTAuxiliar.cloneRepositories(repositoryPath, leftRepository, rightRepository, developerMergedRepository);

        //Checking out revisions
        Git.checkout(leftRepository, leftSHA);
        Git.checkout(rightRepository, rightSHA);
        Git.checkout(developerMergedRepository, developerSolutionSHA);

        //Paths to files
        String solutionPath = developerMergedRepository + relativePath;
        String conflictPath = repositoryPath + relativePath;

        //Files contenct
        List<String> conflictingContent = FileUtils.readLines(new File(conflictPath));
        List<String> solutionContent = FileUtils.readLines(new File(solutionPath));

        for (ConflictingChunk conflictingChunk : conflictingChunks) {

            //Getting conflict area
            int beginConflict, endConflict;
            beginConflict = ASTAuxiliar.getConflictLowerBound(conflictingChunk, context);
            endConflict = ASTAuxiliar.getConflictUpperBound(conflictingChunk, context, conflictingContent);

            List<String> conflictingArea = conflictingContent.subList(beginConflict, endConflict);

            //Getting parts of conflict area
            ConflictPartsExtractor cpe = new ConflictPartsExtractor(conflictingArea);
            cpe.extract();

            KindConflict leftKindConflict = new KindConflict();
            KindConflict rightKindConflict = new KindConflict();

            int beginLine = conflictingChunk.getBeginLine() + 1;
            int separatorLine = (conflictingChunk.getBeginLine() + 1) + (cpe.getSeparator() - cpe.getBegin());
            int endLine = conflictingChunk.getEndLine();

            if (conflictingFilePath.contains(".java")) {
                try {
                    leftKindConflict = ASTAuxiliar.getLanguageConstructsJava(beginLine + 1, separatorLine - 1, repositoryPath, leftRepository, relativePath);
                    rightKindConflict = ASTAuxiliar.getLanguageConstructsJava(separatorLine + 1, endLine, repositoryPath, rightRepository, relativePath);
                } catch (IOException ex) {
                    Logger.getLogger(ConflictingFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

                leftKindConflict = ASTAuxiliar.getLanguageConstructsAny(beginLine + 1, separatorLine - 1, repositoryPath, leftRepository, relativePath);
                rightKindConflict = ASTAuxiliar.getLanguageConstructsAny(separatorLine + 1, endLine, repositoryPath, rightRepository, relativePath);
            
            }

            //Get the following data from the conflict:
            //- beginContext and endContext
            //- beginConflict and endConflict
            //- separator (?)
            //- initialVersion and finalVersion
            int context1bOriginal, context1eOriginal, context2bOriginal, context2eOriginal;
            int separator, begin, end;
            String initialPath, finalPath;

            context1bOriginal = beginConflict + 1;
            context1eOriginal = conflictingChunk.getBeginLine();
            context2bOriginal = conflictingChunk.getEndLine() + 1;
            context2eOriginal = endConflict;

            if (context2bOriginal > context2eOriginal) {
                context2bOriginal = context2eOriginal;
            }
            begin = beginConflict;
            end = endConflict;
            separator = begin + cpe.getSeparator() - cpe.getBegin();

            initialPath = conflictingFilePath;
            finalPath = solutionPath;

            Repositioning repositioning = new Repositioning(developerMergedRepository);

            int context1 = ConflictingChunk.checkContext1(context1bOriginal, context1eOriginal, repositioning, initialPath, finalPath, begin, separator, end);

            int context2 = ConflictingChunk.checkContext2(solutionContent, conflictingContent, context2eOriginal, context2bOriginal, repositioning, initialPath, finalPath, separator, begin, end);

            List<String> solutionArea = solutionContent.subList(context1 - 1, context2);

            String dd = DeveloperDecisionAnalyzer.getDeveloperDecision(cpe, solutionArea, context).toString();

            DeveloperDecision developerDecision = DeveloperDecision.MANUAL;
            if (hasSolution) {
                //SetSolution
                developerDecision = DeveloperDecisionAnalyzer.getDeveloperDecision(cpe, solutionArea, context);
                conflictingChunk.setDeveloperDecision(developerDecision);
            }

//            System.out.println("=================" + conflictingChunk.getIdentifier() + "=================");
//            System.out.println("=================Conflicting area=================");
//            conflictingChunk.setConflictingContent(conflictingArea);
//            for (String ca : conflictingArea) {
//                System.out.println(ca);
//            }
//            System.out.println("=================Solution area=================");
//            conflictingChunk.setSolutionContent(solutionArea);
//            for (String sa : solutionArea) {
//                System.out.println(sa);
//            }
//            System.out.println("=================Developers` decision=================");
//            conflictingChunk.setDeveloperDecision(developerDecision);
//            System.out.println(developerDecision.toString());
//            System.out.println("=================Left language constructs=================");
//            conflictingChunk.setLeftLanguageConstructs(leftLanguageConstructs);
//            System.out.println(LanguageConstruct.toString(leftLanguageConstructs));
//            System.out.println("=================Right language constructs=================");
//            conflictingChunk.setRightLanguageConstructs(rightLanguageConstructs);
//            System.out.println(LanguageConstruct.toString(rightLanguageConstructs));
            try {

//                for (LanguageConstruct leftLanguageConstruct : leftKindConflict.getLanguageConstructsJava()) {
//                    languageConstructDAO.save(leftLanguageConstruct);
//                }
//                for (LanguageConstruct rightLanguageConstruct : rightKindConflict.getLanguageConstructsJava()) {
//                    languageConstructDAO.save(rightLanguageConstruct);
//                }
//                kindConflictDAO.save(leftKindConflict);
//                kindConflictDAO.save(rightKindConflict);
                conflictingChunk.setLeftKindConflict(leftKindConflict);
                conflictingChunk.setRightKindConflict(rightKindConflict);
                conflictingChunk.setConflictingContent(conflictingArea);
                conflictingChunk.setSolutionContent(solutionArea);

//                conflictingChunkDAO.save(conflictingChunk);
            } catch (Exception ex) {
                Logger.getLogger(ConflictingFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        conflictingFile.setConflictingChunks(conflictingChunks);

        return conflictingFile;
    }

        private static List<ConflictingChunk> getConflictingChunks(List<String> fileList) {
        List<ConflictingChunk> result = new ArrayList<>();
        ConflictingChunk conflictingChunk = new ConflictingChunk();
        int begin = 0, separator = 0, end = 0, identifier = 1;

        List<Integer> begins, separators, ends;
        begins = new ArrayList<>();
        separators = new ArrayList<>();
        ends = new ArrayList<>();

        for (int i = 0; i < fileList.size(); i++) {
            String get = fileList.get(i);

            if (get.contains("<<<<<<<")) {
                begin = i;
                begins.add(begin);
            } else if (get.contains("=======")) {
                separator = i;
                separators.add(separator);
            } else if (get.contains(">>>>>>>")) {
                end = i;
                ends.add(end);
            }

        }

        while (!(begins.isEmpty() || separators.isEmpty() || ends.isEmpty())) {

            for (int i = begins.size() - 1; i >= 0; i--) {
                Integer b = begins.get(i);

                for (int j = 0; j < separators.size(); j++) {
                    Integer s = separators.get(j);

                    if (s > b) {
                        for (int k = 0; k < ends.size(); k++) {
                            Integer e = ends.get(k);
                            if (e > s) {
                                ends.remove(e);
                                break;
                            }
                        }
                        separators.remove(s);
                        break;
                    }
                }
                begins.remove(b);
                break;
            }

            conflictingChunk = new ConflictingChunk();
            conflictingChunk.setBeginLine(begin);
            conflictingChunk.setSeparatorLine(separator);
            conflictingChunk.setEndLine(end);
            conflictingChunk.setIdentifier("Case " + (identifier++));
            
            result.add(conflictingChunk);
        }

        return result;
    }
}
