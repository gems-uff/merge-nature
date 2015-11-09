/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.Dependencies;
import br.uff.ic.mergeguider.languageConstructs.Location;
import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class MergeGuider {

    public static void main(String[] args) throws IOException {
        String projectPath = "/Users/gleiph/Dropbox/doutorado/repositories/lombok";
        String SHALeft = "e557413";
        String SHARight = "fbab1ca";
        String sandbox = "/Users/gleiph/Dropbox/doutorado/repositories/";

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

            //Repositioning conflicting chunk information to both sides
            for (ConflictingChunkInformation cci : ccis) {

                String baseFilePath = cci.getFilePath();
                String leftFilePath = repositoryLeft + File.separator
                        + baseFilePath.replace(projectPath, "");
                String rightFilePath = repositoryRight + File.separator
                        + baseFilePath.replace(projectPath, "");

                cci.reposition(baseFilePath, leftFilePath, rightFilePath);

//                System.out.println("------------------------------------------------");
//                System.out.println(cci.getFilePath());
//                System.out.println(cci.getBegin() + " -> " + cci.getLeftBegin() + " -> " + cci.getRightBegin());
//                System.out.println(cci.getEnd() + " -> " + cci.getLeftEnd() + " -> " + cci.getRightEnd());
//                System.out.println("------------------------------------------------");
            }

            //Find method declaration that has some intersection with a method declaration
            for (ConflictingChunkInformation cci : ccis) {

                List<MyMethodDeclaration> methodDeclarations = leftHasIntersectionWithMethodDeclaration(projectPath, cci, ASTLeft);
                methodDeclarations.addAll(rightHasIntersectionWithMethodDeclaration(projectPath, cci, ASTRight));

                System.out.println("-----------------------------------------------------------------------");
                System.out.println(cci.toString());
                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    System.out.println(methodDeclaration.getMethodDeclaration().getName().getIdentifier());
                }
                System.out.println("-----------------------------------------------------------------------");

            }

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

    public static List<MyMethodDeclaration> leftHasIntersectionWithMethodDeclaration(String projectPath,
            ConflictingChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodDeclaration> result = new ArrayList<>();

        String relativePath = cci.getFilePath().replace(projectPath, "");

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

    public static boolean leftHasIntersection(MyMethodDeclaration methodDeclaration, ConflictingChunkInformation cci) {

        Location location = methodDeclaration.getLocation();

        if (cci.getLeftBegin() > location.getElementLineEnd()) {
            return false;
        } else if (cci.getLeftEnd() < location.getElementLineBegin()) {
            return false;
        } else {
            return true;
        }

    }

    public static List<MyMethodDeclaration> rightHasIntersectionWithMethodDeclaration(String projectPath,
            ConflictingChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodDeclaration> result = new ArrayList<>();

        String relativePath = cci.getFilePath().replace(projectPath, "");

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

    public static boolean rightHasIntersection(MyMethodDeclaration methodDeclaration, ConflictingChunkInformation cci) {

        Location location = methodDeclaration.getLocation();

        if (cci.getRightBegin() > location.getElementLineEnd()) {
            return false;
        } else if (cci.getRightEnd() < location.getElementLineBegin()) {
            return false;
        } else {
            return true;
        }

    }

}
