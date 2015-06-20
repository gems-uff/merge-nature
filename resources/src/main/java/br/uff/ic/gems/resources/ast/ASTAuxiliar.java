/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.ast;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.LanguageConstruct;
import br.uff.ic.gems.resources.repositioning.Repositioning;
import br.uff.ic.gems.resources.utils.Context;
import br.uff.ic.gems.resources.utils.ContextFinder;
import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gleiph
 */
public class ASTAuxiliar {

    public static void cloneRepositories(String sourceRepository, String leftRepository, String rightRepository, String developerMergeRepositoryPath) {
        if (!new File(developerMergeRepositoryPath).isDirectory()) {
            System.out.println("Cloning Developer merge repository...");
            Git.clone(sourceRepository, sourceRepository, developerMergeRepositoryPath);
        }

        if (!new File(leftRepository).isDirectory()) {
            System.out.println("Cloning left reporitory...");
            Git.clone(sourceRepository, sourceRepository, leftRepository);
        }
        if (!new File(rightRepository).isDirectory()) {
            System.out.println("Cloning rifht repository...");
            Git.clone(sourceRepository, sourceRepository, rightRepository);
        }
    }

    public static int getConflictUpperBound(ConflictingChunk conflictArea, int context, List<String> fileConflict) {
        int conflictingUpperBound;
        conflictingUpperBound = conflictArea.getEndLine() + context + 1;////I don't want do exclude the last line
        if (conflictingUpperBound > fileConflict.size()) {
            conflictingUpperBound = fileConflict.size();
        }
        return conflictingUpperBound;
    }

    public static int getConflictLowerBound(ConflictingChunk conflictArea, int context) {
        int conflictLowerBound;
        conflictLowerBound = conflictArea.getBeginLine() - context;
        if (conflictLowerBound < 0) {
            conflictLowerBound = 0;
        }
        return conflictLowerBound;
    }

    @Deprecated
    public static KindConflict getLanguageConstructs(List<String> conflictArea, String repository, String relativePath) throws IOException {

        KindConflict kindConflict = new KindConflict();

        List<String> conflict = conflictArea;
        String file = repository + relativePath;
        List<String> fileList = FileUtils.readLines(new File(file));
        List<Integer> beginList = new ArrayList<>();

        for (Context b : ContextFinder.getBegin(conflict, fileList)) {
            beginList.add(b.getLineNumber());
        }

        int begin = 0;
        if (beginList.size() == 1) {
            begin = beginList.get(0) + 1;//transform in the real line (begining from 1)
        }
        int end = begin + conflict.size() - 1;

        //Dealing with AST
        ASTExtractor ats = new ASTExtractor(file);
        ats.parser();
        List<LanguageConstruct> languageConstructs = ats.getLanguageConstructs(begin, end);

        kindConflict.setBeginLine(begin);
        kindConflict.setEndLine(end);
        kindConflict.setLanguageConstructs(languageConstructs);

        return kindConflict;
    }

    public static KindConflict getLanguageConstructsJava(int begin, int end, String currentRepository, String currentFile, String baseFile) throws IOException {

        KindConflict kindConflict = new KindConflict();

        Repositioning repositioning = new Repositioning(currentRepository);
        int beginRepositioned = repositioning.repositioning(currentFile, baseFile, begin);
        int endRepositioned = repositioning.repositioning(currentFile, baseFile, end);

        //Dealing with AST
        ASTExtractor ats = new ASTExtractor(baseFile);
        ats.parser();
        List<LanguageConstruct> languageConstructs = ats.getLanguageConstructs(beginRepositioned, endRepositioned);

        kindConflict.setBeginLine(beginRepositioned);
        kindConflict.setEndLine(endRepositioned);
        kindConflict.setLanguageConstructs(languageConstructs);

        return kindConflict;
    }

    public static KindConflict getLanguageConstructsAny(int begin, int end, String currentRepository, String baseRepository, String relativePath) {

        KindConflict kindConflict = new KindConflict();

        String currentFile = currentRepository + relativePath;
        String baseFile = baseRepository + relativePath;

        int beginRepositioned = 0, endRepositioned = 0, fileSize = 0;
        List<String> file;

        try {
            Repositioning repositioning = new Repositioning(currentRepository);
            beginRepositioned = repositioning.repositioning(currentFile, baseFile, begin);
            endRepositioned = repositioning.repositioning(currentFile, baseFile, end);
            file = FileUtils.readLines(new File(baseFile));
            fileSize = file.size();
        } catch (Exception e) {
            beginRepositioned = begin;
            endRepositioned = end;
        }


        //Dealing with AST
        List<LanguageConstruct> languageConstructs = new ArrayList<>();

        String[] fileBroken = relativePath.split("\\.");
        LanguageConstruct languageConstruct = new LanguageConstruct(fileBroken[fileBroken.length - 1], 0, fileSize);
        languageConstructs.add(languageConstruct);

        kindConflict.setBeginLine(beginRepositioned);
        kindConflict.setEndLine(endRepositioned);
        kindConflict.setLanguageConstructs(languageConstructs);

        return kindConflict;
    }
}
