/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
import br.uff.ic.mergeguider.datastructure.CCDependency;
import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.Dependencies;
import br.uff.ic.mergeguider.languageConstructs.Location;
import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodInvocation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.IMethodBinding;

/**
 *
 * @author gleiph
 */
public class MergeGuider {

    public static void main(String[] args) {
//        String projectPath = "/Users/gleiph/repositories/icse/antlr4";
        String projectPath = "/Users/gleiph/repositories/icse/lombok";
        //        String projectPath = "/Users/gleiph/repositories/icse/mct";
        //        String projectPath = "/Users/gleiph/repositories/icse/twitter4j";
//        String projectPath = "/Users/gleiph/repositories/icse/voldemort";

        String sandbox = "/Users/gleiph/repositories/icse";

        List<String> mergeRevisions = Git.getMergeRevisions(projectPath);

        for (String mergeRevision : mergeRevisions) {

            List<String> parents = Git.getParents(projectPath, mergeRevision);

            if (parents.size() == 2) {
                String SHALeft = parents.get(0);
                String SHARight = parents.get(1);

                System.out.println("Merging revisions " + SHALeft + " and " + SHARight);

                List<CCDependency> performMerge;
                try {
                    performMerge = performMerge(projectPath, SHALeft, SHARight, sandbox);
                    if (performMerge == null || performMerge.isEmpty()) {
                        System.out.println("Merge between revisions " + SHALeft + " and " + SHARight + " has not dependencies.");
                    } else {
                        System.out.println("Merge between revisions " + SHALeft + " and " + SHARight + " has dependencies.");
                    }
                } catch (IOException ex) {
                    System.out.println("Merge between revisions " + SHALeft + " and " + SHARight + " was not performed.");

                }

            }
        }
//        String SHALeft = "e557413";
//        String SHARight = "fbab1ca";
    }

    public static List<CCDependency> performMerge(String projectPath, String SHALeft, String SHARight, String sandbox) throws IOException {
        if (isFailedMerge(projectPath, SHALeft, SHARight)) {

            List<String> conflictedFilePaths = Git.conflictedFiles(projectPath);

            //Getting conflicting chunks
            List<ConflictingChunkInformation> ccis = ConflictingChunkInformation.extractConflictingChunksInformation(conflictedFilePaths);

            //Extracting Left AST
            String repositoryLeft = sandbox + File.separator + "left";

            clone(projectPath, repositoryLeft);
            Git.reset(repositoryLeft);
            Git.clean(repositoryLeft);
            Git.checkout(repositoryLeft, SHALeft);

            List<ClassLanguageContructs> ASTLeft = extractAST(repositoryLeft);

            //Extracting Right AST
            String repositoryRight = sandbox + File.separator + "right";

            clone(projectPath, repositoryRight);
            Git.reset(repositoryRight);
            Git.clean(repositoryRight);
            Git.checkout(repositoryRight, SHARight);

            List<ClassLanguageContructs> ASTRight = extractAST(repositoryRight);

            repositioningConflictingChunksInformation(ccis, repositoryLeft, projectPath, repositoryRight);

            //Creating depedency matrix
            int order = ccis.size();
            int[][] dependencyMatrix = instanciatingDepedencyMatrix(order);

            fillingDepedencyMatrix(ccis, projectPath,
                    ASTLeft, ASTRight, dependencyMatrix);

            printDepedencyMatrix(dependencyMatrix);

            List<CCDependency> extractDependencies = extractDependencies(dependencyMatrix);

            printDependencies(dependencyMatrix);

            return extractDependencies;
        }

        return null;
    }

    public static void repositioningConflictingChunksInformation(List<ConflictingChunkInformation> ccis, String repositoryLeft, String projectPath, String repositoryRight) {
        //Repositioning conflicting chunk information to both sides
        for (ConflictingChunkInformation cci : ccis) {

            String baseFilePath = cci.getFilePath();
            String leftFilePath = null;

            if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
                leftFilePath = repositoryLeft + File.separator
                        + cci.getRelativePathLeft();
            } else {
                leftFilePath = repositoryLeft + File.separator
                        + baseFilePath.replace(projectPath, "");
            }

            String rightFilePath = null;

            if (cci.isRenamed() && cci.getRelativePathRight() != null) {
                rightFilePath = repositoryRight + File.separator + cci.getRelativePathRight();
            } else {
                rightFilePath = repositoryRight + File.separator
                        + baseFilePath.replace(projectPath, "");
            }

            cci.reposition(baseFilePath, leftFilePath, rightFilePath);

//                System.out.println("------------------------------------------------");
//                System.out.println(cci.getFilePath());
//                System.out.println(cci.getBegin() + " -> " + cci.getLeftBegin() + " -> " + cci.getRightBegin());
//                System.out.println(cci.getEnd() + " -> " + cci.getLeftEnd() + " -> " + cci.getRightEnd());
//                System.out.println("------------------------------------------------");
        }
    }

    public static void fillingDepedencyMatrix(List<ConflictingChunkInformation> ccis, String projectPath,
            List<ClassLanguageContructs> ASTLeft, List<ClassLanguageContructs> ASTRight, int[][] dependencyMatrix) {
        for (ConflictingChunkInformation cci : ccis) {

            //Find method declaration that has some intersection with a method declaration
            List<MyMethodDeclaration> leftMethodDeclarations = leftCCMethodDeclarations(projectPath, cci, ASTLeft);
            List<MyMethodDeclaration> rightMethodDeclarations = rightCCMethodDeclaration(projectPath, cci, ASTRight);

            int rowNumber = ccis.indexOf(cci);

            for (ConflictingChunkInformation cciAux : ccis) {
                int columnNumber = ccis.indexOf(cciAux);

                List<MyMethodInvocation> leftMethodInvocations = leftCCMethodInvocations(projectPath, cciAux, ASTLeft);
                List<MyMethodInvocation> rightMethodInvocations = rightCCMethodInvocations(projectPath, cciAux, ASTRight);

                boolean hasDependecy
                        = hasMethodDependency(leftMethodDeclarations, leftMethodInvocations)
                        || hasMethodDependency(rightMethodDeclarations, rightMethodInvocations);

                if (hasDependecy) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    dependencyMatrix[columnNumber][rowNumber] = 1;
                }

            }

//                System.out.println("-----------------------------------------------------------------------");
//                System.out.println(cci.toString());
//                System.out.println("Left");
//                for (MyMethodDeclaration methodDeclaration : leftMethodDeclarations) {
//                    System.out.println("\t" + methodDeclaration.getMethodDeclaration().getName().getIdentifier());
//                }
//                
//                System.out.println("Right");
//                for (MyMethodDeclaration methodDeclaration : rightMethodDeclarations) {
//                    System.out.println("\t" + methodDeclaration.getMethodDeclaration().getName().getIdentifier());
//                }
//                System.out.println("-----------------------------------------------------------------------");
        }
    }

    public static int[][] instanciatingDepedencyMatrix(int order) {
        int[][] dependencyMatrix = new int[order][order];
        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                dependencyMatrix[i][j] = 0;
            }
        }
        return dependencyMatrix;
    }

    public static void printDepedencyMatrix(int[][] dependencyMatrix) {

        int order = dependencyMatrix.length;
        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                System.out.print(dependencyMatrix[i][j] + "  ");
            }
            System.out.println("");
        }
    }

    public static boolean hasMethodDependency(List<MyMethodDeclaration> methodDeclarations, List<MyMethodInvocation> methodInvocations) {

        for (MyMethodInvocation methodInvocation : methodInvocations) {
            for (MyMethodDeclaration methodDeclaration : methodDeclarations) {

                if (sameMethod(methodDeclaration, methodInvocation)) {
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean sameMethod(MyMethodDeclaration methodDeclaration, MyMethodInvocation methodInvocation) {

        IMethodBinding methodDeclarationBinding = methodDeclaration.getMethodDeclaration().resolveBinding();
        IMethodBinding methodInvocationBinding = methodInvocation.getMethodInvocation().resolveMethodBinding();

        if (methodDeclarationBinding != null && methodInvocationBinding != null && methodDeclarationBinding.equals(methodInvocationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    public static void printConflictingChunksInformation(List<ConflictingChunkInformation> ccis) throws IOException {
        for (ConflictingChunkInformation cci : ccis) {
            List<String> fileLines = FileUtils.readLines(new File(cci.getFilePath()));

            List<String> ccLines = fileLines.subList(cci.getBegin(), cci.getEnd() + 1);

            System.out.println(cci.toString());
            for (String ccLine : ccLines) {
                System.out.println("\t\t" + ccLine);
            }
        }
    }

    public static boolean isFailedMerge(String projectPath, String SHALeft, String SHARight) {
        Git.reset(projectPath);
        Git.checkout(projectPath, SHALeft);
        List<String> merge = Git.merge(projectPath, SHARight, false, false);

        return MergeStatusAnalizer.isConflict(merge);
    }

    public static List<ClassLanguageContructs> extractAST(String projectPath) {

        Dependencies dependencies = new Dependencies(projectPath);

        return dependencies.getClassesLanguageConstructs();
    }

    public static void clone(String projectPath, String target) throws IOException {

        File targetFile = new File(target);

        if (targetFile.isDirectory()) {
            FileUtils.deleteDirectory(targetFile);
        }

        Git.clone(projectPath, projectPath, target);

    }

    public static List<MyMethodDeclaration> leftCCMethodDeclarations(String projectPath,
            ConflictingChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodDeclaration> result = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (AST.getPath().contains(relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    if (leftHasIntersection(methodDeclaration, cci)) {
                        result.add(methodDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static boolean leftHasIntersection(Location location, ConflictingChunkInformation cci) {

        if (cci.getLeftBegin() > location.getElementLineEnd()) {
            return false;
        } else if (cci.getLeftEnd() < location.getElementLineBegin()) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean leftHasIntersection(MyMethodDeclaration methodDeclaration, ConflictingChunkInformation cci) {

        return leftHasIntersection(methodDeclaration.getLocation(), cci);

    }

    public static List<MyMethodDeclaration> rightCCMethodDeclaration(String projectPath,
            ConflictingChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodDeclaration> result = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathRight() != null) {
            relativePath = cci.getRelativePathRight();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (AST.getPath().contains(relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    if (rightHasIntersection(methodDeclaration, cci)) {
                        result.add(methodDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static boolean rightHasIntersection(Location location, ConflictingChunkInformation cci) {

        if (cci.getRightBegin() > location.getElementLineEnd()) {
            return false;
        } else if (cci.getRightEnd() < location.getElementLineBegin()) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean rightHasIntersection(MyMethodDeclaration methodDeclaration, ConflictingChunkInformation cci) {

        return rightHasIntersection(methodDeclaration.getLocation(), cci);

    }

    public static List<MyMethodInvocation> leftCCMethodInvocations(String projectPath,
            ConflictingChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodInvocation> result = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (AST.getPath().contains(relativePath)) {

                List<MyMethodInvocation> methodInvocations = AST.getMethodInvocations();

                for (MyMethodInvocation methodInvocation : methodInvocations) {
                    if (leftHasIntersection(methodInvocation.getLocation(), cci)) {
                        result.add(methodInvocation);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyMethodInvocation> rightCCMethodInvocations(String projectPath,
            ConflictingChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodInvocation> result = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathRight() != null) {
            relativePath = cci.getRelativePathRight();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (AST.getPath().contains(relativePath)) {

                List<MyMethodInvocation> methodInvocations = AST.getMethodInvocations();

                for (MyMethodInvocation methodInvocation : methodInvocations) {
                    if (rightHasIntersection(methodInvocation.getLocation(), cci)) {
                        result.add(methodInvocation);
                    }
                }
            }
        }

        return result;
    }

    public static void printDependencies(int[][] dependencyMatrix) {

        for (int i = 0; i < dependencyMatrix.length; i++) {
            int[] row = dependencyMatrix[i];
            for (int j = 0; j < row.length; j++) {
                if (dependencyMatrix[i][j] == 1) {
                    System.out.println("CC" + (i + 1) + " depends on CC" + (j + 1));
                }
            }

        }

    }

    public static List<CCDependency> extractDependencies(int[][] dependencyMatrix) {

        List<CCDependency> result = new ArrayList<>();

        for (int i = 0; i < dependencyMatrix.length; i++) {
            int[] row = dependencyMatrix[i];
            for (int j = 0; j < row.length; j++) {
                if (dependencyMatrix[i][j] == 1) {
                    result.add(new CCDependency(i, j));
                }
            }

        }

        return result;
    }

}
