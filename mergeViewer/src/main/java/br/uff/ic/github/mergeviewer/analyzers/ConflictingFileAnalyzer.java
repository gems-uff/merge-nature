/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.analyzers;

import br.uff.ic.gems.merge.repositioning.Repositioning;
import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.ast.ASTAuxiliar;
import br.uff.ic.github.mergeviewer.kinds.ConflictingChunk;
import br.uff.ic.github.mergeviewer.kinds.ConflictingFile;
import br.uff.ic.github.mergeviewer.processing.ConflictingChunkInformation;
import br.uff.ic.github.mergeviewer.util.ConflictPartsExtractor;
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
        GitCMD.checkout(leftRepository, leftSHA);
        GitCMD.checkout(rightRepository, rightSHA);
        GitCMD.checkout(developerMergedRepository, developerSolutionSHA);

        //Paths to files
        String solutionPath = developerMergedRepository + relativePath;
        String conflictPath = repositoryPath + relativePath;
        
        List<String> conflictingContent = FileUtils.readLines(new File(conflictPath));
        List <String> solutionContent = FileUtils.readLines(new File(solutionPath));


        for (ConflictingChunk conflictingChunk : conflictingChunks) {

            //Getting conflict area
            int beginConflict, endConflict;
            beginConflict = ASTAuxiliar.getConflictLowerBound(conflictingChunk, context);
            endConflict = ASTAuxiliar.getConflictUpperBound(conflictingChunk, context, conflictingContent);
            
            //TODO: Store
            List<String> conflictingArea = conflictingContent.subList(beginConflict, endConflict);

            //Getting parts of conflict area
            ConflictPartsExtractor cpe = new ConflictPartsExtractor(conflictingArea);
            cpe.extract();

            String leftLanguageConstruct = null;
            String rightLanguageConstruct = null;

            
            
            if (conflictingFilePath.contains(".java")) {
                try {
                    leftLanguageConstruct = ASTAuxiliar.getSyntacticStructures(cpe.getLeftConflict(), leftRepository, relativePath);
                    rightLanguageConstruct = ASTAuxiliar.getSyntacticStructures(cpe.getRightConflict(), rightRepository, relativePath);
                } catch (IOException ex) {
                    Logger.getLogger(ConflictingChunkInformation.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                String[] fileBroken = relativePath.split("\\.");
                leftLanguageConstruct = fileBroken[fileBroken.length - 1];
                rightLanguageConstruct = fileBroken[fileBroken.length - 1];
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
            context1eOriginal = conflictingChunk.getBegin();
            context2bOriginal = conflictingChunk.getEnd() + 1;
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

            int context1 = checkContext1(context1bOriginal, context1eOriginal, repositioning, initialPath, finalPath, begin, separator, end);

            int context2 = checkContext2(solutionContent, conflictingContent, context2eOriginal, context2bOriginal, repositioning, initialPath, finalPath, separator, begin, end);

            List<String> solutionArea = solutionContent.subList(context1 - 1, context2);

            String dd = DeveloperDecision.getDeveloperDecision(cpe, solutionArea, context).toString();

            if (hasSolution) {
                //SetSolution
                DeveloperChoice developerDecision = DeveloperDecision.getDeveloperDecision(cpe, solutionArea, context);
                conflictingChunk.setDeveloperChoice(developerDecision);
            } else {
                conflictingChunk.setDeveloperChoice(DeveloperChoice.MANUAL);
            }

            System.out.println("=================" + conflictingChunk.getIdentifier() + "=================");
            System.out.println("=================Conflicting area=================");
            for (String ca : conflictingArea) {
                System.out.println(ca);
            }
            System.out.println("=================Solution area=================");
            for (String sa : solutionArea) {
                System.out.println(sa);
            }
            System.out.println("=================Developers` decision=================");
            System.out.println(conflictingChunk.getDeveloperChoice().toString());
            System.out.println("=================Left language constructs=================");
            System.out.println(leftLanguageConstruct);
            System.out.println("=================Right language constructs=================");
            System.out.println(rightLanguageConstruct);

        }

        conflictingFile.setConflictingChunks(conflictingChunks);

        return conflictingFile;
    }

    public static int checkContext2(List<String> solutionContent, List<String> conflictingContent, int context2eOriginal, int context2bOriginal, Repositioning repositioning, String initialPath, String finalPath, int separator, int begin, int end) {
        //Context 2
        int context2 = solutionContent.size() + 1;
        int context2Original = conflictingContent.size();
        boolean changed = false;
        for (int i = context2eOriginal; i >= context2bOriginal; i--) {
            context2 = repositioning.repositioningCluster(initialPath, finalPath, i);
            
            if (context2 != -1) {
                context2Original = i;
                changed = true;
                break;
            }
        }
        if (context2 == solutionContent.size() && !changed) {
            
            int c2a0 = -1, c2b0 = -1;
            int c2a = -1, c2b = -1;
            
            for (int i = separator - 1; i > begin; i--) {
                c2a = repositioning.repositioningCluster(initialPath, finalPath, i);
                
                if (c2a != -1) {
                    c2a0 = i;
                    break;
                }
            }
            
            for (int i = end - 1; i > separator; i--) {
                c2b = repositioning.repositioningCluster(initialPath, finalPath, i);
                
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
        //Treating exceptions
        if (context2 > solutionContent.size() || context2 < 1) {
            context2 = solutionContent.size();
        }
        return context2;
    }
    
    public static int checkContext1(int context1bOriginal, int context1eOriginal, Repositioning repositioning, String initialPath, String finalPath, int begin, int separator, int end) {
        int context1 = -1, context1Original = -1;
        boolean changed = false;
        for (int i = context1bOriginal; i <= context1eOriginal; i++) {
            context1 = repositioning.repositioning(initialPath, finalPath, i);
            
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
                c1a = repositioning.repositioningCluster(initialPath, finalPath, i);
                
                if (c1a != -1) {
                    c1a0 = i;
                    break;
                }
            }
            
            for (int i = separator + 1; i < end - 1; i++) {
                c1b = repositioning.repositioningCluster(initialPath, finalPath, i);
                
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
        //Treating exceptions
            if (context1 < 1) {
                context1 = 1;
            }
            return context1;
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
