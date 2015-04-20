/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.processing;

import br.uff.ic.gems.merge.utils.MergeUtils;
import br.uff.ic.gems.merge.vcs.GitCMD;
import br.uff.ic.github.mergeviewer.ShowFile;
import br.uff.ic.github.mergeviewer.util.Information;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 *
 * @author Gleiph
 */
public class Understanding implements Runnable {

    private JProgressBar jProgressBar;
    private JComboBox cbFiles;
    private String mergedRepository;
    private int chunks;

    public Understanding(JProgressBar jProgressBar, JComboBox cbFiles, String mergedRepository) {
        this.jProgressBar = jProgressBar;
        this.cbFiles = cbFiles;
        this.mergedRepository = mergedRepository;
    }

    @Override
    public void run() {

        jProgressBar.setIndeterminate(true);
        jProgressBar.setVisible(true);
        String file = cbFiles.getSelectedItem().toString();

        String head1Repository = mergedRepository + "clone" + File.separator + "1";
        String head2Repository = mergedRepository + "clone" + File.separator + "2";
        String originalRepository = mergedRepository + "clone" + File.separator + "Or";
        String developerMergeRepository = mergedRepository + "clone" + File.separator + "OK";

        String replaceFile = file.replace(mergedRepository, "");

        if (!new File(head1Repository).isDirectory()) {
            System.out.println("Cloning 1");
            GitCMD.clone(mergedRepository, mergedRepository, head1Repository);
        }
        if (!new File(head2Repository).isDirectory()) {
            System.out.println("Cloning 2");
            GitCMD.clone(mergedRepository, mergedRepository, head2Repository);
        }
        if (!new File(originalRepository).isDirectory()) {
            System.out.println("Cloning Original");
            GitCMD.clone(mergedRepository, mergedRepository, originalRepository);
        }
        if (!new File(developerMergeRepository).isDirectory()) {
            System.out.println("Cloning Merge");
            GitCMD.clone(mergedRepository, mergedRepository, developerMergeRepository);
        }

        String revision = Information.DEVELOPER_MERGE_REVISION;

        String mergeBase = Information.BASE_REVISION;
        String head1 = Information.LEFT_REVISION;
        String head2 = Information.RIGHT_REVISION;
        String developerMerge = Information.DEVELOPER_MERGE_REVISION;

        GitCMD.checkout(originalRepository, mergeBase);
        GitCMD.checkout(head1Repository, head1);
        GitCMD.checkout(head2Repository, head2);
        GitCMD.checkout(developerMergeRepository, developerMerge);

        jProgressBar.setIndeterminate(false);
        jProgressBar.setVisible(false);

        String originalFile = originalRepository + replaceFile;
        String head1File = head1Repository + replaceFile;
        String head2File = head2Repository + replaceFile;
        String mergedFile = mergedRepository + replaceFile;
        String developerMergeFile = developerMergeRepository + replaceFile;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        ShowFile originalFrame = new ShowFile(originalFile);
        originalFrame.setSize(new Dimension((int) width, (int) (height / 5)));
        originalFrame.setTitle("Original");
        originalFrame.setVisible(true);

        ShowFile head1Frame = new ShowFile(head1File);
        head1Frame.setSize(new Dimension((int) (width / 2), (int) (2 * height / 5)));
        head1Frame.setLocation(0, (int) (height / 5));
        head1Frame.setTitle("Head1");
        head1Frame.setVisible(true);

        ShowFile head2Frame = new ShowFile(head2File);
        head2Frame.setSize(new Dimension((int) (width / 2), (int) (2 * height / 5)));
        head2Frame.setLocation((int) (width / 2), (int) (height / 5));
        head2Frame.setTitle("Head2");
        head2Frame.setVisible(true);

        ShowFile mergeFrame = new ShowFile(mergedFile);
        mergeFrame.setTitle("Merge");
        mergeFrame.setLocation((int) (0), (int) (0));
        mergeFrame.setSize(new Dimension((int) (width / 2), (int) (height)));
//        mergeFrame.setLocation((int) (0), (int) (3 * height / 5));
//        mergeFrame.setSize(new Dimension((int) screenSize.getWidth(), (int) (screenSize.getHeight() / 5)));
        mergeFrame.setVisible(true);
        List<String> merged = MergeUtils.fileToLines(mergedFile);
        int conflicts = 0;
        for (String line : merged) {
            if (line.startsWith("<<<<<<<")) {
                conflicts++;
            }
        }

        ShowFile developerMergeFrame = new ShowFile(developerMergeFile);
        developerMergeFrame.setTitle("Developer merge");
        developerMergeFrame.setLocation((int) (width / 2), (int) (4 * height / 5));
        developerMergeFrame.setSize(new Dimension((int) width / 2, (int) (height)));
//        developerMergeFrame.setLocation((int) (0), (int) (4 * height / 5));
//        developerMergeFrame.setSize(new Dimension((int) screenSize.getWidth(), (int) (screenSize.getHeight() / 5)));
        developerMergeFrame.setVisible(true);

        JOptionPane.showMessageDialog(null, "There are " + conflicts + " conflicts.");
    }

}
