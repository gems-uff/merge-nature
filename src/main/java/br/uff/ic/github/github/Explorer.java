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
public class Explorer {

    public static final String BEGIN_LINK = "Link: <";
    public static final String END_LINK = ">;";
    public static final String NAME = ">;";

    public static void main(String[] args) {

        
//        int languages = GithubAPI.languages("https://api.github.com/repos/git/git");
//        int commits = GithubAPI.commits("https://api.github.com/repos/mojombo/grit");
//        int merges = GithubAPI.merges("https://api.github.com/repos/mojombo/grit");
        int contributors = GithubAPI.contributors("https://api.github.com/repos/mojombo/grit");
//        System.out.println("commits = " + commits);
//        System.out.println("merges = " + merges);
//        System.out.println("languages = " + languages);
        System.out.println("contributors = " + contributors);
//        String next_link = null;
//        String base = "curl -i -u \"gleiph:Xcx3xy5T@\" ";
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repositories"; //ok
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repos/mojombo/grit"; //ok
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repos/git/git/languages"; //ok
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repos/mojombo/grit/contributors"; //ok
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repos/git/git/merges"; //ok
//
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repos/mojombo/grit/stats/commit_activity";
////        String command = "curl -i -u \"gleiph:Xcx3xy5T@\" https://api.github.com/repos/mojombo/grit/commits"; //ok
//        String command = "https://api.github.com/repos/mojombo/grit/commits"; //ok
//
//        CMDOutput result = CMD.cmd(base + command);
//
//        String current_link = Parser.getLink(result.getOutput());
//        while (Parser.getLink(result.getOutput()) != null) {
//            System.out.println(current_link);
//            result = CMD.cmd(base + current_link);
//            current_link = Parser.getLink(result.getOutput());
//        }
//
////        System.out.println("Error:");
////        for (String string : result.getErrors()) {
////            System.out.println("\t" + string);
////        }
////
////        System.out.println("Output:");
////        for (String string : result.getOutput()) {
////            System.out.println("\t" + string);
////            
//////            if(string.startsWith(BEGIN_LINK)){
//////                String link = string.replaceFirst(BEGIN_LINK, "");
//////                String[] split = link.split(END_LINK);
//////                next_link = split[0];
//////                System.out.println(next_link);
//////            }
//////            
//////            if(string.contains("\"name\"")){
//////                getContent(string);
//////            }
////            
////        }
    }

    
}
