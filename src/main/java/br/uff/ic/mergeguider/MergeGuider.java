/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.gems.resources.analises.merge.ConflictingFileAnalyzer;
import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class MergeGuider {

    public static void main(String[] args) throws IOException {
        String projectPath = "/Users/gleiph/Dropbox/doutorado/repositories/lombok";
        String SHALeft = "e557413";
        String SHARight = "fbab1ca";
        int contextSize = 3;

        
        Git.reset(projectPath);
        Git.checkout(projectPath, SHALeft);
        List<String> merge = Git.merge(projectPath, SHARight, false, false);

        if (MergeStatusAnalizer.isConflict(merge)) {

            List<String> conflictedFilePaths = Git.conflictedFiles(projectPath);

            for (String conflictedFilePath : conflictedFilePaths) {

                System.out.println("--------------------------------------------------------------------------");
                System.out.println(conflictedFilePath);
                System.out.println("--------------------------------------------------------------------------");
            
                List<String> conflictedFileContent = FileUtils.readLines(new File(conflictedFilePath));
                
                List<ConflictingChunk> conflictingChunks = ConflictingFileAnalyzer.getConflictingChunks(conflictedFileContent);

                for (ConflictingChunk conflictingChunk : conflictingChunks) {
                    
                    int beginContext = conflictingChunk.getBeginLine() - contextSize;
                    if (beginContext < 0) {
                        beginContext = 0;
                    }

                    int endContext = conflictingChunk.getEndLine() + contextSize;
                    if (endContext > conflictedFileContent.size()) {
                        endContext = conflictedFileContent.size();
                    }

                    List<String> conflictingContent = conflictedFileContent.subList(beginContext, endContext);
                    conflictingChunk.setConflictingContent(conflictingContent);

                    System.out.println(conflictingChunk.toString());
                    System.out.println("");
                }
            
            }
        }

    }

}
