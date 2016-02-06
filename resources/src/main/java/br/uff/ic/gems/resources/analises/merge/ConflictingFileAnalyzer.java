/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.ast.ASTAuxiliar;
import br.uff.ic.gems.resources.ast.ASTExtractor;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.states.DeveloperDecision;
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

    public static ConflictingFile analyze(String conflictingFilePath, String repositoryPath, String leftSHA, String rightSHA, String developerSolutionSHA) throws IOException, StackOverflowError {

        int context = 3;
        boolean hasSolution = true;
        ConflictingFile conflictingFile = new ConflictingFile(conflictingFilePath);

        List<String> conflictingFileList;
        conflictingFileList = FileUtils.readLines(new File(conflictingFile.getPath()));
        List<ConflictingChunk> conflictingChunks = getConflictingChunks(conflictingFileList);

        //Treating when conflict files does not have well-formed chunks
        if(conflictingChunks == null)
            return conflictingFile;
        
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
        List<String> conflictingContent = null;
        List<String> solutionContent = null;

        try {
            conflictingContent = FileUtils.readLines(new File(conflictPath));
            solutionContent = FileUtils.readLines(new File(solutionPath));
        } catch (Exception e) {
            //If there is no solution

            List<String> removedFiles = Git.removedFiles(repositoryPath, leftSHA, developerSolutionSHA);
            String replaceFirst = relativePath.replaceFirst(File.separator, "");
            if (removedFiles.contains(replaceFirst)) {
                conflictingFile.setRemoved(true);
            }

            conflictingFile.setConflictingChunks(conflictingChunks);
            
            return conflictingFile;

        }

        //If there is solution
        //Marks from conflicting chunks
        int beginLine = -1;
        int separatorLine = -1;
        int endLine = -1;

        String leftRelativePath = relativePath;
        String rightRelativePath = relativePath;

        if (conflictingChunks != null && !conflictingChunks.isEmpty()) {
            beginLine = conflictingChunks.get(0).getBeginLine();
            separatorLine = conflictingChunks.get(0).getSeparatorLine();
            endLine = conflictingChunks.get(0).getEndLine();

            String left = conflictingContent.get(beginLine);
            String right = conflictingContent.get(endLine);

            leftRelativePath = getMove(left);
            rightRelativePath = getMove(right);

            if (leftRelativePath == null) {
                leftRelativePath = relativePath;
            }

            if (rightRelativePath == null) {
                rightRelativePath = relativePath;
            }
        }

        String currentFile, leftFile, rightFile;

        currentFile = repositoryPath + File.separator + relativePath;
        leftFile = leftRepository + File.separator + leftRelativePath;
        rightFile = rightRepository + File.separator + rightRelativePath;

        ASTExtractor leftAST = new ASTExtractor(leftFile);
        leftAST.parser();

        ASTExtractor rightAST = new ASTExtractor(rightFile);
        rightAST.parser();

        for (ConflictingChunk conflictingChunk : conflictingChunks) {

            //Marks from conflicting chunks
            beginLine = conflictingChunk.getBeginLine();
            separatorLine = conflictingChunk.getSeparatorLine();
            endLine = conflictingChunk.getEndLine();

            //Getting conflict area
            int beginConflict, endConflict;
            beginConflict = ASTAuxiliar.getConflictLowerBound(beginLine, context);
            endConflict = ASTAuxiliar.getConflictUpperBound(endLine, context, conflictingContent);
            List<String> conflictingArea = null;

            conflictingArea = conflictingContent.subList(beginConflict, endConflict);

            KindConflict leftKindConflict = new KindConflict();
            KindConflict rightKindConflict = new KindConflict();
            try {

                if (conflictingFilePath.endsWith(".java")) {
                    leftKindConflict = ASTAuxiliar.getLanguageConstructsJava(beginLine, separatorLine, repositoryPath, currentFile, leftFile, leftAST);
                    rightKindConflict = ASTAuxiliar.getLanguageConstructsJava(separatorLine, endLine, repositoryPath, currentFile, rightFile, rightAST);
                } else {

                    leftKindConflict = ASTAuxiliar.getLanguageConstructsAny(beginLine, separatorLine, repositoryPath, currentFile, leftFile);
                    rightKindConflict = ASTAuxiliar.getLanguageConstructsAny(separatorLine, endLine, repositoryPath, currentFile, rightFile);

                }
            } catch (IOException ex) {
                Logger.getLogger(ConflictingFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Get the following data from the conflict:
            //- beginContext and endContext
            //- beginConflict and endConflict
            //- separator (?)
            //- initialVersion and finalVersion
            int context1bOriginal, context1eOriginal, context2bOriginal, context2eOriginal;

            context1bOriginal = beginConflict + 1;
            context1eOriginal = conflictingChunk.getBeginLine();
            context2bOriginal = conflictingChunk.getEndLine() + 1;
            context2eOriginal = endConflict;

            if (context2bOriginal > context2eOriginal) {
                context2bOriginal = context2eOriginal;
            }

            Repositioning repositioning = new Repositioning(developerMergedRepository);

            int context1 = ConflictingChunk.checkContext1(context1bOriginal, context1eOriginal,
                    repositioning, conflictingFilePath, solutionPath, beginLine, separatorLine, endLine);

            int context2 = ConflictingChunk.checkContext2(solutionContent, conflictingContent, context2eOriginal,
                    context2bOriginal, repositioning, conflictingFilePath, solutionPath, separatorLine, beginLine, endLine);

            List<String> solutionArea;
            solutionArea = solutionContent.subList(context1 - 1, context2);


            /*
             Calculating values of begin, separator and end mark in the conflicting area
             */
            int beginConflictingArea, separatorConflictingArea, endConflictingArea;
            beginConflictingArea = beginLine - beginConflict;
            separatorConflictingArea = separatorLine - beginConflict;
            endConflictingArea = endLine - beginConflict;

            DeveloperDecision developerDecision = DeveloperDecision.MANUAL;

            try {
                //SetSolution
                developerDecision = DeveloperDecisionAnalyzer.developerDevision(conflictingArea, solutionArea,
                        beginConflictingArea, separatorConflictingArea, endConflictingArea);
            } catch (Exception ex) {
                Logger.getLogger(ConflictingFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
            conflictingChunk.setDeveloperDecision(developerDecision);

            try {

                conflictingChunk.setLeftKindConflict(leftKindConflict);
                conflictingChunk.setRightKindConflict(rightKindConflict);
                conflictingChunk.setConflictingContent(conflictingArea);
                conflictingChunk.setSolutionContent(solutionArea);

            } catch (Exception ex) {
                Logger.getLogger(ConflictingFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        conflictingFile.setConflictingChunks(conflictingChunks);

        return conflictingFile;
    }

    public static List<ConflictingChunk> getConflictingChunks(List<String> fileList) {

        List<ConflictingChunk> result = new ArrayList<>();
        ConflictingChunk conflictingChunk = new ConflictingChunk();
        int begin = 0, separator = 0, end = 0, identifier = 1;

        List<Integer> begins, separators, ends;
        begins = new ArrayList<>();
        separators = new ArrayList<>();
        ends = new ArrayList<>();

        for (int i = 0; i < fileList.size(); i++) {
            String get = fileList.get(i);

            if (get.startsWith("<<<<<<<")) {
                begin = i;
                begins.add(begin);
            } else if (get.startsWith("=======")) {
                separator = i;
                separators.add(separator);
            } else if (get.startsWith(">>>>>>>")) {
                end = i;
                ends.add(end);
            }

        }

        if(begins.size() != separators.size() || separators.size() != ends.size())
            return null;
        
        while (!(begins.isEmpty() || separators.isEmpty() || ends.isEmpty())) {
            Integer b = 0, s = 0, e = 0;

            for (int i = 0; i < begins.size(); i++) {
                b = begins.get(i);

                if (isNextSeparator(b, begins, separators, ends)) {
                    s = nextValue(b, begins, separators, ends);
                    e = nextValue(s, begins, separators, ends);

                    begins.remove(b);
                    separators.remove(s);
                    ends.remove(e);

                    break;
                }
            }

            conflictingChunk = new ConflictingChunk();
            conflictingChunk.setBeginLine(b);
            conflictingChunk.setSeparatorLine(s);
            conflictingChunk.setEndLine(e);
            conflictingChunk.setIdentifier("Conflicting chunk " + (identifier++));

            result.add(conflictingChunk);
        }

        return result;
    }

    public static String getMove(String line) {

        if (line.contains(":")) {
            String[] split = line.split(":");
            return split[split.length - 1];
        }

        return null;

    }

    private static final String BEGIN = "b";
    private static final String SEPARATOR = "s";
    private static final String END = "e";

    private static Boolean isNextBegin(int line, List<Integer> begin, List<Integer> separator, List<Integer> end) {
        String whatsNext = whatsNext(line, begin, separator, end);

        if (whatsNext == null) {
            return false;
        } else {
            return whatsNext.equals(BEGIN);
        }
    }

    private static Boolean isNextSeparator(int line, List<Integer> begin, List<Integer> separator, List<Integer> end) {
        String whatsNext = whatsNext(line, begin, separator, end);

        if (whatsNext == null) {
            return false;
        } else {
            return whatsNext.equals(SEPARATOR);
        }
    }

    private static Boolean isNextEnd(int line, List<Integer> begin, List<Integer> separator, List<Integer> end) {
        String whatsNext = whatsNext(line, begin, separator, end);

        if (whatsNext == null) {
            return false;
        } else {
            return whatsNext.equals(END);
        }
    }

    private static String whatsNext(int line, List<Integer> begin, List<Integer> separator, List<Integer> end) {
        int next = Integer.MAX_VALUE;
        String result = null;

        for (Integer b : begin) {
            if (b > line && b < next) {
                next = b;
                result = BEGIN;
            } else if (b >= next) {
                break;
            }
        }

        for (Integer s : separator) {
            if (s > line && s < next) {
                next = s;
                result = SEPARATOR;
            } else if (s >= next) {
                break;
            }
        }

        for (Integer e : end) {
            if (e > line && e < next) {
                next = e;
                result = END;
            } else if (e >= next) {
                break;
            }
        }

        return result;
    }

    private static Integer nextValue(int line, List<Integer> begin, List<Integer> separator, List<Integer> end) {
        Integer result = Integer.MAX_VALUE;

        for (Integer b : begin) {
            if (b > line && b < result) {
                result = b;
            } else if (b >= result) {
                break;
            }
        }

        for (Integer s : separator) {
            if (s > line && s < result) {
                result = s;
            } else if (s >= result) {
                break;
            }
        }

        for (Integer e : end) {
            if (e > line && e < result) {
                result = e;
            } else if (e >= result) {
                break;
            }
        }

        return result;
    }

}
