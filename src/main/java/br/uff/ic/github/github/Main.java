/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github;

import br.uff.ic.github.github.data.Language;
import br.uff.ic.github.github.parser.GithubAPI;
import java.util.List;

/**
 *
 * @author Gleiph
 */
public class Main {

    public static void main(String[] args) {

        String projectUrl = "https://api.github.com/repos/mojombo/grit/commits?per_page=10000";

        //        "html_url": "https://github.com/mojombo/grit/commit/2fb7d5d232412fd932b1a3c694f92880add7ba9f"
//        "html_url": "https://github.com/mojombo/grit/commit/1c03f3e1f822232aeb00833081418391a44fe3df"
        GithubAPI.init("gleiph", "Xcx3xy5T@");

//        List<Language> languagesList = GithubAPI.languagesList("https://api.github.com/repos/bs/starling");
//        System.out.println(languagesList.size());
//        GithubAPI.generic(projectUrl);
//        Project projectInfo = GithubAPI.projectInfo(projectUrl);
//        System.out.println(projectInfo.toString());
        GithubAPI.projects(0);
//        int languages = GithubAPI.languages(projectUrl);
//        System.out.println("languages = " + languages);
//
//        int commits = GithubAPI.commits(projectUrl);
//        System.out.println("commits = " + commits);
//
//        int contributors = GithubAPI.contributors(projectUrl);
//        System.out.println("contributors = " + contributors);
//
//        int merges = GithubAPI.merges(projectUrl);
//        System.out.println("merges = " + merges);
    }
}
