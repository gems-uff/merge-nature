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

    public int repositioningCluster(String initialFile, String finalFile, int line) {
        int result = line;

        Git git = new Git(repositoryPath);
        String diff = git.fileDiff(initialFile, finalFile);

        GitTranslator gitTranslator = new GitTranslator();
        List<Operation> operations = gitTranslator.translateDelta(diff);

        List<Operation> clusteredOperations = gitTranslator.cluster(operations);

        int displacementRemove = 0, displacementAdd = 0;

        for (Operation clusteredOperation : clusteredOperations) {

            System.out.println("\t" + clusteredOperation.toString());

            int initialLine = 0;

            int finalLine = 0;

            if (clusteredOperation.getType() == OperationType.ADD) {
                initialLine = clusteredOperation.getLine() + displacementRemove;

                if (initialLine <= result) {
                    result += clusteredOperation.getSize();
                    displacementAdd += clusteredOperation.getSize();
                }
            } else if (clusteredOperation.getType() == OperationType.REMOVE) {
                initialLine = clusteredOperation.getLine() + displacementRemove;
                finalLine = initialLine + clusteredOperation.getSize() - 1;

                if (finalLine < result) {
                    result -= clusteredOperation.getSize();
                    displacementRemove -= clusteredOperation.getSize();

                } else if (initialLine <= result
                        && result <= finalLine) {
                    return -1;
                }
            }
        }

        return result;
    }

    public int repositioning(String initialFile, String finalFile, int line) {
        int result = line;

        Git git = new Git(repositoryPath);
        String diff = git.fileDiff(initialFile, finalFile);

        GitTranslator gitTranslator = new GitTranslator();
        List<Operation> operations = gitTranslator.translateDelta(diff);

        int addDisplacement = 0, removeDisplacement = 0;

        for (Operation operation : operations) {

            System.out.println(operation.toString());

            if (operation.getLine() > line) {
                System.out.println(line - removeDisplacement + addDisplacement);
                return line - removeDisplacement + addDisplacement;
            }

            if (operation.getType() == OperationType.REMOVE) {
                if (operation.getLine() == line) {
                    return -1;
                } else {
                    System.out.println(operation.getLine() + " X ");
                    removeDisplacement++;
                }
            } else if (operation.getType() == OperationType.ADD) {
                System.out.println(" X " + operation.getLine());
                addDisplacement++;
            }
        }

        return result;
    }
}
