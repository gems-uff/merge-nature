/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.analyzers;

import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.states.MergeStatus;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.github.mergeviewer.util.OutputParser;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class RevisionAnalyzer {

    public static Revision analyze(String revisionSHA, String repositoryPath) {
        Revision revision = new Revision();

        revision.setSha(revisionSHA);

        GitCMD.reset(repositoryPath);
        List<String> parents = GitCMD.getParents(repositoryPath, revisionSHA);

        if (parents.size() == 2) {


            String leftParent = parents.get(0);
            String rightParent = parents.get(1);
            //Filling parents
            revision.setLeftSha(leftParent);
            revision.setRightSha(rightParent);

            String mergeBase = GitCMD.getMergeBase(repositoryPath, leftParent, rightParent);
            //Filling base revision
            revision.setBaseSha(mergeBase);

            GitCMD.checkout(repositoryPath, leftParent);

            List<String> mergeOutput = GitCMD.merge(repositoryPath, rightParent, false, true);

            if (OutputParser.isConflict(mergeOutput)) {
                
                revision.setStatus(MergeStatus.CONFLICTING);

                List<String> conflictedFiles = GitCMD.conflictedFiles(repositoryPath);
                revision.setNumberConflictingFiles(conflictedFiles.size());
                int javaFiles = 0;

                for (String conflictedFile : conflictedFiles) {
                    try {
                        ConflictingFile conflictingFile = ConflictingFileAnalyzer.analyze(conflictedFile, repositoryPath, leftParent, rightParent, revisionSHA);
                        revision.getConflictingFiles().add(conflictingFile);
                        
                        if (conflictingFile.isJava()) {
                            javaFiles++;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(RevisionAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Investigate!!!!!");
                    }
                }

                revision.setNumberJavaConflictingFiles(javaFiles);

            } else if (OutputParser.isFastForward(mergeOutput)) {
                revision.setStatus(MergeStatus.FAST_FORWARD);
            } else {
                revision.setStatus(MergeStatus.NON_CONFLICTING);
            }

        } else {
            System.out.println("Implement!!!!!");
        }

        return revision;
    }

}
