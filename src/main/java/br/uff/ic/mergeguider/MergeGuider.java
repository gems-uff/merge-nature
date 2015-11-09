/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
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

        if (isFailedMerge(projectPath, SHALeft, SHARight)) {

            List<String> conflictedFilePaths = Git.conflictedFiles(projectPath);

            //Getting conflicting chunks
            List<ConflictingChunkInformation> ccis = ConflictingChunkInformation.extractConflictingChunksInformation(conflictedFilePaths);
            
            printConflictingChunksInformation(ccis);
            
            
        }

    }

    public static void printConflictingChunksInformation(List<ConflictingChunkInformation> ccis) throws IOException {
        for (ConflictingChunkInformation cci : ccis) {
            List<String> fileLines = FileUtils.readLines(new File(cci.getFilePath()));
            
            List<String> ccLines = fileLines.subList(cci.getBegin(), cci.getEnd() + 1);
            
            System.out.println(cci.toString());
            for (String ccLine : ccLines) {
                System.out.println("\t\t" + ccLine);
            }
        }
    }

    public static boolean isFailedMerge(String projectPath, String SHALeft, String SHARight) {
        Git.reset(projectPath);
        Git.checkout(projectPath, SHALeft);
        List<String> merge = Git.merge(projectPath, SHARight, false, false);

        return MergeStatusAnalizer.isConflict(merge);
    }

}
