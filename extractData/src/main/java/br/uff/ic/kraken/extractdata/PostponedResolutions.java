/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata;

import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class PostponedResolutions {

    public static void main(String[] args) throws ParseException {

        int daysRange = 7;

        List<String> projects = new ArrayList<>();

        projects.add("/Users/gleiph/repositories/antlr4");
        projects.add("/Users/gleiph/repositories/lombok");
        projects.add("/Users/gleiph/repositories/mct");
        projects.add("/Users/gleiph/repositories/twitter4j");
        projects.add("/Users/gleiph/repositories/voldemort");

        for (String project : projects) {
            projectAnalysis(project, daysRange);
        }

    }

    public static void projectAnalysis(String repository, int daysRange) throws ParseException {

        if (!repository.endsWith(File.separator)) {
            repository += File.separator;
        }

        System.out.println(repository);

        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss z");
        List<String> mergeRevisions = Git.getMergeRevisions(repository, true);

        System.out.println("Merges: " + mergeRevisions.size());

        int filesCochanged = 0, mergesWithCochanges = 0, merges = 0;

        for (String mergeRevision : mergeRevisions) {

            filesCochanged = 0;

            List<String> parents = Git.getParents(repository, mergeRevision);
            if (parents.size() != 2) {
                continue;
            }

            Git.reset(repository);
            Git.clean(repository);
            Git.checkout(repository, parents.get(0));
            List<String> mergeOutput = Git.merge(repository, parents.get(1), false, false);

            if (MergeStatusAnalizer.isConflict(mergeOutput)) {

                merges++;

                List<String> conflictedFiles = Git.conflictedFiles(repository);

                for (String conflictedFile : conflictedFiles) {
                    if (conflictedFile.startsWith(repository)) {
                        conflictedFiles.set(conflictedFiles.indexOf(conflictedFile), conflictedFile.replaceFirst(repository, ""));
                    }
                }

                System.out.println("Merge " + mergeRevision);
//                for (String conflictedFile : conflictedFiles) {
//                    System.out.println("\t\t" + conflictedFile);
//                }

                String date = Git.getDateISO(repository, mergeRevision);
                Date mergeDate = formater.parse(date);

                Calendar aux = Calendar.getInstance();
                aux.setTime(mergeDate);
                aux.add(Calendar.DATE, daysRange);
                Date endWindow = aux.getTime();

                //Getting commits in the range of days
                List<String> commitsRange = Git.logByDays(repository, mergeDate, endWindow);

//                commitsRange = untilNextMerge(commitsRange, mergeRevisions, repository);

                commitsRange  = commitsFreeMerges(repository, commitsRange, mergeRevisions);
                
                for (String commit : commitsRange) {
//                    System.out.println("\tChanged files in " + commit);
                    List<String> changedFiles = Git.getChangedFiles(repository, commit);

//                    for (String changedFile : changedFiles) {
//                        System.out.println("\t\t" + changedFile);
//                    }
                    changedFiles.retainAll(conflictedFiles);

                    if (!changedFiles.isEmpty()) {
//                        System.out.println("Same file edition!");
                        filesCochanged += changedFiles.size();
                    }

                }

                if (filesCochanged > 0) {
                    mergesWithCochanges++;
                }

                System.out.println("Files co-changed: " + filesCochanged);

            }
        }
        System.out.println("Merge with co changes: " + mergesWithCochanges + "(" + merges + ")");
    }

    public static List<String> untilNextMerge(List<String> commits, List<String> merges, String repositoryPath) {
        List<String> result = new ArrayList<>();

        int indexOf = merges.indexOf(commits.get(0));
        String baseMerge = commits.get(0);

        List<String> way = null;
        for (int i = indexOf + 1; i < merges.size(); i++) {
            way = new ArrayList<>();

            String currentMerge = merges.get(i);

            boolean hasWay = hasWay(repositoryPath, baseMerge, currentMerge, commits, way);

            if (hasWay) {

                for (String commit : way) {
                    System.out.println(commit);

                }

                return way;
            }

        }

        //Caso em que os commits não chegam a um merge
        //Fazer a mesma coisa para os commits 
        return result;
    }

    public static boolean hasWay(String repositoryPath, String shaSource, String shaTarget, List<String> commits, List<String> way) {

        List<String> parents = Git.getParents(repositoryPath, shaTarget);

        for (String parent : parents) {
            if (parent.equals(shaSource)) {
                return true;
            } else if (commits.contains(parent)) {
                boolean hasWay = hasWay(repositoryPath, shaSource, parent, commits, way);
                if (hasWay) {
                    way.add(0, parent);
                    return true;
                }
            }

        }

        return false;
    }

    public static boolean hasWayWithoutMerge(String repositoryPath, String shaSource, String shaTarget, 
            List<String> commits, List<String> merges) {

        List<String> parents = Git.getParents(repositoryPath, shaTarget);

        for (String parent : parents) {
            if(merges.contains(parent) && !parent.equals(shaSource)){
            }
            else if (parent.equals(shaSource)) {
                return true;
            } else if (commits.contains(parent)) {
                boolean hasWay = hasWayWithoutMerge(repositoryPath, shaSource, parent, commits, merges);
                if (hasWay) {
                    return true;
                }
            }

        }

        return false;
    }
    
    //Filter the commits that has a way to the source merge and does not have merges in the way
    public static List<String> commitsFreeMerges(String repositoryPath, List<String> commits, List<String> merges){
        List<String> result = new ArrayList<>();
        
        if(commits == null || commits.isEmpty())
            return null;
        
        String sourceCommit = commits.get(0);
        
//        List<String> commitsFree = new ArrayList<>(commits.size());
        
//        Collections.copy(commitsFree, commits);
//        commits.remove(sourceCommit);
        
        for (int i = 1; i < commits.size(); i++) {
            String currentCommit = commits.get(i);
            
            boolean hasWay = hasWayWithoutMerge(repositoryPath, sourceCommit, currentCommit, commits, merges);
                    
            
            if(hasWay && !merges.contains(currentCommit))
                result.add(currentCommit);
            
        }
        
        
        return result;
    }
    
    

}
