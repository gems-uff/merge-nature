/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import br.uff.ic.gems.merge.utils.MergeUtils;
import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.util.ConflictingChunk;
import br.uff.ic.github.mergeviewer.util.ContextFinder;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class StructureAnalyses {
    
    public static void cloneRepositories(String sourceRepository, String repositorySolutionPath, String head1Repository, String head2Repository) {
        if (!new File(repositorySolutionPath).isDirectory()) {
            System.out.println("Cloning Merge");
            GitCMD.clone(sourceRepository, sourceRepository, repositorySolutionPath);
        }
        
        if (!new File(head1Repository).isDirectory()) {
            System.out.println("Cloning 1");
            GitCMD.clone(sourceRepository, sourceRepository, head1Repository);
        }
        if (!new File(head2Repository).isDirectory()) {
            System.out.println("Cloning 2");
            GitCMD.clone(sourceRepository, sourceRepository, head2Repository);
        }
    }
    
    public static int getConflictUpperBound(ConflictingChunk conflictArea, int context, List<String> fileConflict) {
        int conflictingUpperBound;
        conflictingUpperBound = conflictArea.getEnd() + context;
        if (conflictingUpperBound > fileConflict.size()) {
            conflictingUpperBound = fileConflict.size();
        }
        return conflictingUpperBound;
    }

    public static int getConflictLowerBound(ConflictingChunk conflictArea, int context) {
        int conflictLowerBound;
        conflictLowerBound = conflictArea.getBegin() - context;
        if (conflictLowerBound < 0) {
            conflictLowerBound = 0;
        }
        return conflictLowerBound;
    }
    
    public static String getSyntacticStructures(List<String> conflictArea, String head1Repository, String relativePath) throws IOException {
        //Left side
        List<String> conflictLeft = conflictArea;
        String file = head1Repository + relativePath;
        List<String> leftFileList = MergeUtils.fileToLines(file);
        List<Integer> beginList = ContextFinder.getBegin(conflictLeft, leftFileList);
        int begin = 0;
        if (beginList.size() == 1) {
            begin = beginList.get(0) + 1;//transform in the real line (begining from 1)
        }
        int end = begin + conflictLeft.size() - 1;
        //Dealing with AST
        ASTExtractor ats = new ASTExtractor(file);
        ats.parser();
        List<String> kindConflict = ats.getStructures(begin, end);
        String leftSS = ats.toString(kindConflict);
        return leftSS;
    }

}
