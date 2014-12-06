/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.util.ConflictingChunk;
import java.io.File;
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

}
