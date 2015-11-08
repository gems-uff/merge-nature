/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.javaparser;

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

        Map<?, ?> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
        parser.setCompilerOptions(options);

        String[] srcDirs = FileUtils.getAllDirs(path);
        String[] javaFiles = FileUtils.getAllJavaFiles(path);

        parser.setEnvironment(null, srcDirs, null, true);

        Storage storage = new Storage();
        parser.createASTs(javaFiles, null, new String[0], storage, null);
        return storage;
    }

    public List<LanguageConstructsByClass> parser(String path) {
        List<LanguageConstructsByClass> languageConstructs = new ArrayList<>();

        JavaParser javaParser = new JavaParser();
        Storage ASTs = javaParser.generateASTs(path);

        Set<String> classes = ASTs.keys();

        for (String clazz : classes) {
            CompilationUnit cu = ASTs.get(clazz);

            DepVisitor depVisitor = new DepVisitor(cu);
            cu.accept(depVisitor);

            String className = depVisitor.getClassName();
            if (className != null) {

                LanguageConstructsByClass languageConstructsByClass = new LanguageConstructsByClass(className);

                languageConstructsByClass.setMethodDeclarations(depVisitor.getMethodDeclarations());
                languageConstructsByClass.setMethoInvocations(depVisitor.getMethodInvocations());

                languageConstructs.add(languageConstructsByClass);
            }

        }

        return languageConstructs;
    }

//    public static void main(String[] args) {
//
//        String projectPath = "/Users/gleiph/Dropbox/doutorado/repositories/lombok";
//
//        JavaParser javaParser = new JavaParser();
//        Storage ASTs = javaParser.generateASTs(projectPath);
//
//        Set<String> classes = ASTs.keys();
//
//        for (String clazz : classes) {
//            CompilationUnit cu = ASTs.get(clazz);
//
//            DepVisitor depVisitor = new DepVisitor(cu);
//            cu.accept(depVisitor);
//
//            String className = depVisitor.getClassName();
//            if (className != null) {
//            }
//
//        }
//
//    }

}
