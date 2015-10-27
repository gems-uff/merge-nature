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
public class AntBuilder implements Builder {

    private String pathProject;
    private String antHome;
    private boolean showProgress;

    public AntBuilder(String pathProject, String antHome, boolean showProgress) {

        this.pathProject = pathProject;
        this.antHome = antHome + File.separator + "bin" + File.separator + "ant";
        this.showProgress = showProgress;

    }

    @Override
    public boolean compile() {

        return task("compile");
    }

    @Override
    public boolean test() {

        return task("test");
    }

    @Override
    public boolean task(String task) {
        String command = getAntHome() + " " + task;
        CMDOutput cmd = CMD.cmd(getPathProject(), command, showProgress);

        for (String output : cmd.getOutput()) {
            if (output.contains("BUILD FAILURE")) {
                return false;
            }
        }

        return !(cmd.getErrors() != null && !cmd.getErrors().isEmpty());
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

//    public static void main(String[] args) {
//        AntBuilder ant = new AntBuilder("/Users/gleiph/Dropbox/doutorado/repositories/lombok", "/Users/gleiph/Applications/apache-ant-1.9.6", true);
//        ant.compile();
//    }
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

    /**
     * @return the antHome
     */
    public String getAntHome() {
        return antHome;
    }

    /**
     * @param antHome the antHome to set
     */
    public void setAntHome(String antHome) {
        this.antHome = antHome;
    }

}
