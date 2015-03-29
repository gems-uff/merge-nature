/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.merge.repositioning;

import br.uff.ic.gems.merge.diff.translator.GitTranslator;
import br.uff.ic.gems.merge.operation.Operation;
import br.uff.ic.gems.merge.operation.OperationType;
import br.uff.ic.gems.merge.vcs.Git;
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

    public int repositioning(String initialVersion, String finalVersion, String pathFile, int line) {
        int result = line;

        Git git = new Git(repositoryPath);
        String diff = git.fileDiff(initialVersion, finalVersion, pathFile);

        GitTranslator gitTranslator = new GitTranslator();
        List<Operation> operations = gitTranslator.translateDelta(diff);

        List<Operation> clusteredOperations = gitTranslator.cluster(operations);

        int displacement = 0;

        for (Operation clusteredOperation : clusteredOperations) {

            int initialLine = clusteredOperation.getLine() + displacement;
            int finalLine = initialLine + clusteredOperation.getSize() - 1;
            
            if (clusteredOperation.getType() == OperationType.ADD) {
                if (initialLine <= result) {
                    result += clusteredOperation.getSize();
                    displacement += clusteredOperation.getSize();
                }
            } else if (clusteredOperation.getType() == OperationType.REMOVE) {
                if (finalLine < result) {
                    result -= clusteredOperation.getSize();
                    displacement -= clusteredOperation.getSize();

                } else if (initialLine <= result
                        && result <= finalLine) {
                    return -1;
                }
            }
        }

        return result;
    }

}
