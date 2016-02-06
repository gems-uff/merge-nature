/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.vcs;

import br.uff.ic.gems.resources.cmd.CMDOutput;
import br.uff.ic.gems.resources.cmd.CMD;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class Git {

    private String repository;

    public Git(String repository) {
        this.repository = repository;
    }

    public String fileDiff(String initialFile, String finalFile) {
        StringBuilder result = new StringBuilder();

        String command = "git diff " + initialFile + " " + finalFile;

        CMDOutput cmdOutput = CMD.cmd(getRepository(), command);
        if (cmdOutput.getErrors().isEmpty()) {

            for (String line : cmdOutput.getOutput()) {
                result.append(line).append("\n");
            }

            return result.toString();
        } else {
            return null;
        }
    }

    public String clone(String url) {
        StringBuilder result = new StringBuilder();

        String command = "git clone " + url;

        System.out.println(command);

        CMDOutput cmdOutput = CMD.cmd(getRepository(), command);
        if (cmdOutput.getErrors().isEmpty()) {

            for (String line : cmdOutput.getOutput()) {
                result.append(line).append("\n");
            }

            return result.toString();
        } else {
            return null;
        }
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /*-------------------------------------------------------------------------------------------
    
     COMMANDS
    
     ----------------------------------------------------------------------------------------------*/
    public static List<String> fileDiff(String repository, String file, String sourceSHA, String targetSHA) {
        List<String> result = new ArrayList<String>();
        String command = "git diff " + sourceSHA + " " + targetSHA + " " + file;

        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }

    public static List<String> diff(String repository, String sourceFile, String targetFile) {
        List<String> result = new ArrayList<String>();
        String command = "git diff " + sourceFile + " " + targetFile;

        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }
    
    public static List<String> diffLog(String repository, String relativePath) {
        List<String> result = new ArrayList<String>();
        String command = "git log -1 -p " + relativePath;

        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }
    
    /*-------------------------------------------------------------------------------------------
    
     LOG BASED
    
     ----------------------------------------------------------------------------------------------*/
    public static List<String> revList(String repository, String since, String until) {

        String command = "git rev-list --ancestry-path " + since + ".." + until;
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        cmdOutput.getOutput().add(since);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }

    /**
     * Method used to get commits in a given interval [mergebase, head]
     *
     * @param mergeBase
     * @param head
     * @param repositoryPath
     * @return
     * @throws IOException
     */
    public static List<String> getInterval(String mergeBase, String head, String repositoryPath) throws IOException {
        List<String> revList = Git.revList(repositoryPath, mergeBase, head);

        List<String> result = new ArrayList<String>();
        //filtering

        if (revList.size() > 5000) {
            System.out.println("Excedeu limite de 5000");
            System.out.println("mergeBase = " + mergeBase);
            System.out.println("head = " + head);
            return result;
        }
        try {
            filter(revList.get(0), revList, result, repositoryPath);
        } catch (StackOverflowError ex) {
            System.out.println("mergeBase = " + mergeBase);
            System.out.println("head = " + head);
            ex.printStackTrace();
        }
        result.add(head);
        return result;
    }

    public static List<String> logRange(String repository, String since, String until) {

        String command = "git log " + since + ".." + until + " --reverse --first-parent --pretty=%H";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        cmdOutput.getOutput().add(0, since);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput();
        } else {
            return null;
        }
    }

    public static List<String> logByDays(String repository, Date begin, Date end) {
        List<String> commits = new ArrayList<>();

        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss z");

        String dateBegin = formater.format(begin);
        String dateEnd = formater.format(end);

        //git.sh is in ~/bin
        String[] command = {"git",
            "log",
            "--all",
            "--reverse",
            "--format=%H;%ci",
            "--after=\"" + dateBegin + "\"",
            "--before=\"" + dateEnd + "\""};

        CMDOutput cmdOutput = CMD.cmdArray(repository, command);

        for (String output : cmdOutput.getOutput()) {
            commits.add(output.split(";")[0]);
        }

        return commits;
    }


    /*-------------------------------------------------------------------------------------------
    
     STATUS BASED
    
     ----------------------------------------------------------------------------------------------*/
    public static List<String> status(String repositoryPath) {
        String command = "git status ";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> statusShort(String repositoryPath) {
        String command = "git status -s";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    /*-------------------------------------------------------------------------------------------
    
     UNCLASSIFIED
    
     ----------------------------------------------------------------------------------------------*/
    private static boolean filter(String currentCommit, List<String> commits, List<String> result, String repositoryPath) {

        if (currentCommit.equals(commits.get(commits.size() - 1))) {
            return true;
        }

        List<String> parents = getParents(repositoryPath, currentCommit);

        for (String parent : parents) {
            if (commits.contains(parent)) {
                boolean assessment = filter(parent, commits, result, repositoryPath);
                if (assessment) {
                    result.add(parent);
                    return assessment;
                }

            }
        }
        return false;
    }

    public static String getCommiter(String repository, String sha1) {

        String command = "git show " + sha1 + " --pretty=%cn";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput().get(0);
        } else {
            return null;
        }
    }

    public static List<String> getChangedFiles(String repository, String sha1) {

        List<String> result = new ArrayList<>();

        String[] command = {"git", "diff-tree", "--no-commit-id", "--name-only", "-r", sha1};

        CMDOutput cmdOutput = CMD.cmdArray(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {

            for (String output : cmdOutput.getOutput()) {
                result.add(output);
            }
            return result;
        } else{
            return null;
        }

    }

    public static String getAuthor(String repository, String sha1) {

        String command = "git show " + sha1 + " --pretty=%an";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput().get(0);
        } else {
            return null;
        }
    }

    public static String getDate(String repository, String sha1) {

        String command = "git show " + sha1 + " --pretty=%ad";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput().get(0);
        } else {
            return null;
        }

    }

    public static String getDateISO(String repository, String sha1) {

        String command = "git show " + sha1 + " --pretty=%ci";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput().get(0);
        } else {
            return null;
        }

    }

    public static Long getDateLong(String repository, String sha1) {

        String command = "git show " + sha1 + " --pretty=%ct";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return Long.parseLong(cmdOutput.getOutput().get(0));
        } else {
            return null;
        }

    }

    public static String getMessage(String repository, String sha1) {

        String command = "git show " + sha1 + " --pretty=%s";
        CMDOutput cmdOutput = CMD.cmd(repository, command);
        if (cmdOutput.getErrors().isEmpty()) {
            return cmdOutput.getOutput().get(0);
        } else {
            return null;
        }
    }

    public static List<String> getMergeRevisions(String repositoryPath) {
        String command = "git log --all --merges --pretty=%H";
//        System.out.println(command);
        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));
//            exec.waitFor();

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);

            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> getMergeRevisions(String repositoryPath, boolean  reverse) {
        String command = "git log --all --merges --pretty=%H";
        
        if(reverse)
            command = "git log --all --merges --reverse --pretty=%H";
//        System.out.println(command);
        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));
//            exec.waitFor();

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);

            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }
    
    public static List<String> getAllRevisions(String repositoryPath) {
        String command = "git log --all --pretty=%H";
//        System.out.println(command);
        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));
//            exec.waitFor();

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);

            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> getParents(String repositoryPath, String revision) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git log --pretty=%P -n 1 " + revision;

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                String[] split = s.split(" ");
                for (String rev : split) {
                    output.add(rev);
                }

            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static String getMergeBase(String repositoryPath, String commit1, String commit2) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git merge-base " + commit1 + " " + commit2;

        String output = null;

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output = s;
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> logOneline(String repositoryPath, String since, String until) {
//        String command = "git rev-list --parents -n 1 " + revision;
//        String command = "git log --pretty=oneline " + since + ".." + until;
//        String command = "git log --pretty=oneline --first-parent " + since + ".." + until;
        String command = "git rev-list --ancestry-path " + since + ".." + until;

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> checkout(String repositoryPath, String revision) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git checkout " + revision;

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                String[] split = s.split(" ");
                for (String rev : split) {
                    output.add(rev);
                }

            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> merge(String repositoryPath, String revision, boolean commit, boolean fastForward) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git merge ";

        if (!commit) {
            command = command + " --no-commit ";
        }

        if (!fastForward) {
            command = command + "--no-ff ";
        }

        command = command + revision;

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
//                String[] split = s.split(" ");
//                for (String rev : split) {
//                    output.add(rev);
//                }
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> reset(String repositoryPath) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git reset --hard";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
//                String[] split = s.split(" ");
//                for (String rev : split) {
//                    output.add(rev);
//                }
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> clean(String repositoryPath) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git clean -df";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
//                String[] split = s.split(" ");
//                for (String rev : split) {
//                    output.add(rev);
//                }
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> mergeAbort(String repositoryPath) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git merge --abort";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
//                String[] split = s.split(" ");
//                for (String rev : split) {
//                    output.add(rev);
//                }
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> resetMerge(String repositoryPath) {
//        String command = "git rev-list --parents -n 1 " + revision;
        String command = "git reset --merge";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> clone(String repositoryPath, String repositoryURL, String localRepository) {
//        String command = "git rev-list --parents -n 1 " + revision;

        String command = "git clone " + repositoryURL + " " + localRepository;
        if (localRepository == null) {
            command = "git clone " + repositoryURL;
        }

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }

    public static List<String> removedFiles(String repositoryPath, String sha1, String sha2) {
        List<String> result = new ArrayList<>();

        String command = "git diff " + sha1 + " " + sha2 + " --name-status";

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (String line : output) {
            if (line.startsWith("D")) {
                line = line.replaceFirst("D", "");
                while (line.startsWith(" ")) {
                    line = line.replaceFirst(" ", "");
                }

                while (line.startsWith("\t")) {
                    line = line.replaceFirst("\t", "");
                }

                line = line.replaceAll("\\\\", File.separator);
                line = line.replaceAll("/", File.separator);

                result.add(line);
            }
        }

        return result;
    }

    public static List<String> conflictedFiles(String repositoryPath) {

        if(!repositoryPath.endsWith(File.separator))
            repositoryPath += File.separator;
        
        List<String> statusShort = Git.statusShort(repositoryPath);
        List<String> files = new ArrayList<String>();

        for (String line : statusShort) {
            if (line.startsWith("UU")) {
                String lineChanged = line.replaceFirst("UU", "");

                while (lineChanged.startsWith(" ")) {
                    lineChanged = lineChanged.replaceFirst(" ", "");
                    lineChanged = repositoryPath + lineChanged;
                }

                files.add(lineChanged);
            }
        }

        return files;
    }

    public static List<String> diffNameStatus(String repositoryPath, String revision1, String revision2) {
        String command = "git diff --name-status " + revision1 + " " + revision2;

        List<String> output = new ArrayList<String>();

        try {
            Process exec = Runtime.getRuntime().exec(command, null, new File(repositoryPath));

            String s;

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(exec.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(exec.getErrorStream()));

            // read the output from the command
            while ((s = stdInput.readLine()) != null) {
                output.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException ex) {
            Logger.getLogger(Git.class.getName()).log(Level.SEVERE, null, ex);
        }

        return output;
    }
}
