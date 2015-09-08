/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.analises.merge;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import br.uff.ic.gems.resources.data.dao.RevisionDAO;
import br.uff.ic.gems.resources.utils.FileManager;
import br.uff.ic.gems.resources.vcs.Git;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class ProjectAnalyzer {

    @Deprecated
    public Project analyze(String repositoryPath) {

        Project project = new Project();
        ProjectDAO projectDAO = new ProjectDAO();
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

        try {
            projectDAO.save(project);
        } catch (Exception ex) {
            Logger.getLogger(ProjectAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return project;
    }

    public Project analyze(Project project, boolean persiste, String outputProjectDirectory) {

        System.out.println("Getting merge information...");
        String repositoryPath = project.getRepositoryPath();

        RevisionDAO revisionDAO = new RevisionDAO();

        ProjectDAO projectDAO = new ProjectDAO();

        project.setRepositoryPath(repositoryPath);
        List<String> allRevisions = Git.getAllRevisions(repositoryPath);
        List<String> allMergeRevisions = Git.getMergeRevisions(repositoryPath);
        project.setRepositoryPath(repositoryPath);

        String[] split = repositoryPath.split(File.separator);
        project.setName(split[split.length - 1]);

        List<Revision> revisions = new ArrayList<>();

        int conflictingMerges = 0;
        int progress = 1;

        String pathSHA;
        if (!outputProjectDirectory.endsWith(File.separator)) {
            outputProjectDirectory += File.separator;
        }

        pathSHA = outputProjectDirectory + "sha";

        File SHAFile = new File(pathSHA);

        List<String> shaList = null;

        if (SHAFile.isFile()) {
            try {
                shaList = FileUtils.readLines(SHAFile);
            } catch (IOException ex) {
                Logger.getLogger(ProjectAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Saving merge sequence for future execution
        File mergeSequence  = new File(outputProjectDirectory, "sequence");
        
        if(!mergeSequence.isFile()){
            Writer writer = FileManager.createWriter(mergeSequence.getAbsolutePath());
            for (String allMergeRevision : allMergeRevisions) {
                FileManager.write(allMergeRevision+"\n", writer);
            }
            FileManager.closeWriter(writer);
        } else{
            try {
                allMergeRevisions = FileUtils.readLines(mergeSequence);
            } catch (IOException ex) {
                Logger.getLogger(ProjectAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        //Analysis taking place
        for (String rev : allMergeRevisions) {

            progress = allMergeRevisions.indexOf(rev) + 1;
            
            if (shaList != null && shaList.contains(rev) && !persiste) {
                String jsonPath = outputProjectDirectory + (progress / 1000) + File.separator + rev;

                Gson gson = new Gson();
                Revision revision = AutomaticAnalysis.readRevision(jsonPath);

                if (revision.isConflict()) {
                    conflictingMerges++;
                }

                System.out.println("Skiping >>>> " + (progress++) + "//" + allMergeRevisions.size() + ": " + rev);

            } else {
                Revision revision = RevisionAnalyzer.analyze(rev, repositoryPath);

                if (revision.isConflict()) {
                    conflictingMerges++;
                }

                try {
                    if (persiste) {
                        revision = revisionDAO.save(revision);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ProjectAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (persiste) {
                    //In case of persistence in the database
                    revisions.add(revision);
                } else {
                    //Persisting the data using JSon files
                    saveRevision(outputProjectDirectory, project.getName(), progress, revision);

                }
                System.out.println((progress++) + "//" + allMergeRevisions.size() + ": " + rev);
            }
        }

        project.setRevisions(revisions);
        project.setNumberConflictingMerges(conflictingMerges);
        project.setNumberRevisions(allRevisions.size());
        project.setNumberMergeRevisions(allMergeRevisions.size());

        try {
            //Case we are persiting the data in the database
            if (persiste) {
                if (project.getId() == null) {
                    projectDAO.save(project);
                } else {
                    projectDAO.saveGithub(project);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ProjectAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return project;
    }

    public void saveRevision(String outputProjectDirectory, String name, int revisionNumber, Revision revision) {

        Gson gson = new Gson();

        String content = gson.toJson(revision);

        int sizeDirectory = 1000;

        if (!outputProjectDirectory.endsWith(File.separator)) {
            outputProjectDirectory += outputProjectDirectory + File.separator;
        }

        String path = outputProjectDirectory;

        int directoryNumber = revisionNumber / sizeDirectory;

        path += directoryNumber;

        //Creating director, if it sdoes not exist 
        File directory = new File(path);
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        path += File.separator + revision.getSha();

        //Saving revision
        Writer writer = FileManager.createWriter(path);
        FileManager.write(content, writer);
        FileManager.closeWriter(writer);

        //Saving SHA
        writer = FileManager.createWriter(outputProjectDirectory + "sha", true);
        FileManager.write(revision.getSha() + "\n", writer);
        FileManager.closeWriter(writer);
    }
}
