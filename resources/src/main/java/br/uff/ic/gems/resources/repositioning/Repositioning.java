/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.repositioning;

import br.uff.ic.gems.resources.diff.translator.GitTranslator;
import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.gems.resources.operation.OperationType;
import br.uff.ic.gems.resources.vcs.Git;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class Repositioning {

    private final String repositoryPath;

    public Repositioning(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public int repositioning(String initialFile, String finalFile, int line) {
        int result = line;

        Git git = new Git(repositoryPath);
        String diff = git.fileDiff(initialFile, finalFile);

        GitTranslator gitTranslator = new GitTranslator();
        List<Operation> operations = gitTranslator.translateDelta(diff);

        int addDisplacement = 0, removeDisplacement = 0;

        for (Operation operation : operations) {

            if (operation.getLine() > line) {
                return line - removeDisplacement + addDisplacement;
            }

            if (operation.getType() == OperationType.REMOVE) {
                if (operation.getLine() == line) {
                    return -1;
                } else {
                    removeDisplacement++;
                }
            } else if (operation.getType() == OperationType.ADD) {
                addDisplacement++;
            }
        }

        return result - removeDisplacement + addDisplacement;
    }
}
