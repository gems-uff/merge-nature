/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 *
 * @author gleiph
 */
public class Quickstart {

    public static void main(String args[]) {
        try {

            ASTParser parser = ASTParser.newParser(AST.JLS3);
//            File file = new File("/Users/gleiph/Dropbox/UCI/MergeViewer/src/main/java/br/uff/ic/github/mergeviewer/util/ContextFinder.java");
//            File file = new File("/Users/gleiph/Dropbox/UCI/MergeViewer/src/main/java/br/uff/ic/github/mergeviewer/processing/Understanding.java");
            File file = new File("/Users/gleiph/Dropbox/UCI/MergeViewer/src/test/java/br/uff/ic/github/mergeviewer/test/Structures.java");

            String stringFile = FileUtils.readFileToString(file);
            parser.setSource(stringFile.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);

            final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
            cu.accept(new Visitor(cu));

            List<Comment> commentList = cu.getCommentList();

//            for (Comment comment : commentList) {
//
//                comment.accept(new Visitor(cu));
//
//                int begin = cu.getLineNumber(comment.getStartPosition());
//                int end = cu.getLineNumber(comment.getStartPosition() + comment.getLength());
//
//                System.out.println("Comment(" + begin + ", " + end + ")");
//                System.out.println(comment.toString());
//            }

        } catch (IOException ex) {
            Logger.getLogger(Quickstart.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
