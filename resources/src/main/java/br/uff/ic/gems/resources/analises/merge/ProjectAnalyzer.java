/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ProjectAnalyzer {

    public Project analyze(String repositoryPath) {

        Project project = new Project();
        project.setRepositoryPath(repositoryPath);
        List<String> allRevisions = Git.getMergeRevisions(repositoryPath);
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
