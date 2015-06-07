/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
import br.uff.ic.github.mergeviewer.util.Variables;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Gleiph
 */
public class InitProject implements Runnable {

    JProgressBar jProgressBar;
    JTable jTable;
    JMenu jMenu;
    private String repositoryPath;

    public InitProject(JProgressBar jProgressBar, JTable jTable, JMenu jMenu) {
        this.jProgressBar = jProgressBar;
        this.jTable = jTable;
        this.jMenu = jMenu;
    }

    @Override
    public void run() {

        jProgressBar.setIndeterminate(true);
        String status = "";
        DefaultTableModel model = new DefaultTableModel();
        int progress = 0;

        //Table
//        JTable table;
        jTable.setModel(model);
        model.addColumn("SHA");
        model.addColumn("Status");
        model.addColumn("Files");
        model.addColumn("Percentage");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(jMenu);
        File absoluteFile = fileChooser.getSelectedFile().getAbsoluteFile();

        jTable.setVisible(false);

        setRepositoryPath(absoluteFile.toString());
        List<String> mergeRevisions = Git.getMergeRevisions(getRepositoryPath());
        jProgressBar.setMinimum(1);
        jProgressBar.setMaximum(mergeRevisions.size());
        jProgressBar.setStringPainted(true);

        jProgressBar.setIndeterminate(false);

        int count = 0;

        for (String revision : mergeRevisions) {

            System.out.println((++count) + " Current revision: " + revision);
            List<String> parents = Git.getParents(repositoryPath, revision);
            Git.reset(repositoryPath);

            double javaPercentage = 0;
            double files = 0;
            if (parents.size() == 2) {
                Git.checkout(getRepositoryPath(), parents.get(0));
                List<String> merge = Git.merge(repositoryPath, parents.get(1), false, true);

                if (MergeStatusAnalizer.isConflict(merge)) {
                    status = Variables.CONFLICT;
                    List<String> conflictedFiles = Git.conflictedFiles(repositoryPath);

                    double javaFiles = 0;

                    files = conflictedFiles.size();

                    for (String file : conflictedFiles) {
                        if (file.toUpperCase().contains(".JAVA")) {
                            javaFiles++;
                        }
                    }

                    javaPercentage = javaFiles / files;

                } else if (MergeStatusAnalizer.isFastForward(merge)) {
                    status = Variables.FAST_FORWARD;
                } else {
                    status = Variables.NO_CONFLICT;
                }

                if (status.equals(Variables.CONFLICT)) {
                    model.insertRow(progress, new Object[]{revision, status, files, javaPercentage});
                } else {
                    model.insertRow(progress, new Object[]{revision, status});
                }

                jProgressBar.setValue(++progress);
            } else {
                System.out.println("Not implemented! Implement more than two parents!");
            }

        }

        jTable.setVisible(true);

    }

    /**
     * @return the repositoryPath
     */
    public String getRepositoryPath() {
        return repositoryPath;
    }

    /**
     * @param repositoryPath the repositoryPath to set
     */
    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

}
