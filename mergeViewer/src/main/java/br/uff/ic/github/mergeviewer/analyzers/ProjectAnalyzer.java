/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.analyzers;

import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.kinds.Project;
import br.uff.ic.github.mergeviewer.kinds.Revision;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ProjectAnalyzer {

    public static void main(String[] args) {
        Date begin, end;
        begin = new Date();
        ProjectAnalyzer pa = new ProjectAnalyzer();
        pa.analyze("/Users/gleiph/Repositories/antlr4");
//        pa.analyze("/Users/gleiph/Repositories/voldemort");
//        pa.analyze("/Users/gleiph/Repositories/mct");
//        pa.analyze("/Users/gleiph/Repositories/lombok");
//        pa.analyze("/Users/gleiph/Repositories/twitter4j");

        end = new Date();
        
        System.out.println("Begin: "+ begin);
        System.out.println("End: "+end);
    }

    public Project analyze(String repositoryPath) {

        Project project = new Project();
        project.setRepositoryPath(repositoryPath);
        List<String> allRevisions = GitCMD.getMergeRevisions(repositoryPath);
        project.setRepositoryPath(repositoryPath);

        String[] split = repositoryPath.split(File.separator);
        project.setName(split[split.length - 1]);

        List<Revision> revisions = new ArrayList<>();

        project.setNumberRevisions(revisions.size());

        int conflictingMerges = 0;
        int progress = 1;

        for (String rev : allRevisions) {

            System.out.println((progress++) + "//" + allRevisions.size() + ": " + rev);
            Revision revision = RevisionAnalyzer.analyze(rev, repositoryPath);

            if (revision.isConflict()) {
                conflictingMerges++;
            }

            revisions.add(revision);
        }

        project.setRevisions(revisions);
        project.setNumberConflictingMerges(conflictingMerges);

        return project;
    }

}
