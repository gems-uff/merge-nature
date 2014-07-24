/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.Main;
import br.uff.ic.github.mergeviewer.util.OutputParser;
import br.uff.ic.github.mergeviewer.util.Variables;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(jMenu);
        File absoluteFile = fileChooser.getSelectedFile().getAbsoluteFile();

        jTable.setVisible(false);
        
        setRepositoryPath(absoluteFile.toString());
        List<String> mergeRevisions = GitCMD.getMergeRevisions(getRepositoryPath());
        jProgressBar.setMinimum(1);
        jProgressBar.setMaximum(mergeRevisions.size());
        jProgressBar.setStringPainted(true);

        jProgressBar.setIndeterminate(false);

        for (String revision : mergeRevisions) {

            List<String> parents = GitCMD.getParents(getRepositoryPath(), revision);
            GitCMD.reset(getRepositoryPath());

            if (parents.size() == 2) {
                GitCMD.checkout(getRepositoryPath(), parents.get(0));
                List<String> merge = GitCMD.merge(repositoryPath, parents.get(1), false, true);

                if (OutputParser.isConflict(merge)) {
                    status = Variables.CONFLICT;
                } else if (OutputParser.isFastForward(merge)) {
                    status = Variables.FAST_FORWARD;
                } else {
                    status = Variables.NO_CONFLICT;
                }
            }

            model.insertRow(progress, new Object[]{revision, status});
            jProgressBar.setValue(++progress);

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

//        jProgressBar.setVisible(false);
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
