/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.analises.merge.DeveloperDecisionAnalyzer;
import br.uff.ic.gems.resources.ast.ASTAuxiliar;
import br.uff.ic.gems.resources.ast.ASTExtractor;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.gems.resources.operation.OperationType;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.states.DeveloperDecision;
import br.uff.ic.gems.resources.utils.Information;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Gleiph, Cristiane
 */
public class FileAnalyzer {

    public static CFile analyze(String FilePath, String repositoryPath, String leftSHA, String rightSHA, String developerSolutionSHA) throws IOException, StackOverflowError {

        int context = 3;
        boolean hasSolution = true;
        CFile CFile = new CFile(FilePath);

        List<String> FileList;
        FileList = FileUtils.readLines(new File(CFile.getPath()));
        List<Chunk> Chunks = getChunks(FileList, leftSHA, developerSolutionSHA );

        //Treating when conflict files does not have well-formed chunks
        if (Chunks == null) {
            return CFile;
        }

        String relativePath = FilePath.replace(repositoryPath, "");

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
                CFile.setRemoved(true);
            }

            CFile.setChunks(Chunks);

            return CFile;

        }

        //If there is solution
        //Marks from conflicting chunks
        int addedLine = -1;
        int removedLine = -1;
        int endLine = -1;

        String leftRelativePath = relativePath;
        String rightRelativePath = relativePath;

        //if (Chunks != null && !Chunks.isEmpty()) {
        String left = conflictingContent.get(addedLine);
        String right = conflictingContent.get(removedLine);

        leftRelativePath = getMove(left);
        rightRelativePath = getMove(right);

        if (leftRelativePath == null) {
            leftRelativePath = relativePath;
        }

        if (rightRelativePath == null) {
            rightRelativePath = relativePath;
        }
        // }

        String currentFile, leftFile, rightFile;

        currentFile = repositoryPath + File.separator + relativePath;
        leftFile = leftRepository + File.separator + leftRelativePath;
        rightFile = rightRepository + File.separator + rightRelativePath;

        ASTExtractor leftAST = new ASTExtractor(leftFile);
        leftAST.parser();

        ASTExtractor rightAST = new ASTExtractor(rightFile);
        rightAST.parser();

        for (Chunk Chunk : Chunks) {

            //Marks from chunks
            //Getting conflict area
            int beginConflict, endConflict;
            beginConflict = ASTAuxiliar.getConflictLowerBound(addedLine, context);
            endConflict = ASTAuxiliar.getConflictUpperBound(removedLine, context, conflictingContent);
            List<String> conflictingArea = null;

            conflictingArea = conflictingContent.subList(beginConflict, endConflict);

            KindConflict leftKindConflict = new KindConflict();
            KindConflict rightKindConflict = new KindConflict();
            try {

                if (FilePath.endsWith(".java")) {
                    leftKindConflict = ASTAuxiliar.getLanguageConstructsJava(addedLine, removedLine, repositoryPath, currentFile, leftFile, leftAST);
                    rightKindConflict = ASTAuxiliar.getLanguageConstructsJava(addedLine, removedLine, repositoryPath, currentFile, rightFile, rightAST);
                } else {

                    leftKindConflict = ASTAuxiliar.getLanguageConstructsAny(addedLine, removedLine, repositoryPath, currentFile, leftFile);
                    rightKindConflict = ASTAuxiliar.getLanguageConstructsAny(addedLine, removedLine, repositoryPath, currentFile, rightFile);

                }
            } catch (IOException ex) {
                Logger.getLogger(FileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Get the following data from the conflict:
            //- beginContext and endContext
            //- beginConflict and endConflict
            //- separator (?)
            //- initialVersion and finalVersion
            int context1bOriginal, context1eOriginal, context2bOriginal, context2eOriginal;

            context1bOriginal = beginConflict + 1;
            // context1eOriginal = Chunk.getAddedLine();
            //context2bOriginal = Chunk.getRemovedLine() + 1;
            context2eOriginal = endConflict;

            // if (context2bOriginal > context2eOriginal) {
            //     context2bOriginal = context2eOriginal;
            // }
            Repositioning repositioning = new Repositioning(developerMergedRepository);

            //int context1 = Chunk.checkContext1(context1bOriginal, context1eOriginal,
            //repositioning, FilePath, solutionPath, addedLine, removedLine);
            //int context2 = Chunk.checkContext2(solutionContent, conflictingContent, context2eOriginal,
            //context2bOriginal, repositioning, FilePath, solutionPath, separatorLine, beginLine, endLine);
            //List<String> solutionArea;
            //solutionArea = solutionContent.subList(context1 - 1, context2);
            /*
             Calculating values of begin, separator and end mark in the conflicting area
             */
            // int addedArea, removedArea;
            //  addedArea = addedLine - beginConflict;
            // removedArea = removedLine - beginConflict;
            //endConflictingArea = endLine - beginConflict;
            DeveloperDecision developerDecision = DeveloperDecision.MANUAL;

            try {
                //SetSolution
                //developerDecision = DeveloperDecisionAnalyzer.developerDevision(conflictingArea, solutionArea,
                //beginConflictingArea, separatorConflictingArea, endConflictingArea);
            } catch (Exception ex) {
                Logger.getLogger(FileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

            } catch (Exception ex) {
                Logger.getLogger(FileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        CFile.setChunks(Chunks);

        return CFile;
    }

    

    public static List<Chunk> getChunks(List<String> fileDiff, String filePath, String branch) {
        List<Chunk> result = new ArrayList<>();
        Chunk Chunk = new Chunk();
        String chunkID;
        int line;

        GitTranslator gitTranslator = new GitTranslator();
        List<Operation> operations = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        //List<Operation> operationsCluster = new ArrayList<>();

        String delta = fileDiff.toString();

        operations = gitTranslator.translateDelta(delta); 
        operationsBase = gitTranslator.translateDeltaBase(delta);
        //operationsCluster = gitTranslator.cluster(operations);

        Chunk = new Chunk();
        
        chunkID = filePath.substring(filePath.lastIndexOf('/')+ 1, filePath.length()-5);//class name
        
        Chunk.setOperations(operations); 
        Chunk.setOperationsBase(operationsBase);
        Chunk.setIdentifier(branch + chunkID);

        result.add(Chunk);

        return result;
    }

    public static String getMove(String line) {

        if (line.contains(":")) {
            String[] split = line.split(":");
            return split[split.length - 1];
        }

        return null;

    }

    private static final String ADDEDLINE = "a";
    private static final String REMOVEDLINE = "r";

    private static Boolean isNextAdded(int line, List<Integer> added, List<Integer> removed) {
        String whatsNext = whatsNext(line, added, removed);

        if (whatsNext == null) {
            return false;
        } else {
            return whatsNext.equals(ADDEDLINE);
        }
    }

    private static Boolean isNextRemoved(int line, List<Integer> added, List<Integer> removed) {
        String whatsNext = whatsNext(line, added, removed);

        if (whatsNext == null) {
            return false;
        } else {
            return whatsNext.equals(REMOVEDLINE);
        }
    }
    private static String whatsNext(int line, List<Integer> added, List<Integer> removed) {
        int next = Integer.MAX_VALUE;
        String result = null;

        for (Integer a : added) {
            if (a > line && a < next) {
                next = a;
                result = ADDEDLINE;
            } else if (a >= next) {
                break;
            }
        }

        for (Integer r : removed) {
            if (r > line && r < next) {
                next = r;
                result = "REMOVEDLINE";
            } else if (r >= next) {
                break;
            }
        }

        //  for (Integer e : end) {
        //  if (e > line && e < next) {
        //     next = e;
        //     result = END;
        // } else if (e >= next) {
        //     break;
        // }
        // }
        return result;
    }

    private static Integer nextValue(int line, List<Integer> added, List<Integer> removed) {
        Integer result = Integer.MAX_VALUE;

        for (Integer a : added) {
            if (a > line && a < result) {
                result = a;
            } else if (a >= result) {
                break;
            }
        }

        for (Integer r : removed) {
            if (r > line && r < result) {
                result = r;
            } else if (r >= result) {
                break;
            }
        }

        //for (Integer e : end) {
        // if (e > line && e < result) {
        //    result = e;
        // } else if (e >= result) {
        //     break;
        // }
        // }
        return result;
    }

}
