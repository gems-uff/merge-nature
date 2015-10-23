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

    public AntBuilder(String pathProject, String antPath) {

        this.pathProject = pathProject;
        this.antPath = antPath + File.separator + "bin" + File.separator + "ant";

    }

    public boolean compile() {

        String command = getAntPath() + " compile";
        CMDOutput cmd = CMD.cmd(pathProject, command);

        System.out.println("Error:\n\n\n");
        for (String error : cmd.getErrors()) {
            System.out.println(error);
        }

        System.out.println("\n\n\n\nOutput:\n\n\n");

        for (String line : cmd.getOutput()) {
            System.out.println(line);
        }

        return true;
    }

    public boolean test() {
        String command = getAntPath() + " test";
        CMD.cmd(getPathProject(), command);

        return true;
    }

    public boolean task(String task) {
        String command = getAntPath() + " " + task;
        CMD.cmd(getPathProject(), command);

        return true;
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
        AntBuilder ant = new AntBuilder("/Users/gleiph/Dropbox/doutorado/repositories/lombok", "/Users/gleiph/Applications/apache-ant-1.9.6");
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
}
