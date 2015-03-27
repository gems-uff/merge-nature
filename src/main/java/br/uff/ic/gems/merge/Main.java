/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.merge;

import br.uff.ic.gems.merge.diff.translator.GitTranslator;
import br.uff.ic.gems.merge.vcs.Git;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class Main {

    public static void main(String[] args) {

        String initialVersion = "163ec98";
        String finalVersion = "e3fc04bd";
        String filePath = "/Users/gleiph/Dropbox/UCI/Repositories/antlr4/tool/src/org/antlr/v4/tool/ErrorType.java";
        String repositoryPath = "/Users/gleiph/Dropbox/UCI/Repositories/antlr4";

        Git git = new Git(repositoryPath);
        List<String> diff = git.fileDiff(initialVersion, finalVersion, filePath);

        
    }

}
