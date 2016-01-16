package br.uff.ic.gems.resources.github.parser;

import br.uff.ic.gems.resources.cmd.CMDOutput;
import br.uff.ic.gems.resources.data.Fork;
import br.uff.ic.gems.resources.data.Language;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.github.authentication.User;
import br.uff.ic.gems.resources.github.cmd.CMDGithub;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Gleiph
 */
public class GithubAPI {

    private static String base = null;
    private static final String BEGIN_PARENTS = "\"parents\": [";
    private static final String END_PARENTS = "]";
    private static User user;

    private static final String CREATED_AT = "created_at";
    private static final String PRIVATE = "private";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String URL = "url";
    private static final String HTML_URL = "html_url";
    private static final String CONTRIBUTORS_URL = "contributors_url";
    private static final String LANGUAGES_URL = "languages_url";
    private static final String MESSAGE = "message";
    private static final String UPDATED_AT = "updated_at";
    private static final String FORK = "fork";
    private static final String FORKS_URL = "forks_url";

    static {
        User.init();
    }

    public static User nextUser() {
        return init();
    }

    public static User init() {
        user = User.nextUser();
        base = "curl -i -k -u " + user.getLogin() + ":" + user.getPassword() + " ";
        return user;
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

            output = CMDGithub.cmdGithub(base + link);

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

            output = CMDGithub.cmdGithub(base + link);

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

        CMDOutput output = CMDGithub.cmdGithub(base + url);

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

        CMDOutput output = CMDGithub.cmdGithub(base + query);

        for (String line : output.getOutput()) {
            System.out.println(line);
        }

    }

    public static List<Language> languagesList(String url) {

        List<Language> result = new ArrayList<>();
        String name = null;
        double size = 0;
        double total = 0;

        boolean area = false;

        CMDOutput output = CMDGithub.cmdGithub(base + url);

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

                size = Double.parseDouble(split[1].replaceAll(" ", "").replaceAll(",", ""));
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

        CMDOutput output = null;
        String link = url;

        while (link != null) {

            output = CMDGithub.cmdGithub(base + link);

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

    public static void projects(Connection connection) throws SQLException {

        //Jpa manager
        ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);

        String LINK = "Link:";

        CMDOutput output = null;
        String link = "https://api.github.com/repositories";

        JSONParser parser = new JSONParser();

        long lastId = projectDAO.lastID();

        if (lastId != -1) {
            link += "?since=" + lastId;
        }

        while (link != null) {

            output = CMDGithub.cmdGithub(base + link);

            String content = "";
            boolean begin = false;
            int count = 0;

            for (String line : output.getOutput()) {

                if (line.equals("[")) {
                    begin = true;
                }

                if (line.endsWith("},")) {
                    count++;
                }

                if (begin && !line.equals("[") && !line.equals("]")) {

                    if (line.endsWith("},")/* && count == 2*/) {
                        content += line.replace("},", "}");
                    } else {
                        content += line;
                    }
                }

                if (count == 2) {

                    try {
                        Object parse = parser.parse(content);
                        JSONObject jsono = (JSONObject) parse;

                        String id = jsono.get(ID).toString();
                        String searchUrl = jsono.get(URL).toString();

                        Project project = projectDAO.selectByProjectId(Long.parseLong(id));

                        //Project is not in the database
                        if (project.getId() == null) {

                            project = project(searchUrl, true, true, true);

                            if (project.getName() == null) {
                                String priva = jsono.get(PRIVATE).toString();
                                String name = jsono.get(NAME).toString();
                                String htmlUrl = jsono.get(HTML_URL).toString();
                                boolean fork = Boolean.parseBoolean(jsono.get(FORK).toString());

                                project.setCreatedAt(null);
                                project.setHtmlUrl(htmlUrl);
                                project.setId(Long.parseLong(id));
                                project.setMessage(null);
                                project.setName(name);
                                project.setPriva(Boolean.parseBoolean(priva));
                                project.setSearchUrl(searchUrl);
                                project.setUpdatedAt(null);
                                project.setFork(fork);

                                //Contributors
                                String developersUrl = jsono.get(CONTRIBUTORS_URL).toString();
                                int developers = GithubAPI.contributors(developersUrl);
                                project.setDevelopers(developers);

                                //Languages
                                String languagesUrl = jsono.get(LANGUAGES_URL).toString();
                                List<Language> languagesList = GithubAPI.languagesList(languagesUrl);
                                project.setLanguages(languagesList);

                                //Forks
                                Long originalProjectId = GithubAPI.originalProjectId(project.getSearchUrl());
                                project.setFork(!Objects.equals(project.getId(), originalProjectId));

                                try {
                                    projectDAO.insertAll(project);
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    projectDAO.insertAll(project);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            System.out.println("Skip: " + project.getName());

                        }

                        content = "";
                        count = 0;
                    } catch (ParseException ex) {
                        Logger.getLogger(GithubAPI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

            link = Parser.getLink(output.getOutput());
            String[] split = link.split("=");
            System.out.println("Next:" + split[split.length - 1]);
        }

    }

    public static Project project(String link, boolean developersInformation, boolean languageInformation, boolean forkInformation) {
        Project project = new Project();

        CMDOutput output = null;

        if (base == null) {
            output = CMDGithub.cmdGithub(link);
        } else {
            output = CMDGithub.cmdGithub(base + link);
        }

        String content = "";
        boolean begin = false;
        int count = 0;

        for (String line : output.getOutput()) {

            if (line.equals("{")) {
                begin = true;
            }

            if (line.endsWith("},")) {
                count++;
            }

            if (begin && !line.equals("[") && !line.equals("]")) {

                if (line.endsWith("},")/* && count == 2*/) {
                    content += line.replace("},", "}");
                } else {
                    content += line;
                }
            }

        }

        JSONParser parser = new JSONParser();
        Object parse;

        try {
            parse = parser.parse(content);
            JSONObject jsono = (JSONObject) parse;

            if (!jsono.containsKey(MESSAGE)) {

                project.setCreatedAt(jsono.get(CREATED_AT).toString());
                project.setHtmlUrl(jsono.get(HTML_URL).toString());
                project.setId(Long.parseLong(jsono.get(ID).toString()));
                project.setName(jsono.get(NAME).toString());
                project.setPriva(Boolean.parseBoolean(jsono.get(PRIVATE).toString()));
                project.setSearchUrl(jsono.get(URL).toString());
                project.setUpdatedAt(jsono.get(UPDATED_AT).toString());
                project.setFork(Boolean.parseBoolean(jsono.get(FORK).toString()));

                //Contributors
                if (developersInformation) {
                    String developersUrl = jsono.get(CONTRIBUTORS_URL).toString();
                    int developers = GithubAPI.contributors(developersUrl);
                    project.setDevelopers(developers);
                }

                //Languages
                if (languageInformation) {
                    String languagesUrl = jsono.get(LANGUAGES_URL).toString();
                    List<Language> languagesList = GithubAPI.languagesList(languagesUrl);
                    project.setLanguages(languagesList);
                }

                //Forks
                if (forkInformation) {
                    Long originalProjectId = GithubAPI.originalProjectId(project.getSearchUrl());
                    project.setFork(!Objects.equals(project.getId(), originalProjectId));

                    if (project.isFork()) {
                        project.setMainProjectId(originalProjectId);
                    } else {
                        project.setMainProjectId(project.getId());
                    }

                }
            } else {

                project.setMessage(jsono.get(MESSAGE).toString());

            }
            System.out.println(project.getName());

        } catch (ParseException ex) {
            Logger.getLogger(GithubAPI.class.getName()).log(Level.SEVERE, null, ex);
        }

        return project;
    }

    public static List<Fork> forks(String forksUrl, Long projectId) {

        List<Fork> forks = new ArrayList<>();

        CMDOutput output = CMDGithub.cmdGithub(base + forksUrl);
        int lastPage = 0;

        for (String line : output.getOutput()) {

            if (line.startsWith("Link:")) {
                String[] split = line.split(",");
                String last = null;
                for (String part : split) {
                    if (part.contains("rel=\"last\"")) {
                        last = part;
                    }
                }

                if (last != null) {
                    String max = last.split(";")[0].split("=")[1].replace(">", "");
                    lastPage = Integer.parseInt(max);
                }

            }
        }

        for (int i = 1; i < lastPage; i++) {
            output = CMDGithub.cmdGithub(base + forksUrl + "?page=" + i);
            List<String> jsons = getJsonsString(output.getOutput());

            for (String json : jsons) {
                Fork fork = getFork(json, projectId);
                forks.add(fork);
            }

        }

        return forks;
    }

    private static List<String> getJsonsString(List<String> output) {

        List<String> jsons = new ArrayList<>();

        boolean jsonContent = false;
        String jsonString = "";
        int count = 0;
        for (String line : output) {

            if (line.startsWith("[")) {
                jsonContent = true;
            }

            if (jsonContent && !line.startsWith("[") && !line.startsWith("]")) {
                if (line.contains("{") && line.contains("}")) {
                    jsonString += line + "\n";
                } else if (line.contains("{")) {
                    jsonString += line + "\n";
                    count++;
                } else if (line.contains("}")) {
                    count--;

                    if (line.contains("},") && count == 0) {
                        jsonString += line.replace("},", "}") + "\n";
                    } else {
                        jsonString += line + "\n";
                    }

                    if (count == 0) {
                        jsons.add(jsonString);
                        jsonString = "";
                    }
                } else {
                    jsonString += line + "\n";
                }

            }
        }

        return jsons;
    }

    private static String getJsonString(List<String> output) {

        boolean jsonContent = false;
        String jsonString = "";
        int count = 0;
        for (String line : output) {

            if (line.startsWith("{")) {
                jsonContent = true;
            }

            if (jsonContent && !line.startsWith("[") && !line.startsWith("]")) {
                jsonString += line + "\n";
            }
        }

        return jsonString;
    }

    public static Fork getFork(String jsonString, Long projectId) {

        JSONParser jSONParser = new JSONParser();

        JSONObject parser = null;
        try {
            parser = (JSONObject) jSONParser.parse(jsonString);
        } catch (ParseException ex) {
            return null;
        }

        Object idObject = parser.get("id");
        Object htmlUrlObject = parser.get("html_url");

        Long id = Long.parseLong(idObject.toString());
        String htmlUrl = htmlUrlObject.toString();

        Fork fork = new Fork();
        fork.setForkId(id);
        fork.setForkURL(htmlUrl);
        fork.setProjectId(projectId);

        return fork;
    }

    public static Long originalProjectId(String projectSearchURL) throws ParseException {

        String message = null;

        CMDOutput output = CMDGithub.cmdGithub(base + projectSearchURL);

        JSONParser jSONParser = new JSONParser();
        JSONObject parser;
        parser = null;

        String jsonString = getJsonString(output.getOutput());

        parser = (JSONObject) jSONParser.parse(jsonString);

        try {//Take the cases where the project is not avaliable 
            boolean fork = Boolean.parseBoolean(parser.get(FORK).toString());

            if (fork) {
                JSONObject parent = (JSONObject) parser.get("parent");
                String url = parent.get(URL).toString();

                Long originalProjectId = originalProjectId(url);

                if (originalProjectId != null) {
                    return originalProjectId;
                } else {
                    return Long.parseLong(parser.get(ID).toString());
                }

            } else {
                Long id = Long.parseLong(parser.get(ID).toString());
                return id;
            }
        } catch (NullPointerException ex) {
            return null;
        }
    }

}
