/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.merge.vcs;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class Git {
    
    private String repository;

    public Git(String repository) {
        this.repository = repository;
    }

    
    public List<String> fileDiff(String initialVersion, String finalVersion, String file){
        List<String> result = new ArrayList<>();
        String command = "git diff " + initialVersion+ " "+ finalVersion+ " "+ file;
        
        CMDOutput cmdOutput = CMD.cmd(getRepository(), command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }
    
}
