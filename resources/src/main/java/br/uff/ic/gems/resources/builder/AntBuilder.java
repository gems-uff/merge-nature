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
public class AntBuilder {

    private String pathProject;
    private String antPath;
    private boolean showProgress;

    public AntBuilder(String pathProject, String antPath, boolean showProgress) {

        this.pathProject = pathProject;
        this.antPath = antPath + File.separator + "bin" + File.separator + "ant";
        this.showProgress = showProgress;

    }

    public boolean compile() {

        String command = getAntPath() + " compile";
        CMDOutput cmd = CMD.cmd(getPathProject(), command, showProgress);

        return !(cmd.getErrors() != null && !cmd.getErrors().isEmpty());
    }

    public boolean test() {
        String command = getAntPath() + " test";
        CMDOutput cmd = CMD.cmd(getPathProject(), command, showProgress);

        return !(cmd.getErrors() != null && !cmd.getErrors().isEmpty());
    }

    public boolean task(String task) {
        String command = getAntPath() + " " + task;
        CMDOutput cmd = CMD.cmd(getPathProject(), command, showProgress);

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

    public static void main(String[] args) {
        AntBuilder ant = new AntBuilder("/Users/gleiph/Dropbox/doutorado/repositories/lombok", "/Users/gleiph/Applications/apache-ant-1.9.6", true);
        ant.compile();
    }

    /**
     * @return the antPath
     */
    public String getAntPath() {
        return antPath;
    }

    /**
     * @param antPath the antPath to set
     */
    public void setAntPath(String antPath) {
        this.antPath = antPath;
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
