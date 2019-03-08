/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.cmd.CMDOutput;
import br.uff.ic.gems.resources.cmd.CMD;
import br.uff.ic.gems.resources.operation.Operation;
import br.uff.ic.mergeguider.MergeGuider;
import br.uff.ic.mergeguider.dependency.DependencyType;
import br.uff.ic.mergeguider.javaparser.ClassLanguageContructs;
import br.uff.ic.mergeguider.javaparser.ProjectAST;
import br.uff.ic.mergeguider.languageConstructs.Location;
import br.uff.ic.mergeguider.languageConstructs.MyAnnotationDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyAnnotationUsage;
import br.uff.ic.mergeguider.languageConstructs.MyAttributeCall;
import br.uff.ic.mergeguider.languageConstructs.MyAttributeDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyMethodInvocation;
import br.uff.ic.mergeguider.languageConstructs.MyTypeDeclaration;
import br.uff.ic.mergeguider.languageConstructs.MyVariableCall;
import br.uff.ic.mergeguider.languageConstructs.MyVariableDeclaration;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleType;

/**
 *
 * @author Gleiph, Cristiane
 */
public class CouplingChunks {

    public static void main(String[] args) {
        List<String> projectsPath = new ArrayList<>();
        //projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\banco");
        // projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\banco_old");
        projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\banco_new");
        String sandbox = "C:\\Cristiane\\mestrado\\sandbox";
        String outputPathName = "C:\\Cristiane\\mestrado\\results_structural_coupling\\results.txt";

        for (String projectPath : projectsPath) {
            System.out.println("Project: " + projectPath);
            analyze(projectPath, sandbox, outputPathName);
        }

    }

    public static void analyze(String projectPath, String sandbox, String outputPathName) {
        List<String> mergeRevisions = Git.getMergeRevisions(projectPath);
        int hasDependencies = 0, hasNoDependencies = 0, oneCC = 0, moreThanOneCC = 0;

        MergeDependency mergeDependency = new MergeDependency();

        try (FileWriter file = new FileWriter(new File(outputPathName))) {
            file.write(projectPath + "\n");
            int Chunks, dependencies;
            String project, SHAMerge, SHALeft, SHARight, SHAmergeBase;

            int methodQtd = 0;
            int attributeQtd = 0;
            project = projectPath;

            for (String mergeRevision : mergeRevisions) {
                SHAMerge = mergeRevision;
                List<String> parents = Git.getParents(projectPath, mergeRevision);
                if (parents.size() == 2) {
                    SHALeft = parents.get(0);
                    SHARight = parents.get(1);
                    SHAmergeBase = Git.getMergeBase(projectPath, SHALeft, SHARight);
                    //Check if is a fast-forward merge*/
                    if ((!(SHAmergeBase.equals(SHALeft))) && (!(SHAmergeBase.equals(SHARight)))) {

                        try {
                            mergeDependency = performMerge(projectPath, SHALeft, SHARight, SHAmergeBase, sandbox);

                            //Treating dependencies 
                            if (mergeDependency == null) {
                                Chunks = 0;
                                dependencies = 0;
                            } else {
                                methodQtd = 0;
                                attributeQtd = 0;
                                Chunks = mergeDependency.getChunksAmount();
                                dependencies = mergeDependency.getChunksDependencies().size();

                                List<ChunkInformation> cis = mergeDependency.getCis();
                                for (ChunksDependency chunksDependency : mergeDependency.getChunksDependencies()) {
                                    int reference = cis.indexOf(chunksDependency.getReference());
                                    int dependsOn = cis.indexOf(chunksDependency.getDependsOn());
                                    String depedencyType = chunksDependency.getType().toString();
                                    if (depedencyType.equals("METHOD_DECLARATION_INVOCATION")) {
                                        methodQtd++;
                                    } else if (depedencyType.equals("ATTRIBUTE_DECLARATION_USAGE")) {
                                        attributeQtd++;
                                    }
                                    //System.out.println("(" + reference + ", " + dependsOn + ", " + depedencyType + ")");
                                }
                            }
                            file.write(SHAMerge + ", " + Chunks + ", " + dependencies + ", " + methodQtd + ", " + attributeQtd + "\n");
                            System.out.println(SHAMerge + " Chunks: " + Chunks + ", Dependencies: " + dependencies + ", Method: " + methodQtd + ", Attribute: " + attributeQtd);
                        } catch (IOException ex) {
                            System.out.println("Merge between revisions " + SHALeft + " and " + SHARight + " was not performed.");
                        }
                    }
                }
            }
            file.close();
        } catch (IOException ex) {
            System.out.println("The file could not be created");
        }
    }

    public static MergeDependency performMerge(String projectPath, String SHALeft, String SHARight, String SHAmergeBase, String sandbox) throws IOException {
        //VRF se está vindo certo e depois apagar o diff desta classe
        List<String> diffLeft = Git.diff(projectPath, SHAmergeBase, SHALeft);
        List<String> diffRight = Git.diff(projectPath, SHAmergeBase, SHARight);

        //Getting modified files 
        List<String> changedFilesLeft = Git.getChangedFiles(projectPath, SHALeft, SHAmergeBase);
        List<String> changedFilesRight = Git.getChangedFiles(projectPath, SHARight, SHAmergeBase);

        //to remove files that have extension other than java
        for (int i = 0; i < changedFilesLeft.size(); i++) {
            if (!changedFilesLeft.get(i).endsWith("java")) {
                changedFilesLeft.remove(changedFilesLeft.get(i));
            }
        }
        for (int i = 0; i < changedFilesRight.size(); i++) {
            if (!changedFilesRight.get(i).endsWith("java")) {
                changedFilesRight.remove(changedFilesRight.get(i));
            }
        }

        //Getting chunks tem que armazenar as linhas add e removidas para cada arquivo
        List<ChunkInformation> cisL = ChunkInformation.extractChunksInformation(projectPath, changedFilesLeft, SHAmergeBase, SHALeft, "Left");
        List<ChunkInformation> cisR = ChunkInformation.extractChunksInformation(projectPath, changedFilesRight, SHAmergeBase, SHARight, "Right");

        //Union Left and Right modified files  
        List<String> changedFiles = new ArrayList<String>();
        for (String fileLeft : changedFilesLeft) {
            changedFiles.add(fileLeft);
        }

        for (String fileRight : changedFilesRight) {
            changedFiles.add(fileRight);
        }

        for (int i = 0; i < changedFiles.size(); i++) {
            for (int j = i + 1; j < changedFiles.size(); j++) {
                if (changedFiles.get(i).equals(changedFiles.get(j))) {
                    changedFiles.remove(changedFiles.get(j));
                }
            }
        }

        //Extracting Left AST
        System.out.println("Cloning left repository...");
        String repositoryLeft = sandbox + File.separator + "left";

        MergeGuider.clone(projectPath, repositoryLeft);
        Git.reset(repositoryLeft);
        Git.clean(repositoryLeft);
        Git.checkout(repositoryLeft, SHALeft);

        System.out.println("Extracting left repository AST...");

        List<ClassLanguageContructs> ASTLeft = extractAST(repositoryLeft);

        //Extracting Right AST
        System.out.println("Cloning right repository...");

        String repositoryRight = sandbox + File.separator + "right";

        MergeGuider.clone(projectPath, repositoryRight);
        Git.reset(repositoryRight);
        Git.clean(repositoryRight);
        Git.checkout(repositoryRight, SHARight);

        System.out.println("Extracting right repository AST...");

        List<ClassLanguageContructs> ASTRight = extractAST(repositoryRight);

        //Extracting merge-base AST
        System.out.println("Cloning merge-base repository...");
        String repositoryBase = sandbox + File.separator + "base";

        MergeGuider.clone(projectPath, repositoryBase);
        Git.reset(repositoryBase);
        Git.clean(repositoryBase);
        Git.checkout(repositoryBase, SHAmergeBase);

        System.out.println("Extracting merge-base repository AST...");

        List<ClassLanguageContructs> ASTmergeBase = extractAST(repositoryBase);

        System.out.println("Repositioning...");
        repositioningChunksInformation(cisL, repositoryLeft, projectPath, repositoryBase);
        repositioningChunksInformation(cisR, repositoryRight, projectPath, repositoryBase);

        //Getting modified files AST
        List<ClassLanguageContructs> ASTchangedFilesLeft = generateASTFiles(ASTLeft, changedFilesLeft);
        List<ClassLanguageContructs> ASTchangedFilesRight = generateASTFiles(ASTRight, changedFilesRight);

        //Creating depedency matrix
        System.out.println("Extracting dependency matrix...");

        //Union Left and Right ChunkInformation 
        List<ChunkInformation> cis = new ArrayList<ChunkInformation>();
        for (ChunkInformation cisLeft : cisL) {
            cis.add(cisLeft);
        }

        for (ChunkInformation cisRight : cisR) {
            cis.add(cisRight);
        }

        MergeDependency mergeDependency = extractDepedencies(cisL, cisR, projectPath, ASTchangedFilesLeft, ASTchangedFilesRight, ASTmergeBase);
        mergeDependency.setCis(cis);

        return mergeDependency;
    }

    public static List<ClassLanguageContructs> generateASTFiles(List<ClassLanguageContructs> ASTProject, List<String> changedFiles) {
        List<ClassLanguageContructs> ASTchangedFiles = new ArrayList<ClassLanguageContructs>();

        //VRF se os caminhos dos arquivos da AST contém os nomes dos arquivos modificados
        for (String file : changedFiles) {
            for (ClassLanguageContructs AST : ASTProject) {
                if (containsPath(AST.getPath(), file)) {
                    ASTchangedFiles.add(AST);
                }
            }
        }
        return ASTchangedFiles;
    }

    public static List<String> diffStat(String repository, String mergebaseSHA, String commitSHA) {
        List<String> result = new ArrayList<String>();
        String command = "git diff --stat " + mergebaseSHA + " " + commitSHA;

        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }

    public static List<String> diff(String repository, String mergebaseSHA, String commitSHA) {
        List<String> result = new ArrayList<String>();
        String command = "git diff " + mergebaseSHA + " " + commitSHA;

        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }

    public static void repositioningChunksInformation(List<ChunkInformation> cis, String repositoryPath, String projectPath, String repositoryBase) {
        //Repositioning conflicting chunk information to both sides
        for (ChunkInformation ci : cis) {

            String baseFilePath = ci.getFilePath();
            String filePath, mergeBaseFilePath;

            if (ci.isRenamed() && ci.getRelativePathLeft() != null) {
                filePath = repositoryPath + File.separator
                        + ci.getRelativePathLeft();
            } else {
                filePath = repositoryPath + File.separator
                        + baseFilePath.replace(projectPath, "");
            }

            if (ci.isRenamed() && ci.getRelativePathLeft() != null) {
                mergeBaseFilePath = repositoryBase + File.separator
                        + ci.getRelativePathLeft();
            } else {
                mergeBaseFilePath = repositoryBase + File.separator
                        + baseFilePath.replace(projectPath, "");
            }

            ci.reposition(filePath, mergeBaseFilePath);

        }
    }

    public static MergeDependency extractDepedencies(List<ChunkInformation> cisL, List<ChunkInformation> cisR, String projectPath,
            List<ClassLanguageContructs> ASTLeft, List<ClassLanguageContructs> ASTRight, List<ClassLanguageContructs> ASTmergeBase) {

        MergeDependency mergeDependency = new MergeDependency();
        mergeDependency.setChunksAmount(cisL.size() + cisR.size());

        for (ChunkInformation ci : cisL) {

            //Find method declaration that has some intersection with a method declaration
            List<MyMethodDeclaration> leftMethodDeclarations = leftCCMethodDeclarations(projectPath, ci, ASTLeft);
            List<MyMethodInvocation> leftMethodInvocations = leftCCMethodInvocations(projectPath, ci, ASTLeft);

            List<MyMethodDeclaration> leftBaseMethodDeclarations = leftCCMethodDeclarations(projectPath, ci, ASTmergeBase);
            List<MyMethodInvocation> leftBaseMethodInvocations = leftCCMethodInvocations(projectPath, ci, ASTmergeBase);

            //Find attribute declarations
            List<MyAttributeDeclaration> leftAttributeDeclarations = leftAttributes(projectPath, ci, ASTLeft);
            List<MyAttributeCall> leftAttributeCalls = leftAttributeCalls(projectPath, ci, ASTLeft);

            List<MyAttributeDeclaration> leftBaseAttributeDeclarations = leftAttributes(projectPath, ci, ASTmergeBase);
            List<MyAttributeCall> leftBaseAttributeCalls = leftAttributeCalls(projectPath, ci, ASTmergeBase);

            //Find variable declarations
            List<MyVariableDeclaration> leftVariableDeclarations = leftVariableDeclarations(projectPath, ci, ASTLeft);
            List<MyVariableDeclaration> rightVariableDeclarations = rightVariableDeclarations(projectPath, ci, ASTRight);

            //int rowNumber = cisL.indexOf(ci);
            for (ChunkInformation ciAux : cisR) {
                int columnNumber = cisR.indexOf(ciAux);

                List<MyMethodDeclaration> rightMethodDeclarations = rightCCMethodDeclaration(projectPath, ciAux, ASTRight);
                List<MyMethodInvocation> rightMethodInvocations = rightCCMethodInvocations(projectPath, ciAux, ASTRight);

                List<MyMethodDeclaration> rightBaseMethodDeclarations = rightCCMethodDeclaration(projectPath, ciAux, ASTmergeBase);
                List<MyMethodInvocation> rightBaseMethodInvocations = rightCCMethodInvocations(projectPath, ciAux, ASTmergeBase);

                List<MyAttributeDeclaration> rightAttributeDeclarations = rightAttributes(projectPath, ciAux, ASTRight);
                List<MyAttributeCall> rightAttributeCalls = rightAttributeCalls(projectPath, ciAux, ASTRight);

                List<MyAttributeDeclaration> rightBaseAttributeDeclarations = rightAttributes(projectPath, ciAux, ASTmergeBase);
                List<MyAttributeCall> rightBaseAttributeCalls = rightAttributeCalls(projectPath, ciAux, ASTmergeBase);

                List<MyVariableCall> leftVariableCalls = leftVariableCalls(projectPath, ciAux, ASTLeft);
                List<MyVariableCall> rightVariableCalls = rightVariableCalls(projectPath, ciAux, ASTRight);

                boolean hasMethodDependecy
                        = hasMethodDependency(rightBaseMethodDeclarations, leftMethodInvocations)
                        || hasMethodDependency(leftBaseMethodDeclarations, rightMethodInvocations);

                if (hasMethodDependecy) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ciAux, ci, DependencyType.METHOD_DECLARATION_INVOCATION);
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }

                boolean hasAttributeDepedency
                        = hasAttributeDependency(leftBaseAttributeDeclarations, rightAttributeCalls)
                        || hasAttributeDependency(rightBaseAttributeDeclarations, leftAttributeCalls);

                if (hasAttributeDepedency) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ciAux, ci, DependencyType.ATTRIBUTE_DECLARATION_USAGE);
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }

                boolean hasVariableDepedency
                        = hasVariableDependency(leftVariableDeclarations, leftVariableCalls)
                        || hasVariableDependency(rightVariableDeclarations, rightVariableCalls);

                if (hasVariableDepedency) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    //if (columnNumber != rowNumber) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ciAux, ci, DependencyType.VARIABLE_DECLARATION_USAGE);
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                    //}
                }

            }

        }
        return mergeDependency;
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

        if (methodDeclarationBinding != null && methodInvocationBinding != null && methodInvocationBinding.isEqualTo(methodDeclarationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    //Treating attribute dependency between declaration and usage
    public static boolean hasAttributeDependency(List<MyAttributeDeclaration> attributeDeclarations, List<MyAttributeCall> attributeCalls) {

        for (MyAttributeCall attributeCall : attributeCalls) {
            for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {

                if (sameAttribute(attributeDeclaration, attributeCall)) {
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean sameAttribute(MyAttributeDeclaration attributeDeclaration, MyAttributeCall attributeCall) {

        IVariableBinding attributeDeclarationBinding = attributeDeclaration.getFieldDeclaration().resolveBinding();
        IBinding attributeCallBinding = attributeCall.getSimpleName().resolveBinding();

        if (attributeDeclarationBinding != null && attributeCallBinding != null && attributeCallBinding.isEqualTo(attributeDeclarationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    //Treating variable dependency between declaration and usage
    public static boolean hasVariableDependency(List<MyVariableDeclaration> variableDeclarations, List<MyVariableCall> variableCalls) {

        for (MyVariableCall variableCall : variableCalls) {
            for (MyVariableDeclaration variableDeclaration : variableDeclarations) {

                if (sameVariable(variableDeclaration, variableCall)) {
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean sameVariable(MyVariableDeclaration variableDeclaration, MyVariableCall variableCall) {

        IVariableBinding attributeDeclarationBinding = variableDeclaration.resolveBinding();
        IBinding attributeCallBinding = variableCall.getSimpleName().resolveBinding();

        if (attributeDeclarationBinding != null && attributeCallBinding != null && attributeDeclarationBinding.equals(attributeCallBinding)) {
            return true;
        } else {
            return false;
        }
    }

    //Treating dependency between a type declaration and its usage as variable declaration
    public static boolean hasTypeDeclarationDependencyVariable(List<MyTypeDeclaration> typeDeclarations, List<MyVariableDeclaration> variableDeclarations) {

        for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
            for (MyTypeDeclaration typeDeclaration : typeDeclarations) {

                if (sameTypeDeclaration(typeDeclaration, variableDeclaration)) {
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean sameTypeDeclaration(MyTypeDeclaration typeDeclaration, MyVariableDeclaration variableDeclaration) {

        ITypeBinding typeDeclarationBinding = typeDeclaration.getTypeDeclaration().resolveBinding();
        ITypeBinding variableDeclarationBinding = variableDeclaration.resolveTypeBinding();

        if (typeDeclarationBinding != null && variableDeclarationBinding != null && typeDeclarationBinding.equals(variableDeclarationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    //Treating dependency between a type declaration and its usage as attribute declaration
    public static boolean hasTypeDeclarationDependencyAttribute(List<MyTypeDeclaration> typeDeclarations, List<MyAttributeDeclaration> attributeDeclarations) {

        for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {
            for (MyTypeDeclaration typeDeclaration : typeDeclarations) {

                if (sameTypeDeclaration(typeDeclaration, attributeDeclaration)) {
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean sameTypeDeclaration(MyTypeDeclaration typeDeclaration, MyAttributeDeclaration attributeDeclaration) {

        ITypeBinding typeDeclarationBinding = typeDeclaration.getTypeDeclaration().resolveBinding();
        IBinding variableDeclarationBinding = attributeDeclaration.resolveTypeBinding();

        if (typeDeclarationBinding != null && variableDeclarationBinding != null && typeDeclarationBinding.equals(variableDeclarationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    //Treating dependency between a type declaration its interfaces
    public static boolean hasDependencyTypeDeclarationInterface(List<MyTypeDeclaration> typeDeclarations, List<MyTypeDeclaration> typeDeclarationsAux) {

        for (MyTypeDeclaration typeDeclaration : typeDeclarations) {
            for (MyTypeDeclaration typeDeclarationAux : typeDeclarationsAux) {
                List<SimpleType> interfaces = typeDeclarationAux.getInterfaces();
                for (SimpleType aInterface : interfaces) {
                    if (sameTypeDeclaration(typeDeclaration, aInterface)) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    public static boolean sameTypeDeclaration(MyTypeDeclaration typeDeclaration, SimpleType interfaace) {

        ITypeBinding typeDeclarationBinding = typeDeclaration.getTypeDeclaration().resolveBinding();
        ITypeBinding variableDeclarationBinding = interfaace.resolveBinding();

        if (typeDeclarationBinding != null && variableDeclarationBinding != null && typeDeclarationBinding.equals(variableDeclarationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    /*public static void printChunksInformation(List<ChunkInformation> cis) throws IOException {
        for (ChunkInformation ci : cis) {
            List<String> fileLines = FileUtils.readLines(new File(ci.getFilePath()));

            List<String> ccLines = fileLines.subList(ci.getBegin(), ci.getEnd() + 1);

            System.out.println(ci.toString());
            for (String ccLine : ccLines) {
                System.out.println("\t\t" + ccLine);
            }
        }
    }*/
    public static List<ClassLanguageContructs> extractAST(String projectPath) {

        ProjectAST projectAST = new ProjectAST(projectPath);

        return projectAST.getClassesLanguageConstructs();
    }

    public static void clone(String projectPath, String target) throws IOException {

        File targetFile = new File(target);

        if (targetFile.isDirectory()) {
            FileUtils.deleteDirectory(targetFile);
        }

        Git.clone(projectPath, projectPath, target);

    }

    public static List<MyMethodDeclaration> leftCCMethodDeclarations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathLeft() != null) {
            relativePath = ci.getRelativePathLeft();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(methodDeclaration, ci, line)) {
                            result.add(methodDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean leftHasIntersection(Location location, ChunkInformation cci, int line) {

        if ((line >= location.getElementLineBegin()) && (line <= location.getElementLineEnd())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean leftHasIntersection(MyMethodDeclaration methodDeclaration, ChunkInformation cci, int line) {

        return leftHasIntersection(methodDeclaration.getLocation(), cci, line);

    }

    public static List<MyMethodDeclaration> rightCCMethodDeclaration(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathRight() != null) {
            relativePath = cci.getRelativePathRight();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {

                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(methodDeclaration, cci, line)) {
                            result.add(methodDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean rightHasIntersection(Location location, ChunkInformation cci, int line) {

        if ((line >= location.getElementLineBegin()) && (line <= location.getElementLineEnd())) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean rightHasIntersection(MyMethodDeclaration methodDeclaration, ChunkInformation cci, int line) {

        return rightHasIntersection(methodDeclaration.getLocation(), cci, line);

    }

    public static List<MyMethodInvocation> leftCCMethodInvocations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodInvocation> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) { //PERCORRE

            if (containsPath(AST.getPath(), relativePath)) { //VRF SE É O MEMO FILE

                List<MyMethodInvocation> methodInvocations = AST.getMethodInvocations();

                for (MyMethodInvocation methodInvocation : methodInvocations) {
                    // repositionBase = cci.getReposition();
                    //for (int line : repositionBase) 
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();

                        if (leftHasIntersection(methodInvocation.getLocation(), cci, line)) { //SE AS INVOCACOES DE METODO ESTAO DENTRO DO CCCI(CONFLICT CHUNK INFORMATUON
                            result.add(methodInvocation);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyMethodInvocation> rightCCMethodInvocations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodInvocation> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (cci.isRenamed() && cci.getRelativePathRight() != null) {
            relativePath = cci.getRelativePathRight();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodInvocation> methodInvocations = AST.getMethodInvocations();

                for (MyMethodInvocation methodInvocation : methodInvocations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {

                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(methodInvocation.getLocation(), cci, line)) {
                            result.add(methodInvocation);
                        }
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

    public static List<MyAttributeDeclaration> leftAttributes(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAttributeDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeDeclaration> attributeDeclarations = AST.getAttributes();

                for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(attributeDeclaration.getLocation(), cci, line)) {
                            result.add(attributeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeDeclaration> rightAttributes(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAttributeDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeDeclaration> attributeDeclarations = AST.getAttributes();

                for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(attributeDeclaration.getLocation(), cci, line)) {
                            result.add(attributeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeCall> leftAttributeCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAttributeCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeCall> attributeCalls = AST.getAttributeCalls();

                for (MyAttributeCall attributeCall : attributeCalls) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(attributeCall.getLocation(), cci, line)) {
                            result.add(attributeCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeCall> rightAttributeCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAttributeCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeCall> attributeCalls = AST.getAttributeCalls();

                for (MyAttributeCall attributeCall : attributeCalls) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (rightHasIntersection(attributeCall.getLocation(), cci, line)) {
                            result.add(attributeCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> leftVariableDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyVariableDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();
        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableDeclaration> variableDeclarations = AST.getVariableDeclarations();

                for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(variableDeclaration.getLocation(), cci, line)) {
                            result.add(variableDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> rightVariableDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyVariableDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableDeclaration> variableDeclarations = AST.getVariableDeclarations();

                for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (rightHasIntersection(variableDeclaration.getLocation(), cci, line)) {
                            result.add(variableDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableCall> leftVariableCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyVariableCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableCall> variableCalls = AST.getVariableCalls();

                for (MyVariableCall variableCall : variableCalls) {
                    //repositionBase = cci.getReposition();
                    //cci.getOperations();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(variableCall.getLocation(), cci, line)) {
                            result.add(variableCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableCall> rightVariableCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyVariableCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableCall> variableCalls = AST.getVariableCalls();

                for (MyVariableCall variableCall : variableCalls) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (rightHasIntersection(variableCall.getLocation(), cci, line)) {
                            result.add(variableCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyTypeDeclaration> leftTypeDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyTypeDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyTypeDeclaration> typeDeclarations = AST.getTypeDeclarations();

                for (MyTypeDeclaration typeDeclaration : typeDeclarations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(typeDeclaration.getLocation(), cci, line)) {
                            result.add(typeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyTypeDeclaration> rightTypeDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyTypeDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyTypeDeclaration> typeDeclarations = AST.getTypeDeclarations();

                for (MyTypeDeclaration typeDeclaration : typeDeclarations) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (rightHasIntersection(typeDeclaration.getLocation(), cci, line)) {
                            result.add(typeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationDeclaration> leftAnnotationDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAnnotationDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAnnotationDeclaration> annotationDeclarations = AST.getAnnotationDeclarations();

                for (MyAnnotationDeclaration annotationDeclaration : annotationDeclarations) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (leftHasIntersection(annotationDeclaration.getLocation(), cci, line)) {
                            result.add(annotationDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationDeclaration> rightAnnotationDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAnnotationDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAnnotationDeclaration> annotationDeclarations = AST.getAnnotationDeclarations();

                for (MyAnnotationDeclaration annotationDeclaration : annotationDeclarations) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (rightHasIntersection(annotationDeclaration.getLocation(), cci, line)) {
                            result.add(annotationDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationUsage> leftAnnotationUsages(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAnnotationUsage> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAnnotationUsage> annotationDeclarations = AST.getAnnotationUsages();

                for (MyAnnotationUsage annotationUsage : annotationDeclarations) {
                    //repositionBase = cci.getReposition();
                    //for (int line : repositionBase) {
                    operations = cci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(annotationUsage.getLocation(), cci, line)) {
                            result.add(annotationUsage);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationUsage> rightAnnotationUsages(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAnnotationUsage> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();

        String relativePath;

        if (cci.isRenamed() && cci.getRelativePathLeft() != null) {
            relativePath = cci.getRelativePathLeft();
        } else {
            relativePath = cci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAnnotationUsage> annotationUsages = AST.getAnnotationUsages();

                for (MyAnnotationUsage annotationUsage : annotationUsages) {
                    repositionBase = cci.getReposition();
                    for (int line : repositionBase) {
                        if (rightHasIntersection(annotationUsage.getLocation(), cci, line)) {
                            result.add(annotationUsage);
                        }
                    }
                }
            }
        }

        return result;
    }

    //Treating dependency between a annotation declaration its usages
    public static boolean hasDependencyAnnotationDeclarationUsage(List<MyAnnotationDeclaration> annotationDeclarations, List<MyAnnotationUsage> annotationUsages) {

        for (MyAnnotationDeclaration annotationDeclaration : annotationDeclarations) {
            for (MyAnnotationUsage annotationUsage : annotationUsages) {
                if (sameAnnotation(annotationDeclaration, annotationUsage)) {
                    return true;
                }
            }

        }

        return false;
    }

    public static boolean sameAnnotation(MyAnnotationDeclaration annotationDeclaration, MyAnnotationUsage annotationUsage) {

        ITypeBinding annotationDeclarationBinding = annotationDeclaration.resolveTypeBinding();
        ITypeBinding annotationUsageBinding = annotationUsage.resolveTypeBinding();

        if (annotationDeclarationBinding != null && annotationUsageBinding != null && annotationDeclarationBinding.equals(annotationUsageBinding)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean containsPath(String path, String relativePath) {
        String pathClean = cleanPath(path);
        String relativeClean = cleanPath(relativePath);

        return pathClean.contains(relativeClean);
    }

    public static String cleanPath(String path) {

        while (path.contains("/")) {
            path = path.replace("/", "");
        }

        while (path.contains("\\")) {
            path = path.replace("\\", "");
        }

        return path;
    }

}
