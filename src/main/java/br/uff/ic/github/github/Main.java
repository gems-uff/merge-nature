/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github;

import br.uff.ic.github.github.parser.GithubAPI;
//import br.uff.ic.github.github.parser.ReportReader;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 *
 * @author Gleiph
 */
public class Main {

    public static void main(String[] args) {
//
//        String username = null;
//        String password = null;
//        String reportPath = null;
//        int since = 0;
//
//        //Java projects
//        if(args.length == 2){
//            
//            try {
//                ReportReader.getJavaProjects(args[0], args[1]);
//            } catch (IOException ex) {
//                System.out.println("Verify the path!");
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        else if (args.length < 2) {
//            System.out.println("At least 2 parameters are expected! (1) username, and (2) password");
//        }
//
//        if (args.length >= 2) {
//            username = args[0];
//            password = args[1];
////            reportPath = args[2];
////
////            if (args.length == 4) {
////                since = Integer.parseInt(args[3]);
////            }
//            
            GithubAPI.init(/*username, password*/);
            GithubAPI.projects(/*since, reportPath*/);
//        }

    }
}
