/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.builder;

import br.uff.ic.gems.merge.util.CMD;
import br.uff.ic.gems.merge.util.CMDOutput;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.SendingContext.RunTime;

/**
 *
 * @author gleiph
 */
public class Ant implements Builder {
    
    public static void main(String[] args) {
        String projectPath = "/Users/gleiph/Repositories/lombok/";
//        Ant ant  = new Ant(projectPath);
//        boolean compile = ant.compile();
        try {
//            Runtime.getRuntime().exec("ant compile", null, new File(projectPath));
            Runtime.getRuntime().exec("ant");
        } catch (IOException ex) {
            Logger.getLogger(Ant.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private String pathProject;

    public Ant(String pathProject) {
        this.pathProject = pathProject;
    }
    
    @Override
    public boolean compile() {

        String command = "ant compile";
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

    @Override
    public boolean test() {
        String command = "ant test";
        CMD.cmd(getPathProject(), command);

        return true;
    }

    @Override
    public boolean task(String task) {
        String command = "ant " + task;
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

}
