/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.mergeviewer.ast;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class Quickstart {

    public static void main(String args[]) {
//        try {
//            String file = "/Users/gleiph/Dropbox/UCI/MergeViewer/src/main/java/br/uff/ic/github/mergeviewer/util/ContextFinder.java";
//            String file = "/Users/gleiph/Dropbox/UCI/MergeViewer/src/main/java/br/uff/ic/github/mergeviewer/processing/Understanding.java";
        String file = "/Users/gleiph/Dropbox/UCI/MergeViewer/src/test/java/br/uff/ic/github/mergeviewer/test/Structures.java";

        try {

            ASTExtractor aSTExtractor = new ASTExtractor(file);
            aSTExtractor.parser();
            List<String> extractor = aSTExtractor.getStructures(91, 98);

            aSTExtractor.print(extractor);

        } catch (IOException ex) {
            Logger.getLogger(Quickstart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
