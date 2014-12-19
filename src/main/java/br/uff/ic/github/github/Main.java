package br.uff.ic.github.github;

import br.uff.ic.github.github.parser.GithubAPI;

/**
 *
 * @author Gleiph
 */
public class Main {

    public static void main(String[] args) {

        GithubAPI.init();
        GithubAPI.projects();

    }
}
