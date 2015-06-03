/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.gems.resources.utils.Information;
import br.uff.ic.github.mergeviewer.util.Variables;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author Gleiph
 */
public class ProcessRevision implements Runnable {

    JTextArea jTextArea;
    JProgressBar jProgressBar;
    JComboBox jComboBox;
    String revision;
    String repositoryPath;
    String status;

    public ProcessRevision(JTextArea jTextArea, JProgressBar jProgressBar, JComboBox jComboBox, String revision, String repositoryPath, String status) {
        this.jTextArea = jTextArea;
        this.jProgressBar = jProgressBar;
        this.jComboBox = jComboBox;
        this.revision = revision;
        this.repositoryPath = repositoryPath;
        this.status = status;
    }

    @Override
    public void run() {

        jComboBox.removeAllItems();

        jProgressBar.setVisible(true);
        jProgressBar.setIndeterminate(true);
        List<String> parents = GitCMD.getParents(repositoryPath, revision);
        StringBuilder output = new StringBuilder();

        output.append("Version:").append("\n");
        output.append("\t").append(revision).append("\n");
        output.append("Parents:").append("\n");

        for (String parent : parents) {
            output.append("\t").append(parent).append("\n");
        }

        if (status.equals(Variables.CONFLICT) && parents.size() == 2) {

            Information.LEFT_REVISION = parents.get(0);
            Information.RIGHT_REVISION = parents.get(1);
            //Merge base
            String mergeBase = GitCMD.getMergeBase(repositoryPath, parents.get(0), parents.get(1));
            output.append("Merge base:").append("\n");
            output.append("\t").append(mergeBase).append("\n");
            Information.BASE_REVISION = mergeBase;
            
            
            //Getting information about the merge
            GitCMD.reset(repositoryPath);
            GitCMD.checkout(repositoryPath, parents.get(0));
            GitCMD.merge(repositoryPath, parents.get(1), false, true);
            List<String> conflictedFiles = GitCMD.conflictedFiles(repositoryPath);
            output.append("Conflicting files:").append("\n");
            for (String conflictedFile : conflictedFiles) {
                output.append("\t").append(conflictedFile).append("\n");
                jComboBox.addItem(conflictedFile);
            }
        }

        jTextArea.setText(output.toString());
        jProgressBar.setIndeterminate(false);
//        jProgressBar.setVisible(false);

    }

}
