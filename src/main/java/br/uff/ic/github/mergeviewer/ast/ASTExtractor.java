/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import br.uff.ic.github.mergeviewer.ast.data.SourceCodeFile;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 *
 * @author gleiph
 */
public class ASTExtractor {

    String filePath;
    SourceCodeFile sourceCode;

    public ASTExtractor(String filePath) {
        this.filePath = filePath;
    }

    public ASTExtractor() {
    }

    public void parser() throws IOException {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        File file = new File(filePath);

        String stringFile = FileUtils.readFileToString(file);
        parser.setSource(stringFile.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        Visitor visitor = new Visitor(cu);
        cu.accept(visitor);

        List<Comment> commentList = cu.getCommentList();

        for (Comment comment : commentList) {
            comment.accept(visitor);
        }

        sourceCode = visitor.getSourceCode();

    }

    public List<String> getStructures(int begin, int end) {
        List<String> kindConflic = sourceCode.getKindConflict(begin, end);
        Collections.sort(kindConflic);

        return kindConflic;
    }

    public void print(List<String> input) {

        for (int i = 0; i < input.size() - 1; i++) {
            System.out.print(input.get(i) + ", ");
        }

        System.out.println(input.get(input.size() - 1));

    }
}
