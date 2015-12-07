/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.javaparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 *
 * @author gleiph
 */
public class JavaParser {

    private Storage generateASTs(String path) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);

        Map options = JavaCore.getOptions();
        
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        parser.setCompilerOptions(options);
        
        String[] srcDirs = FileUtils.getAllDirs(path);
        String[] javaFiles = FileUtils.getAllJavaFiles(path);

        parser.setEnvironment(null, srcDirs, null, true);

        Storage storage = new Storage();
        parser.createASTs(javaFiles, null, new String[0], storage, null);
        return storage;
    }

    public List<ClassLanguageContructs> parser(String path) {
        List<ClassLanguageContructs> classesLanguageConstructs = new ArrayList<>();

        JavaParser javaParser = new JavaParser();
        Storage ASTs = javaParser.generateASTs(path);

        Set<String> classes = ASTs.keys();

        for (String classPath : classes) {
            CompilationUnit cu = ASTs.get(classPath);

            DepVisitor depVisitor = new DepVisitor(cu, classPath);
            cu.accept(depVisitor);

            for (ClassLanguageContructs classLanguageConstructs : depVisitor.getClassesLanguageConstructs()) {
                String qualifiedName = classLanguageConstructs.getQualifiedName();

                if (qualifiedName != null) {

                    ClassLanguageContructs clc = new ClassLanguageContructs(qualifiedName, classPath);

                    clc.setMethodDeclarations(classLanguageConstructs.getMethodDeclarations());
                    clc.setMethodInvocations(classLanguageConstructs.getMethodInvocations());
                    clc.setAttributes(classLanguageConstructs.getAttributes());
                    clc.setAttributeCalls(classLanguageConstructs.getAttributeCalls());
                    clc.setVariableDeclarations(classLanguageConstructs.getVariableDeclarations());
                    clc.setVariableCalls(classLanguageConstructs.getVariableCalls());
                    clc.setTypeDeclarations(classLanguageConstructs.getTypeDeclarations());
                    clc.setAnnotationDeclarations(classLanguageConstructs.getAnnotationDeclarations());
                    
                    classesLanguageConstructs.add(clc);
                }

            }

        }

        return classesLanguageConstructs;
    }

}
