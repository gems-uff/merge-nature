/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.builder;

import br.uff.ic.gems.resources.cmd.CMD;
import br.uff.ic.gems.resources.cmd.CMDOutput;
import java.io.File;

/**
 *
 * @author gleiph
 */
public class MavenBuilder implements Builder {

    private String pathProject;
    private String mvnHome;
    private boolean showProgress;

    public MavenBuilder(String pathProject, String mvnHome, boolean showProgress) {
        this.pathProject = pathProject;
        this.mvnHome = mvnHome + File.separator + "bin" + File.separator + "mvn";
        this.showProgress = showProgress;
    }

    @Override
    public boolean compile() {
        return task("compile");
    }

    @Override
    public boolean task(String task) {
        String command = getMvnHome() + " " + task;
        CMDOutput cmd = CMD.cmd(getPathProject(), command, showProgress);

        for (String output : cmd.getOutput()) {
            if (output.contains("BUILD FAILURE")) {
                return false;
            }
        }

        return !(cmd.getErrors() != null && !cmd.getErrors().isEmpty());
    }

    @Override
    public boolean test() {
        return task("test");

    }

    /**
     * @return the pathProject
     */
    public String getPathProject() {
        return pathProject;
    }

    /**
     * @param pathProject the pathProject to set
     */
    public void setPathProject(String pathProject) {
        this.pathProject = pathProject;
    }

    /**
     * @return the mvnHome
     */
    public String getMvnHome() {
        return mvnHome;
    }

    /**
     * @param mvnHome the mvnHome to set
     */
    public void setMvnHome(String mvnHome) {
        this.mvnHome = mvnHome;
    }

    /**
     * @return the showProgress
     */
    public boolean isShowProgress() {
        return showProgress;
    }

    /**
     * @param showProgress the showProgress to set
     */
    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

}
