/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github.parser;

import br.uff.ic.github.github.CMD;
import br.uff.ic.github.github.CMDOutput;
import br.uff.ic.github.github.data.Language;
import br.uff.ic.github.github.data.Project;
import br.uff.ic.github.github.file.WriteFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gleiph
 */
public class GithubAPI {

    private static String base = null;
    private static final String BEGIN_PARENTS = "\"parents\": [";
    private static final String END_PARENTS = "]";

    public static void init(String login, String password) {
        base = "curl -i -u " + login + ":" + password + " ";
    }

    /**
     * Returns the number of commits from GitHub project
     *
     * @param url from a Github project
     * @return Number of commits
     */
    public static int commits(String url) {
        int result = 0;
        int controller = 0;

        if (url.endsWith("/")) {
            url += "commits";
        } else {
            url += "/commits";
        }

        CMDOutput output = null;
        String link = url;

        while (link != null) {

            output = CMD.cmd(base + link);

            for (String line : output.getOutput()) {
                if (line.contains("{")) {
                    controller++;
                }
                if (line.contains("}")) {
                    controller--;

                    if (controller == 0) {
                        result++;
                    }
                }
            }

            link = Parser.getLink(output.getOutput());
        }

        return result;
    }

    /**
     * Returns the number of merges from GitHub project
     *
     * @param url from a Github project
     * @return Number of commits
     */
    public static int merges(String url) {
        int result = 0;
        boolean parents_area = false;
        int parents_number = 0;

        if (url.endsWith("/")) {
            url += "commits";
        } else {
            url += "/commits";
        }

        CMDOutput output = null;
        String link = url;

        while (link != null) {

            output = CMD.cmd(base + link);

            for (String line : output.getOutput()) {
                if (line.contains(BEGIN_PARENTS)) {
                    parents_area = true;
                }

                if (parents_area) {
                    if (line.contains("{")) {
                        parents_number++;
                    }

                    if (line.contains(END_PARENTS)) {
                        if (parents_number > 1) {
                            result++;
                        }
                        parents_area = false;
                        parents_number = 0;
                    }
                }
            }

            link = Parser.getLink(output.getOutput());
        }

        return result;
    }

    public static int languages(String url) {
        int result = 0;
        boolean area = false;

        if (url.endsWith("/")) {
            url += "languages";
        } else {
            url += "/languages";
        }

        CMDOutput output = CMD.cmd(base + url);

        for (String line : output.getOutput()) {

            if (line.contains("{")) {
                area = true;
            } else if (line.contains("}")) {
                area = false;
            } else if (area) {
                result++;
            }

        }

        return result;
    }

    public static void generic(String query) {

        CMDOutput output = CMD.cmd(base + query);

        for (String line : output.getOutput()) {
            System.out.println(line);
        }

    }

    public static List<Language> languagesList(String url) {

        List<Language> result = new ArrayList<Language>();
        String name = null;
        int size = 0;
        double total = 0;

        boolean area = false;

        if (url.endsWith("/")) {
            url += "languages";
        } else {
            url += "/languages";
        }

        CMDOutput output = CMD.cmd(base + url);

        for (String line : output.getOutput()) {

            if (line.contains("{")) {
                area = true;
            } else if (line.contains("}")) {
                area = false;
            } else if (area) {
                if (!line.contains(":")) {
                    continue;
                }
                if (line.contains("\"message\": \"Repository access blocked\"")) {
                    break;
                }
                String[] split = line.split("\"");
                name = split[1];

                split = line.split(":");

                size = Integer.parseInt(split[1].replaceAll(" ", "").replaceAll(",", ""));
                total += size;
                result.add(new Language(name, size));
            }

        }

        for (Language language : result) {
            language.setPercentage(100.00 * language.getSize() / total);
        }

        return result;
    }

    public static int contributors(String url) {
        int result = 0;
        int controller = 0;

        if (url.endsWith("/")) {
            url += "contributors";
        } else {
            url += "/contributors";
        }

        CMDOutput output = null;
        String link = url;

        while (link != null) {

            output = CMD.cmd(base + link);

            for (String line : output.getOutput()) {
                if (line.contains("{")) {
                    controller++;
                }
                if (line.contains("}")) {
                    controller--;

                    if (controller == 0) {
                        result++;
                    }
                }
            }

            link = Parser.getLink(output.getOutput());
        }

        return result;
    }

    public static void projects(int since, String reportPath) {

        WriteFile fw;
        File report = new File(reportPath);

        if (report.isFile()) {
            fw = new WriteFile(reportPath, false);
            //TODO: read the file and continue
        } else {
            fw = new WriteFile(reportPath, true);
        }

        try {
            fw.open();
        } catch (IOException ex) {
            Logger.getLogger(GithubAPI.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Problem during report generation!");
            return;
        }

        int cont = 0;

        String NAME = "\"name\":";
        String FULL_NAME = "\"full_name\":";
        String PRIVATE = "\"private\":";
        String HTML_URL = "\"html_url\":";
        String URL = "\"url\":";
        String LINK = "Link:";

        String name, fullName, priva, htmlUrl, url;
        name = null;
        fullName = null;
        priva = null;
        htmlUrl = null;
        url = null;

        CMDOutput output = null;
        String link = "https://api.github.com/repositories";

        if (since > 0) {
            link = "https://api.github.com/repositories?since=" + since;
        }
        while (link != null) {

            output = CMD.cmd(base + link);

//            System.out.println(base + link);
            for (String line : output.getOutput()) {

                if (line.contains(LINK)) {
                    continue;
                }

                if (line.contains("{")) {
                    cont++;
                }

                if (line.contains("}")) {
                    cont--;

                    if (cont == 0) {
                        fw.writeln("name: " + name);
                        fw.writeln("\tFull name: " + fullName);
                        fw.writeln("\tPrivate: " + priva);
                        fw.writeln("\tHTML URL: " + htmlUrl);
                        fw.writeln("\tURL: " + url);

//                        int commits = GithubAPI.commits(url);
//                        System.out.println("\tCommits: " + commits);
//                        int merges = GithubAPI.merges(url);
//                        System.out.println("\tMerges: " + merges);
                        int contributors = GithubAPI.contributors(url);
                        fw.writeln("\tContributors = " + contributors);

//                        int languages = GithubAPI.languages(url);
                        List<Language> languagesList = GithubAPI.languagesList(url);
                        if (languagesList != null) {
                            fw.writeln("\tLanguages = " + languagesList.size());
                            for (Language language : languagesList) {
                                fw.writeln("\t\t" + language.getName() + ":" + language.getPercentage());
                            }
                        } else {
                            fw.writeln("\tLanguages = 0");
                        }
                    }

                    try {
                        System.out.println(name);
                        fw.setReplace(false);
                        fw.close();
                        fw.open();
                    } catch (IOException ex) {
                        Logger.getLogger(GithubAPI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (line.contains(NAME)) {
                    name = Parser.getContent(line);
                }
                if (line.contains(FULL_NAME)) {
                    fullName = Parser.getContent(line);
                }
                if (line.contains(PRIVATE)) {
                    priva = Parser.getContent(line);
                }
                if (line.contains(HTML_URL)) {
                    htmlUrl = Parser.getContent(line);
                }
                if (line.contains(URL)) {
                    url = Parser.getContent(line);
                }
            }

            link = Parser.getLink(output.getOutput());
            String[] split = link.split("=");
            System.out.println("Next:" + split[split.length - 1]);
        }

        fw.close();
    }

    public static Project projectInfo(String url) {

        String CREATED_AT = "\"created_at\":";
        String UPDATED_AT = "\"updated_at\":";
        String NAME = "\"name\":";
        String URL = "\"url\":";
        String HTML_URL = "\"html_url\":";

        Project result = new Project();

        CMDOutput output = CMD.cmd(base + url);

        for (String line : output.getOutput()) {

            if (line.contains(CREATED_AT)) {
                result.setCreatedAt(Parser.getContent(line));
            } else if (line.contains(UPDATED_AT)) {
                result.setUpdatedAt(Parser.getContent(line));
            } else if (line.contains(NAME)) {
                result.setName(Parser.getContent(line));
            } else if (line.contains(URL)) {
                result.setUrl(Parser.getContent(line));
            } else if (line.contains(HTML_URL)) {
                result.setHtmlUrl(Parser.getContent(line));
            }
        }

        return result;
    }

}
