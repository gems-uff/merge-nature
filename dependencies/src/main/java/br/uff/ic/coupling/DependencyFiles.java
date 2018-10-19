
package br.uff.ic.coupling;

import br.uff.ic.gems.resources.cmd.CMD;
import br.uff.ic.gems.resources.cmd.CMDOutput;
import br.uff.ic.gems.resources.utils.MergeStatusAnalizer;
import br.uff.ic.gems.resources.vcs.Git;
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
 * @author Cristiane
 */
public class DependencyFiles {
   
/**
 *
 * @author Cristiane
 */
    
    /*mergeSHA = d24ff38abf6c227f25a29cd12bb833ee0af785bc, 
    leftSHA = de8b15157dc728e93c3d8a4482782bd44dede72f, 
    rightSHA = ab998796053e692d5f1336f267d43310a0a53fda
    mergebase = 883024ca7f05582b02e9874f52730c8fbe0a5dd8 */
    
    public static void main(String[] args) {
        List<String> projectsPath = new ArrayList<>();
        projectsPath.add("C:\\Cristiane\\mestrado\\aXMLRPC");
      
        String sandbox = "C:\\Cristiane\\mestrado\\sandbox";
        
        for (String projectPath : projectsPath) {
            System.out.println("Project: " + projectPath);
            analyze(projectPath, sandbox);
        }

    }

     public static void analyze(String projectPath, String sandbox) {
        List<String> mergeRevisions = Git.getMergeRevisions(projectPath);
        int hasDependencies = 0, hasNoDependencies = 0, oneCC = 0, moreThanOneCC = 0;

        int Chunks, dependecies/*, files, filesDepedency*/;
        String project, mergeSHA, leftSHA, rightSHA, mergebaseSHA ;

        project = projectPath;

        //for (String mergeRevision : mergeRevisions) {

            //mergeSHA = mergeRevision;

            //List<String> parents = Git.getParents(projectPath, mergeRevision);

          //if (parents.size() == 2) {
                //leftSHA = parents.get(0);
                ///rightSHA = parents.get(1);
               
                //leftSHA = "de8b15157dc728e93c3d8a4482782bd44dede72f"; OctoUML
                //rightSHA = "ab998796053e692d5f1336f267d43310a0a53fda";
                
                leftSHA = "2e7a74f72bafdf330b2086146a49e4c430d15d0c"; //aXMLRPC
                rightSHA = "8211a954be87f8a553a9fdeaf2ab457db69c005d";
                
                mergebaseSHA = Git.getMergeBase(projectPath, leftSHA, rightSHA);
                
                //List<String> diffLeft = diff(projectPath, mergebaseSHA, leftSHA);
                
                //List<String> diffRight = diff(projectPath, mergebaseSHA, rightSHA);
                
                
                //Fazer diff entre o merge base e cada uma das duas cabeças e fazer um parser do resultado para pegar o nome dos arquivos.
             
                MergeDependency mergeDependency;
                try {
//                    System.out.println("\tMerging " + SHALeft + " and " + SHARight);
                   mergeDependency = performMerge(projectPath, mergebaseSHA, leftSHA, rightSHA, sandbox); 
                   
                  //mergeDependency = performMerge(projectPath, diffLeft, diffRight, leftSHA, rightSHA, sandbox);
                    
                    //Treating dependencies 
                    if (mergeDependency == null) {
                        Chunks = 0;
                        dependecies = 0;
                    } else {
                        Chunks = mergeDependency.getChunksAmount();
                        dependecies = mergeDependency.getChunksDependencies().size();
                    }

                    System.out.println(project + ", " + Chunks + ", " + dependecies + ", " + mergebaseSHA + ", " + leftSHA + ", " + rightSHA);
                } catch (IOException ex) {
                    System.out.println("Merge between revisions " + leftSHA + " and " + rightSHA + " was not performed.");

                }

            //}
        //}
        System.out.println("hasNoDependencies = " + hasNoDependencies);
        System.out.println("hasDependencies = " + hasDependencies);
        System.out.println("moreThanOneCC = " + moreThanOneCC);
        System.out.println("oneCC = " + oneCC);
    }
     
     public static MergeDependency performMerge(String projectPath, String SHAMergebase, String SHALeft, String SHARight, String sandbox) throws IOException {
        //if (isFailedMerge(projectPath, SHALeft, SHARight)) {

            List<String> chunksFilePaths = chunkFiles(projectPath);
            
            //List<String> diffLeft = diffStat(projectPath, mergebaseSHA, SHALeft);
                
            //List<String> diffRight = diffStat(projectPath, mergebaseSHA, SHARight);

            //Getting conflicting chunks
            List<ChunkInformation> cis = ChunkInformation.extractChunksInformation(chunksFilePaths);

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

            //System.out.println("Repositioning...");
            //repositioningChunksInformation(cis, repositoryLeft, projectPath, repositoryRight);

            //Creating depedency matrix
            System.out.println("Extracting dependency matrix...");
            MergeDependency mergeDependency = extractDepedencies(cis, projectPath, ASTLeft, ASTRight);
            mergeDependency.setCis(cis);

            return mergeDependency;
        //}

       // return null;
    }

    public static List<String> chunkFiles(String repositoryPath) {

        if(!repositoryPath.endsWith(File.separator))
            repositoryPath += File.separator;
        
        List<String> statusShort = Git.statusShort(repositoryPath);
        List<String> files = new ArrayList<String>();
        
        String lineChanged;
                
        for (String line : statusShort) {
            if (line.startsWith("M ")) { 
                lineChanged = line.replaceFirst("M ", "");
                
            if (line.startsWith("R ")) 
                lineChanged = line.replaceFirst("R ", "");
                
            if (line.startsWith("UU")) 
                lineChanged = line.replaceFirst("UU", "");
                
            if (line.startsWith("A ")) 
                lineChanged = line.replaceFirst("A ", "");
                
            if (line.startsWith("D ")) 
                lineChanged = line.replaceFirst("D ", "");
                
            if (line.startsWith("C ")) 
                lineChanged = line.replaceFirst("C ", "");
                

                while (lineChanged.startsWith(" ")) {
                    lineChanged = lineChanged.replaceFirst(" ", "");
                    lineChanged = repositoryPath + lineChanged;
                }

                files.add(lineChanged);
            }
        }

        return files;
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
    
 public static void repositioningChunksInformation(List<ChunkInformation> cis, String repositoryLeft, String projectPath, String repositoryRight) {
        //Repositioning conflicting chunk information to both sides
        for (ChunkInformation ci : cis) {

            String baseFilePath = ci.getFilePath();
            String leftFilePath;

            if (ci.isRenamed() && ci.getRelativePathLeft() != null) {
                leftFilePath = repositoryLeft + File.separator
                        + ci.getRelativePathLeft();
            } else {
                leftFilePath = repositoryLeft + File.separator
                        + baseFilePath.replace(projectPath, "");
            }

            String rightFilePath;

            if (ci.isRenamed() && ci.getRelativePathRight() != null) {
                rightFilePath = repositoryRight + File.separator + ci.getRelativePathRight();
            } else {
                rightFilePath = repositoryRight + File.separator
                        + baseFilePath.replace(projectPath, "");
            }

            ci.reposition(baseFilePath, leftFilePath, rightFilePath);

        }
    }

    public static MergeDependency extractDepedencies(List<ChunkInformation> cis, String projectPath,
            List<ClassLanguageContructs> ASTLeft, List<ClassLanguageContructs> ASTRight) {

        MergeDependency mergeDependency = new MergeDependency();
        mergeDependency.setChunksAmount(cis.size()); //qtd de arquivos, não serveria

        for (ChunkInformation ci : cis) {

            //Find method declaration that has some intersection with a method declaration
            List<MyMethodDeclaration> leftMethodDeclarations = leftCCMethodDeclarations(projectPath, ci, ASTLeft);
            List<MyMethodDeclaration> rightMethodDeclarations = rightCCMethodDeclaration(projectPath, ci, ASTRight);

            //Find attribute declarations
            List<MyAttributeDeclaration> leftAttributeDeclarations = leftAttributes(projectPath, ci, ASTLeft);
            List<MyAttributeDeclaration> rightAttributeDeclarations = rightAttributes(projectPath, ci, ASTRight);

            //Find variable declarations
            List<MyVariableDeclaration> leftVariableDeclarations = leftVariableDeclarations(projectPath, ci, ASTLeft);
            List<MyVariableDeclaration> rightVariableDeclarations = rightVariableDeclarations(projectPath, ci, ASTRight);

            //Finding Type declarations
            List<MyTypeDeclaration> leftTypeDeclarations = leftTypeDeclarations(projectPath, ci, ASTLeft);
            List<MyTypeDeclaration> rightTypeDeclarations = rightTypeDeclarations(projectPath, ci, ASTRight);

            //Find Annotation declarations
            List<MyAnnotationDeclaration> leftAnnotationDeclarations = leftAnnotationDeclarations(projectPath, ci, ASTLeft);
            List<MyAnnotationDeclaration> rightAnnotationDeclarations = rightAnnotationDeclarations(projectPath, ci, ASTRight);

            int rowNumber = cis.indexOf(ci);

            for (ChunkInformation ciAux : cis) {
                int columnNumber = cis.indexOf(ciAux); // PASSAR OS CHUNKS AKI

                List<MyMethodInvocation> leftMethodInvocations = leftCCMethodInvocations(projectPath, ciAux, ASTLeft);
                List<MyMethodInvocation> rightMethodInvocations = rightCCMethodInvocations(projectPath, ciAux, ASTRight);

                List<MyAttributeCall> leftAttributeCalls = leftAttributeCalls(projectPath, ciAux, ASTLeft);
                List<MyAttributeCall> rightAttributeCalls = rightAttributeCalls(projectPath, ciAux, ASTRight);

                List<MyVariableCall> leftVariableCalls = leftVariableCalls(projectPath, ciAux, ASTLeft);
                List<MyVariableCall> rightVariableCalls = rightVariableCalls(projectPath, ciAux, ASTRight);

                List<MyTypeDeclaration> leftTypeDeclarationsAux = leftTypeDeclarations(projectPath, ci, ASTLeft);
                List<MyTypeDeclaration> rightTypeDeclarationsAux = rightTypeDeclarations(projectPath, ci, ASTRight);

                List<MyAnnotationUsage> leftAnnotationUsages = leftAnnotationUsages(projectPath, ci, ASTLeft);
                List<MyAnnotationUsage> rightAnnotationUsages = rightAnnotationUsages(projectPath, ci, ASTRight);

                boolean hasMethodDependecy
                        = hasMethodDependency(leftMethodDeclarations, leftMethodInvocations)
                        || hasMethodDependency(rightMethodDeclarations, rightMethodInvocations);

                if (hasMethodDependecy) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    if (columnNumber != rowNumber) {
                        ChunksDependency ChunksDependency
                                = new ChunksDependency(ciAux, ci, DependencyType.METHOD_DECLARATION_INVOCATION);
                        mergeDependency.getChunksDependencies().add(ChunksDependency);
                    }
                }

                boolean hasAttributeDepedency
                        = hasAttributeDependency(leftAttributeDeclarations, leftAttributeCalls)
                        || hasAttributeDependency(rightAttributeDeclarations, rightAttributeCalls);

                if (hasAttributeDepedency) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    if (columnNumber != rowNumber) {
                        ChunksDependency ChunksDependency
                                = new ChunksDependency(ciAux, ci, DependencyType.ATTRIBUTE_DECLARATION_USAGE);
                        mergeDependency.getChunksDependencies().add(ChunksDependency);
                    }
                }

                boolean hasVariableDepedency
                        = hasVariableDependency(leftVariableDeclarations, leftVariableCalls)
                        || hasVariableDependency(rightVariableDeclarations, rightVariableCalls);

                if (hasVariableDepedency) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    if (columnNumber != rowNumber) {
                        ChunksDependency ChunksDependency
                                = new ChunksDependency(ciAux, ci, DependencyType.VARIABLE_DECLARATION_USAGE);
                        mergeDependency.getChunksDependencies().add(ChunksDependency);
                    }
                }

                boolean hasDependencyTypeDeclarationInterface
                        = hasDependencyTypeDeclarationInterface(leftTypeDeclarations, leftTypeDeclarationsAux)
                        || hasDependencyTypeDeclarationInterface(rightTypeDeclarations, rightTypeDeclarationsAux);

                if (hasDependencyTypeDeclarationInterface) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    if (columnNumber != rowNumber) {
                        ChunksDependency ChunksDependency
                                = new ChunksDependency(ciAux, ci, DependencyType.TYPE_DECLARATION_INTERFACE);
                        mergeDependency.getChunksDependencies().add(ChunksDependency);
                    }
                }

                boolean hasAnnotationDependency
                        = hasDependencyAnnotationDeclarationUsage(leftAnnotationDeclarations, leftAnnotationUsages)
                        || hasDependencyAnnotationDeclarationUsage(rightAnnotationDeclarations, rightAnnotationUsages);

                if (hasAnnotationDependency) {
                    //CC(rowNumber) depends on CC(ColumnNumber)
                    if (columnNumber != rowNumber) {
                        ChunksDependency ChunksDependency
                                = new ChunksDependency(ciAux, ci, DependencyType.ANNOTATION_DECLARATION_USAGE);
                        mergeDependency.getChunksDependencies().add(ChunksDependency);
                    }
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

        if (methodDeclarationBinding != null && methodInvocationBinding != null && methodDeclarationBinding.equals(methodInvocationBinding)) {
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

        if (attributeDeclarationBinding != null && attributeCallBinding != null && attributeDeclarationBinding.equals(attributeCallBinding)) {
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

    public static void printChunksInformation(List<ChunkInformation> cis) throws IOException {
        for (ChunkInformation ci : cis) {
            List<String> fileLines = FileUtils.readLines(new File(ci.getFilePath()));

            List<String> ccLines = fileLines.subList(ci.getBegin(), ci.getEnd() + 1);

            System.out.println(ci.toString());
            for (String ccLine : ccLines) {
                System.out.println("\t\t" + ccLine);
            }
        }
    }

    public static boolean isFailedMerge(String projectPath, String SHALeft, String SHARight) {
        Git.reset(projectPath);
        Git.clean(projectPath);
        Git.checkout(projectPath, SHALeft);
        List<String> merge = Git.merge(projectPath, SHARight, false, false);

        return MergeStatusAnalizer.isConflict(merge); //verificar
    }

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
                    if (leftHasIntersection(methodDeclaration, ci)) {
                        result.add(methodDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static boolean leftHasIntersection(Location location, ChunkInformation cci) {

        if (cci.getLeftBegin() > location.getElementLineEnd()) {
            return false;
        } else if (cci.getLeftEnd() < location.getElementLineBegin()) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean leftHasIntersection(MyMethodDeclaration methodDeclaration, ChunkInformation cci) {

        return leftHasIntersection(methodDeclaration.getLocation(), cci);

    }

    public static List<MyMethodDeclaration> rightCCMethodDeclaration(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodDeclaration> result = new ArrayList<>(); 

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
                    if (rightHasIntersection(methodDeclaration, cci)) {
                        result.add(methodDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static boolean rightHasIntersection(Location location, ChunkInformation cci) {

        if (cci.getRightBegin() > location.getElementLineEnd()) {
            return false;
        } else if (cci.getRightEnd() < location.getElementLineBegin()) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean rightHasIntersection(MyMethodDeclaration methodDeclaration, ChunkInformation cci) {

        return rightHasIntersection(methodDeclaration.getLocation(), cci);

    }

    public static List<MyMethodInvocation> leftCCMethodInvocations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyMethodInvocation> result = new ArrayList<>();

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
                    if (leftHasIntersection(methodInvocation.getLocation(), cci)) { //SE AS INVOCACOES DE METODO ESTAO DENTRO DO CCCI(CONFLICT CHUNK INFORMATUON
                        result.add(methodInvocation);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyMethodInvocation> rightCCMethodInvocations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyMethodInvocation> result = new ArrayList<>();

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

    public static List<MyAttributeDeclaration> leftAttributes(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAttributeDeclaration> result = new ArrayList<>();

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
                    if (leftHasIntersection(attributeDeclaration.getLocation(), cci)) {
                        result.add(attributeDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeDeclaration> rightAttributes(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAttributeDeclaration> result = new ArrayList<>();

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
                    if (rightHasIntersection(attributeDeclaration.getLocation(), cci)) {
                        result.add(attributeDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeCall> leftAttributeCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAttributeCall> result = new ArrayList<>();

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
                    if (leftHasIntersection(attributeCall.getLocation(), cci)) {
                        result.add(attributeCall);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAttributeCall> rightAttributeCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAttributeCall> result = new ArrayList<>();

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
                    if (rightHasIntersection(attributeCall.getLocation(), cci)) {
                        result.add(attributeCall);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> leftVariableDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyVariableDeclaration> result = new ArrayList<>();

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
                    if (leftHasIntersection(variableDeclaration.getLocation(), cci)) {
                        result.add(variableDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableDeclaration> rightVariableDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyVariableDeclaration> result = new ArrayList<>();

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
                    if (rightHasIntersection(variableDeclaration.getLocation(), cci)) {
                        result.add(variableDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableCall> leftVariableCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyVariableCall> result = new ArrayList<>();

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
                    if (leftHasIntersection(variableCall.getLocation(), cci)) {
                        result.add(variableCall);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyVariableCall> rightVariableCalls(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyVariableCall> result = new ArrayList<>();

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
                    if (rightHasIntersection(variableCall.getLocation(), cci)) {
                        result.add(variableCall);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyTypeDeclaration> leftTypeDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyTypeDeclaration> result = new ArrayList<>();

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
                    if (leftHasIntersection(typeDeclaration.getLocation(), cci)) {
                        result.add(typeDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyTypeDeclaration> rightTypeDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyTypeDeclaration> result = new ArrayList<>();

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
                    if (rightHasIntersection(typeDeclaration.getLocation(), cci)) {
                        result.add(typeDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationDeclaration> leftAnnotationDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAnnotationDeclaration> result = new ArrayList<>();

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
                    if (leftHasIntersection(annotationDeclaration.getLocation(), cci)) {
                        result.add(annotationDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationDeclaration> rightAnnotationDeclarations(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAnnotationDeclaration> result = new ArrayList<>();

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
                    if (rightHasIntersection(annotationDeclaration.getLocation(), cci)) {
                        result.add(annotationDeclaration);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationUsage> leftAnnotationUsages(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTLeft) {

        List<MyAnnotationUsage> result = new ArrayList<>();

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
                    if (leftHasIntersection(annotationUsage.getLocation(), cci)) {
                        result.add(annotationUsage);
                    }
                }
            }
        }

        return result;
    }

    public static List<MyAnnotationUsage> rightAnnotationUsages(String projectPath,
            ChunkInformation cci, List<ClassLanguageContructs> ASTRight) {

        List<MyAnnotationUsage> result = new ArrayList<>();

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
                    if (rightHasIntersection(annotationUsage.getLocation(), cci)) {
                        result.add(annotationUsage);
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
