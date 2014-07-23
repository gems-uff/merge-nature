/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github;

import br.uff.ic.github.github.parser.GithubAPI;

/**
 *
 * @author Gleiph
 */
public class Main {

    public static void main(String[] args) {

        String username = null;
        String password = null;
        String reportPath = null;
        int since = 0;

        if (args.length < 3) {
            System.out.println("At least 3 parameters are expected! (1) username, (2) password, (3) path to save the report, and (4) last page");
        }

        if (args.length >= 3) {
            username = args[0];
            password = args[1];
            reportPath = args[2];

            if (args.length == 4) {
                since = Integer.parseInt(args[3]);
            }
            
        }

        GithubAPI.init(username, password);
        GithubAPI.projects(since, reportPath);
    }
}
