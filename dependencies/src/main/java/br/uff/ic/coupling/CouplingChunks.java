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
import static oracle.jrockit.jfr.events.Bits.floatValue;
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
        projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\school_conceitual");
        //projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\metodo_atributo_rename");
        //projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\variable");
        //projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\estrutural2");// p testar atributo
        // projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\variavel");
        //projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\banco_atributo");
        // projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\banco2");
        // projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\interviews");
        //projectsPath.add("C:\\Cristiane\\mestrado\\repositorios_teste\\rename2");
        String sandbox = "C:\\Cristiane\\mestrado\\sandbox";
        String sandboxAux = "C:\\Cristiane\\mestrado\\sandboxAux";
        String outputPathName = "C:\\Cristiane\\mestrado\\results_structural_coupling\\";

        for (String projectPath : projectsPath) {
            System.out.println("Project: " + projectPath);
            analyze(projectPath, sandbox, outputPathName, sandboxAux);
        }

    }

    public static void analyze(String projectPath, String sandbox, String outputPathName, String sandboxAux) {
        List<String> mergeRevisions = Git.getMergeRevisions(projectPath);
        int hasDependencies = 0, hasNoDependencies = 0, oneCC = 0, moreThanOneCC = 0;

        MergeDependency mergeDependency = new MergeDependency();
        String projectName = projectPath.substring(projectPath.lastIndexOf(File.separator) + 1, projectPath.length());
        try (FileWriter file = new FileWriter(new File(outputPathName + projectName + ".txt"))) {
            //file.write(projectPath + "\n");
            int chunks, dependencies;
            String project, SHAMerge, SHALeft, SHARight, SHAmergeBase;

            int couplings = 0;
            int attributeQtd = 0;
            int variableQtd = 0;
            int leftBranch = 0;
            int rightBranch = 0;
            float second = 0;
            float third = 0;
            int intensity = 0;
            //double harmonica_media = 0.0;
            project = projectPath;

           /* SHAMerge = "4a19a20daf4aed275bcf27c32d26a64551678e61";
            SHALeft = "f084009537469ea2598785630bfd8b7e6f1910bc";
            SHARight = "f09cad96a7e2037262a2e57d49ba132490a21565";
            SHAmergeBase = "67f3473140e57336ac24202099e8f4ac9a8255c0";*/
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
                    mergeDependency = performMerge(projectPath, SHALeft, SHARight, SHAmergeBase, sandbox, sandboxAux);

                    //Treating dependencies 
                    if (mergeDependency == null) {
                        chunks = 0;
                        dependencies = 0;
                        couplings = 0;
                        attributeQtd = 0;
                        leftBranch = 0;
                        rightBranch = 0;
                    } else {
                        couplings = 0;
                        attributeQtd = 0;
                        variableQtd = 0;
                        leftBranch = 0;
                        rightBranch = 0;
                        chunks = mergeDependency.getChunksAmount();
                        dependencies = mergeDependency.getChunksDependencies().size();

                        List<ChunkInformation> cis = mergeDependency.getCis();
                        for (ChunksDependency chunksDependency : mergeDependency.getChunksDependencies()) {
                            int reference = cis.indexOf(chunksDependency.getReference());
                            int dependsOn = cis.indexOf(chunksDependency.getDependsOn());
                            String depedencyType = chunksDependency.getType().toString();
                            String branch = chunksDependency.getBranch();
                            if (depedencyType.equals("METHOD_DECLARATION_INVOCATION")) {
                                couplings++;
                                if (branch.equals("Left")) {
                                    leftBranch++;
                                } else if (branch.equals("Right")) {
                                    rightBranch++;
                                }
                            } else if (depedencyType.equals("ATTRIBUTE_DECLARATION_USAGE")) {
                                attributeQtd++;
                            } else if (depedencyType.equals("VARIABLE_DECLARATION_USAGE")) {
                                variableQtd++;
                            }
                            //System.out.println("(" + reference + ", " + dependsOn + ", " + depedencyType + ")");
                        }
                    }
                    
                        intensity = leftBranch + rightBranch;
                    
                    
                    if (chunks > 0){
                        second = intensity / chunks;
                    }
                    
                    if (couplings > 0){
                        third = intensity / couplings;
                    }
                    
                    /*if ((leftBranch >= 1) && (rightBranch >= 1)) {
                        harmonica_media = (floatValue(methodQtd)/(1.0 / leftBranch + 1.0 / rightBranch));
                    } else if ((leftBranch >= 1) && (rightBranch < 1)) {
                        harmonica_media = (floatValue(methodQtd) / (1.0 / leftBranch + rightBranch));
                    } else if ((leftBranch < 1) && (rightBranch >= 1)) {
                        harmonica_media = (floatValue(methodQtd) / (leftBranch + 1.0 / rightBranch));
                    }*/

                    //file.write(SHAMerge + ", " + chunks + ", " + dependencies + ", " + methodQtd + ", " + attributeQtd + ", " + variableQtd + "\n");
                    file.write(SHAMerge + ", " + leftBranch + ", " + rightBranch + ", " + intensity + ", " +
                            second + ", " + third + "\n");
                    //System.out.println(SHAMerge + " chunks: " + chunks + ", Dependencies: " + dependencies + ", Method: " + methodQtd + ", Attribute: " + attributeQtd + ", Variable: " + variableQtd);
                    System.out.println(SHAMerge + " chunks: " + chunks + ", Dependencies: " + dependencies + ", Method: " + chunks + ", Left: " + leftBranch + ", Right: " + rightBranch);
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

    public static MergeDependency performMerge(String projectPath, String SHALeft, String SHARight, String SHAmergeBase, String sandbox, String sandboxAux) throws IOException {
        //VRF se está vindo certo e depois apagar o diff desta classe
        List<String> diffLeft = Git.diff(projectPath, SHAmergeBase, SHALeft);
        List<String> diffRight = Git.diff(projectPath, SHAmergeBase, SHARight);

        //Getting modified files 
        List<String> changedFilesLeftAux = Git.getChangedFiles(projectPath, SHALeft, SHAmergeBase);
        List<String> changedFilesRightAux = Git.getChangedFiles(projectPath, SHARight, SHAmergeBase);
        List<String> changedFilesLeft = new ArrayList<String>();
        List<String> changedFilesRight = new ArrayList<String>();

        //to remove files that have extension other than java
        for (int i = 0; i < changedFilesLeftAux.size(); i++) {
            if (changedFilesLeftAux.get(i).endsWith("java")) {
                changedFilesLeft.add(changedFilesLeftAux.get(i));
            }
        }
        for (int i = 0; i < changedFilesRightAux.size(); i++) {
            if (changedFilesRightAux.get(i).endsWith("java")) {
                changedFilesRight.add(changedFilesRightAux.get(i));
            }
        }
        //If not exist java files, the variable changedFiles can be empty and we can't identify dependencies
        if ((changedFilesLeft.isEmpty()) || (changedFilesRight.isEmpty())) {
            return null;
        }

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

        //System.out.println("Repositioning...");
        //repositioningChunksInformation(cisL, repositoryLeft, projectPath, repositoryBase);
        //repositioningChunksInformation(cisR, repositoryRight, projectPath, repositoryBase);
        //Getting modified files AST
        List<ClassLanguageContructs> ASTchangedFilesLeft = generateASTFiles(ASTLeft, changedFilesLeft);
        List<ClassLanguageContructs> ASTchangedFilesRight = generateASTFiles(ASTRight, changedFilesRight);

        //Getting chunks tem que armazenar as linhas add e removidas para cada arquivo
        List<ChunkInformation> cisL = ChunkInformation.extractChunksInformation(repositoryLeft, changedFilesLeft, SHAmergeBase, SHALeft, "Left");
        List<ChunkInformation> cisR = ChunkInformation.extractChunksInformation(repositoryRight, changedFilesRight, SHAmergeBase, SHARight, "Right");

        //Creating depedency matrix
        //System.out.println("Extracting dependency matrix...");
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
        int chunksLeft = 0;
        int chunksRight = 0;

        for (ChunkInformation ci : cisL) {

            //Find method declaration that has some intersection with a method declaration
            List<MyMethodDeclaration> leftMethodDeclarations = leftCCMethodDeclarations(projectPath, ci, ASTLeft);
            List<MyMethodInvocation> leftMethodInvocations = leftCCMethodInvocations(projectPath, ci, ASTLeft);

            List<MyMethodDeclaration> leftBaseMethodDeclarations = leftBaseCCMethodDeclarations(projectPath, ci, ASTmergeBase);

            //Find attribute declarations
            List<MyAttributeDeclaration> leftAttributeDeclarations = leftAttributes(projectPath, ci, ASTLeft);
            List<MyAttributeCall> leftAttributeCalls = leftAttributeCalls(projectPath, ci, ASTLeft);

            List<MyAttributeDeclaration> leftBaseAttributeDeclarations = leftBaseAttributes(projectPath, ci, ASTmergeBase);

            //Find variable declarations
            List<MyVariableDeclaration> leftVariableDeclarations = leftVariableDeclarations(projectPath, ci, ASTLeft);
            List<MyVariableCall> leftVariableCalls = leftVariableCalls(projectPath, ci, ASTLeft);

            List<MyVariableDeclaration> leftBaseVariableDeclarations = leftBaseVariableDeclarations(projectPath, ci, ASTmergeBase);

            //count number of modified methods
            List<MyMethodDeclaration> MethodDeclarationsLeftAux = leftMethodDeclarations;
            //List<MyMethodInvocation> MethodInvocationsLeftAux = leftMethodInvocations;

            for (int i = MethodDeclarationsLeftAux.size() - 1; i > 0; i--) {
                if (MethodDeclarationsLeftAux.get(i).equals(MethodDeclarationsLeftAux.get(i - 1))) {
                    MethodDeclarationsLeftAux.remove(MethodDeclarationsLeftAux.get(i));
                }
            }

            /*for (int i = MethodInvocationsLeftAux.size() - 1; i > 0; i--) {
                if (MethodInvocationsLeftAux.get(i).equals(MethodInvocationsLeftAux.get(i - 1))) {
                    MethodInvocationsLeftAux.remove(MethodInvocationsLeftAux.get(i));
                }
            }*/
           // ci.setchunks(MethodDeclarationsLeftAux.size() + MethodInvocationsLeftAux.size());
           ci.setchunks(MethodDeclarationsLeftAux.size());

            //int rowNumber = cisL.indexOf(ci);
            for (ChunkInformation ciAux : cisR) {
                int columnNumber = cisR.indexOf(ciAux);

                List<MyMethodDeclaration> rightMethodDeclarations = rightCCMethodDeclaration(projectPath, ciAux, ASTRight);
                List<MyMethodInvocation> rightMethodInvocations = rightCCMethodInvocations(projectPath, ciAux, ASTRight);

                List<MyMethodDeclaration> rightBaseMethodDeclarations = rightBaseCCMethodDeclaration(projectPath, ciAux, ASTmergeBase);

                List<MyAttributeDeclaration> rightAttributeDeclarations = rightAttributes(projectPath, ciAux, ASTRight);
                List<MyAttributeCall> rightAttributeCalls = rightAttributeCalls(projectPath, ciAux, ASTRight);

                List<MyAttributeDeclaration> rightBaseAttributeDeclarations = rightBaseAttributes(projectPath, ciAux, ASTmergeBase);

                List<MyVariableDeclaration> rightVariableDeclarations = rightVariableDeclarations(projectPath, ciAux, ASTRight);
                List<MyVariableCall> rightVariableCalls = rightVariableCalls(projectPath, ciAux, ASTRight);

                List<MyVariableDeclaration> rightBaseVariableDeclarations = rightBaseVariableDeclarations(projectPath, ciAux, ASTmergeBase);

                boolean hasMethodDependency1 = hasMethodDependency(rightBaseMethodDeclarations, leftMethodInvocations);
                boolean hasMethodDependency2 = hasMethodDependency(leftBaseMethodDeclarations, rightMethodInvocations);

                if (hasMethodDependency1) {//left branch 
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ciAux, ci, DependencyType.METHOD_DECLARATION_INVOCATION, "Left");
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }
                if (hasMethodDependency2) { //right branch 
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ci, ciAux, DependencyType.METHOD_DECLARATION_INVOCATION, "Right");
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }

                boolean hasAttributeDepedency1 = hasAttributeDependency(rightBaseAttributeDeclarations, leftAttributeCalls);
                boolean hasAttributeDepedency2 = hasAttributeDependency(leftBaseAttributeDeclarations, rightAttributeCalls);

                if (hasAttributeDepedency1) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ciAux, ci, DependencyType.ATTRIBUTE_DECLARATION_USAGE, "Left");
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }

                if (hasAttributeDepedency2) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ci, ciAux, DependencyType.ATTRIBUTE_DECLARATION_USAGE, "Right");
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }

                boolean hasVariableDepedency1 = hasVariableDependency(rightBaseVariableDeclarations, leftVariableCalls);
                boolean hasVariableDepedency2 = hasVariableDependency(leftBaseVariableDeclarations, rightVariableCalls);

                if (hasVariableDepedency1) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    //if (columnNumber != rowNumber) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ciAux, ci, DependencyType.VARIABLE_DECLARATION_USAGE, "Left");
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                    //}
                }

                if (hasVariableDepedency2) {
                    ChunksDependency ChunksDependency
                            = new ChunksDependency(ci, ciAux, DependencyType.VARIABLE_DECLARATION_USAGE, "Right");
                    mergeDependency.getChunksDependencies().add(ChunksDependency);
                }

                //count number of modified methods
                List<MyMethodDeclaration> MethodDeclarationsRightAux = rightMethodDeclarations;
                //List<MyMethodInvocation> MethodInvocationsRightAux = rightMethodInvocations;

                for (int i = MethodDeclarationsRightAux.size() - 1; i > 0; i--) {
                    if (MethodDeclarationsRightAux.get(i).equals(MethodDeclarationsRightAux.get(i - 1))) {
                        MethodDeclarationsRightAux.remove(MethodDeclarationsRightAux.get(i));
                    }
                }

               /* for (int i = MethodInvocationsRightAux.size() - 1; i > 0; i--) {
                    if (MethodInvocationsRightAux.get(i).equals(MethodInvocationsRightAux.get(i - 1))) {
                        MethodInvocationsRightAux.remove(MethodInvocationsRightAux.get(i));
                    }
                }*/
                //ciAux.setchunks(MethodDeclarationsRightAux.size() + MethodInvocationsRightAux.size());
                ciAux.setchunks(MethodDeclarationsRightAux.size());

            }
        }
        for (ChunkInformation ciLeft : cisL) { 
            chunksLeft = chunksLeft + ciLeft.getChunks();
        }

        for (ChunkInformation ciRight : cisR) {
            chunksRight = chunksRight + ciRight.getChunks();
        }
        mergeDependency.setChunksAmount(chunksLeft + chunksRight);
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

    public static boolean sameBaseMethodInvocation(MyMethodInvocation methodInvocationLeft, MyMethodInvocation methodInvocation) {

        IMethodBinding methodInvocationLeftBinding = methodInvocationLeft.getMethodInvocation().resolveMethodBinding();
        IMethodBinding methodInvocationBinding = methodInvocation.getMethodInvocation().resolveMethodBinding();

        if (methodInvocationLeftBinding != null && methodInvocationBinding != null && methodInvocationLeftBinding.isEqualTo(methodInvocationBinding)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean sameBaseAttributeDeclaration(MyAttributeDeclaration leftAttributeDeclaration, MyAttributeDeclaration attributeDeclaration) {

        IVariableBinding attributeDeclarationLeftBinding = leftAttributeDeclaration.getFieldDeclaration().resolveBinding();
        IBinding attributeDeclarationBinding = attributeDeclaration.getFieldDeclaration().resolveBinding();

        if (attributeDeclarationLeftBinding != null && attributeDeclarationBinding != null && attributeDeclarationLeftBinding.isEqualTo(attributeDeclarationBinding)) {
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

        //IVariableBinding attributeDeclarationBinding = attributeDeclaration.getFieldDeclaration().resolveBinding();
        //IBinding attributeCallBinding = attributeCall.getSimpleName().resolveBinding();
        //IVariableBinding variableDeclarationBinding = variableDeclaration.resolveBinding();
        //IBinding variableCallBinding = variableCall.getSimpleName().resolveBinding();
        String attributeDeclarationBinding = attributeDeclaration.getFieldDeclaration().getName().getIdentifier();
        String attributeCallBindingAux = attributeCall.getSimpleName().getParent().toString();
        String attributeCallBinding = attributeCallBindingAux.substring(attributeCallBindingAux.lastIndexOf('.') + 1, attributeCallBindingAux.length());

        if (attributeDeclarationBinding != null && attributeCallBinding != null && attributeCallBinding.equals(attributeDeclarationBinding)) {
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

        IVariableBinding variableDeclarationBinding = variableDeclaration.resolveBinding();
        IBinding variableCallBinding = variableCall.getSimpleName().resolveBinding();

        if (variableDeclarationBinding != null && variableCallBinding != null && variableCallBinding.isEqualTo(variableDeclarationBinding)) {
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

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
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

    public static List<MyMethodDeclaration> leftBaseCCMethodDeclarations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTmergeBase) {

        List<MyMethodDeclaration> result = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathLeftBase() != null) {
            relativePath = ci.getRelativePathLeftBase();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTmergeBase) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    operationsBase = ci.getOperationsBase();
                    for (Operation operation : operationsBase) {
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

    public static List<MyAttributeDeclaration> leftBaseAttributes(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTmergeBase) {

        List<MyAttributeDeclaration> result = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathLeftBase() != null) {
            relativePath = ci.getRelativePathLeftBase();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTmergeBase) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeDeclaration> attributeDeclarations = AST.getAttributes();

                for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {
                    operationsBase = ci.getOperationsBase();
                    for (Operation operation : operationsBase) {
                        int line = operation.getLine();
                        if (leftHasIntersection(attributeDeclaration.getLocation(), ci, line)) {
                            result.add(attributeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean leftHasIntersection(Location location, ChunkInformation ci, int line) {

        if ((line >= location.getElementLineBegin()) && (line <= location.getElementLineEnd())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean leftHasIntersection(MyMethodDeclaration methodDeclaration, ChunkInformation ci, int line) {

        return leftHasIntersection(methodDeclaration.getLocation(), ci, line);

    }

    public static List<MyMethodDeclaration> rightCCMethodDeclaration(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(methodDeclaration, ci, line)) {
                            result.add(methodDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyMethodDeclaration> rightBaseCCMethodDeclaration(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTmergeBase) {

        List<MyMethodDeclaration> result = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathLeftBase() != null) {
            relativePath = ci.getRelativePathLeftBase();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTmergeBase) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodDeclaration> methodDeclarations = AST.getMethodDeclarations();

                for (MyMethodDeclaration methodDeclaration : methodDeclarations) {

                    operationsBase = ci.getOperationsBase();
                    for (Operation operation : operationsBase) {
                        int line = operation.getLine();
                        if (rightHasIntersection(methodDeclaration, ci, line)) {
                            result.add(methodDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean rightHasIntersection(Location location, ChunkInformation ci, int line) {

        if ((line >= location.getElementLineBegin()) && (line <= location.getElementLineEnd())) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean rightHasIntersection(MyMethodDeclaration methodDeclaration, ChunkInformation ci, int line) {

        return rightHasIntersection(methodDeclaration.getLocation(), ci, line);

    }

    public static List<MyMethodInvocation> leftCCMethodInvocations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodInvocation> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) { //PERCORRE

            if (containsPath(AST.getPath(), relativePath)) { //VRF SE É O MEMO FILE

                List<MyMethodInvocation> methodInvocations = AST.getMethodInvocations();

                for (MyMethodInvocation methodInvocation : methodInvocations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();

                        if (leftHasIntersection(methodInvocation.getLocation(), ci, line)) { //SE AS INVOCACOES DE METODO ESTAO DENTRO DO CCCI(CONFLICT CHUNK INFORMATUON
                            result.add(methodInvocation);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyMethodInvocation> rightCCMethodInvocations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodInvocation> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath = null;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyMethodInvocation> methodInvocations = AST.getMethodInvocations();

                for (MyMethodInvocation methodInvocation : methodInvocations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(methodInvocation.getLocation(), ci, line)) {
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
            ChunkInformation ci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAttributeDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeDeclaration> attributeDeclarations = AST.getAttributes();

                for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(attributeDeclaration.getLocation(), ci, line)) {
                            result.add(attributeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeDeclaration> rightAttributes(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTRight) {

        List<MyAttributeDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeDeclaration> attributeDeclarations = AST.getAttributes();

                for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(attributeDeclaration.getLocation(), ci, line)) {
                            result.add(attributeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeDeclaration> rightBaseAttributes(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTmergeBase) {

        List<MyAttributeDeclaration> result = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathLeftBase() != null) {
            relativePath = ci.getRelativePathLeftBase();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTmergeBase) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeDeclaration> attributeDeclarations = AST.getAttributes();

                for (MyAttributeDeclaration attributeDeclaration : attributeDeclarations) {

                    operationsBase = ci.getOperationsBase();
                    for (Operation operation : operationsBase) {
                        int line = operation.getLine();
                        if (rightHasIntersection(attributeDeclaration.getLocation(), ci, line)) {
                            result.add(attributeDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeCall> leftAttributeCalls(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAttributeCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeCall> attributeCalls = AST.getAttributeCalls();

                for (MyAttributeCall attributeCall : attributeCalls) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(attributeCall.getLocation(), ci, line)) {
                            result.add(attributeCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeCall> rightAttributeCalls(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTRight) {

        List<MyAttributeCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyAttributeCall> attributeCalls = AST.getAttributeCalls();

                for (MyAttributeCall attributeCall : attributeCalls) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(attributeCall.getLocation(), ci, line)) {
                            result.add(attributeCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> leftVariableDeclarations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTLeft) {

        List<MyVariableDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();
        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableDeclaration> variableDeclarations = AST.getVariableDeclarations();

                for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(variableDeclaration.getLocation(), ci, line)) {
                            result.add(variableDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> leftBaseVariableDeclarations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTmergeBase) {

        List<MyVariableDeclaration> result = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();
        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathLeftBase() != null) {
            relativePath = ci.getRelativePathLeftBase();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTmergeBase) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableDeclaration> variableDeclarations = AST.getVariableDeclarations();

                for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
                    operationsBase = ci.getOperationsBase();
                    for (Operation operation : operationsBase) {
                        int line = operation.getLine();
                        if (leftHasIntersection(variableDeclaration.getLocation(), ci, line)) {
                            result.add(variableDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> rightVariableDeclarations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTRight) {

        List<MyVariableDeclaration> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableDeclaration> variableDeclarations = AST.getVariableDeclarations();

                for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        //repositionBase = ci.getReposition();
                        //for (int line : repositionBase) {
                        if (rightHasIntersection(variableDeclaration.getLocation(), ci, line)) {
                            result.add(variableDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> rightBaseVariableDeclarations(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTmergeBase) {

        List<MyVariableDeclaration> result = new ArrayList<>();
        List<Operation> operationsBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathLeftBase() != null) {
            relativePath = ci.getRelativePathLeftBase();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTmergeBase) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableDeclaration> variableDeclarations = AST.getVariableDeclarations();

                for (MyVariableDeclaration variableDeclaration : variableDeclarations) {
                    operationsBase = ci.getOperationsBase();
                    for (Operation operation : operationsBase) {
                        int line = operation.getLine();
                        if (rightHasIntersection(variableDeclaration.getLocation(), ci, line)) {
                            result.add(variableDeclaration);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableCall> leftVariableCalls(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTLeft) {

        List<MyVariableCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTLeft) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableCall> variableCalls = AST.getVariableCalls();

                for (MyVariableCall variableCall : variableCalls) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (leftHasIntersection(variableCall.getLocation(), ci, line)) {
                            result.add(variableCall);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableCall> rightVariableCalls(String projectPath,
            ChunkInformation ci, List<ClassLanguageContructs> ASTRight) {

        List<MyVariableCall> result = new ArrayList<>();
        List<Integer> repositionBase = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        String relativePath;

        if (ci.isRenamed() && ci.getRelativePathRight() != null) {
            relativePath = ci.getRelativePathRight();
        } else {
            relativePath = ci.getFilePath().replace(projectPath, "");
        }

        for (ClassLanguageContructs AST : ASTRight) {

            if (containsPath(AST.getPath(), relativePath)) {

                List<MyVariableCall> variableCalls = AST.getVariableCalls();

                for (MyVariableCall variableCall : variableCalls) {
                    //repositionBase = ci.getReposition();
                    //for (int line : repositionBase) {
                    operations = ci.getOperations();
                    for (Operation operation : operations) {
                        int line = operation.getLine();
                        if (rightHasIntersection(variableCall.getLocation(), ci, line)) {
                            result.add(variableCall);
                        }
                    }
                }
            }
        }

        return result;
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
