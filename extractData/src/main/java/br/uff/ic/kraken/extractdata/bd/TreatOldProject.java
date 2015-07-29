/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata.bd;

import br.uff.ic.gems.resources.data.Language;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.ProjectDAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class TreatOldProject {

    //Project
    private static final String ID = "id";

    private static final String NAME = "name";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String PRIVATE = "priva";
    private static final String HTML_URL = "htmlUrl";
    private static final String SEARCH_URL = "searchUrl";
    private static final String DEVELOPERS = "developers";
    private static final String MESSAGE = "message";

    private static final String REPOSITORY_PATH = "repositoryPath";
    private static final String NUMBER_REVISIONS = "numberRevisions";
    private static final String NUMBER_MERGE_REVISIONS = "numberMergeRevisions";
    private static final String NUMBER_CONFLICTING_MERGES = "numberConflictingMerges";

    private static final String SIZE = "size";
    private static final String PERCENTAGE = "percentage";

    //Language
    public static List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TreatOldProject.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/krakenBackup", "postgres",
                    "kraken");

            Statement statement = connection.createStatement();
            String queryProject = "SELECT * "
                    + "FROM project p";

            String queryLanguage = "SELECT l.name, l.size, l.percentage "
                    + "FROM project p, "
                    + "     project_language, "
                    + "     language l "
                    + "WHERE l.id = project_language.languages_id AND "
                    + "      p.id = project_language.project_id AND "
                    + "      p.id = ";

            ResultSet result = statement.executeQuery(queryProject);

            Project project = null;

            while (result.next()) {
                project = new Project();

                project.setId(result.getLong(ID));
                project.setCreatedAt(result.getString(CREATED_AT));
                project.setDevelopers(result.getInt(DEVELOPERS));
                project.setHtmlUrl(result.getString(HTML_URL));
                project.setLanguages(null);
                project.setMessage(result.getString(MESSAGE));
                project.setName(result.getString(NAME));
                project.setNumberConflictingMerges(0);
                project.setNumberMergeRevisions(0);
                project.setNumberRevisions(0);
                project.setPriva(result.getBoolean(PRIVATE));
                project.setRepositoryPath(result.getString(REPOSITORY_PATH));
                project.setRevisions(null);
                project.setSearchUrl(result.getString(SEARCH_URL));
                project.setUpdatedAt(result.getString(UPDATED_AT));

                Statement statement1 = connection.createStatement();
                ResultSet languagesResult = statement1.executeQuery(queryLanguage + project.getId());

                List<Language> languages = new ArrayList<>();
                Language language = null;
                while (languagesResult.next()) {
                    language = new Language();

                    language.setName(languagesResult.getString(NAME));
                    language.setPercentage(languagesResult.getDouble(PERCENTAGE));
                    language.setSize(languagesResult.getDouble(SIZE));

                    languages.add(language);
                }

                project.setLanguages(languages);

                projects.add(project);
            }

        } catch (SQLException ex) {
            Logger.getLogger(TreatOldProject.class.getName()).log(Level.SEVERE, null, ex);
        }

        return projects;
    }

    public static List<Project> getFilteredProjects() {
        List<Project> result = new ArrayList<>();

        System.out.println("Geting projects begin");
        List<Project> projects = getProjects();
        System.out.println("Geting projects end");

        System.out.println("Filterin projects begin");
        for (Project project : projects) {
            if (isMainJava(project)) {
                result.add(project);
            }
        }
        System.out.println("Filtering projects end");

        return result;
    }

    public static boolean isMainJava(Project project) {
        Language mainLanguage = new Language("None", 0);

        for (Language language : project.getLanguages()) {
            if (mainLanguage.getSize() < language.getSize()) {
                mainLanguage = language;
            }
        }

        return mainLanguage.getName().toUpperCase().equals("JAVA");

    }

    public static void migrateProject(Connection connection, Long id) throws SQLException, Exception {

        ProjectDAO projectDAO = new ProjectDAO();

        Statement statement = connection.createStatement();
        String queryProject = "SELECT * "
                + "FROM project p WHERE p.id = " + id;

        String queryLanguage = "SELECT l.name, l.size, l.percentage "
                + "FROM project p, "
                + "     project_language, "
                + "     language l "
                + "WHERE l.id = project_language.languages_id AND "
                + "      p.id = project_language.project_id AND "
                + "      p.id = ";

        ResultSet result = statement.executeQuery(queryProject);

        Project project = null;

        while (result.next()) {
            project = new Project();

            project.setId(result.getLong(ID));
            project.setCreatedAt(result.getString(CREATED_AT));
            project.setDevelopers(result.getInt(DEVELOPERS));
            project.setHtmlUrl(result.getString(HTML_URL));
            project.setLanguages(null);
            project.setMessage(result.getString(MESSAGE));
            project.setName(result.getString(NAME));
            project.setNumberConflictingMerges(0);
            project.setNumberMergeRevisions(0);
            project.setNumberRevisions(0);
            project.setPriva(result.getBoolean(PRIVATE));
            project.setRepositoryPath(result.getString(REPOSITORY_PATH));
            project.setRevisions(null);
            project.setSearchUrl(result.getString(SEARCH_URL));
            project.setUpdatedAt(result.getString(UPDATED_AT));

            Statement statement1 = connection.createStatement();
            ResultSet languagesResult = statement1.executeQuery(queryLanguage + project.getId());

            List<Language> languages = new ArrayList<>();
            Language language = null;
            while (languagesResult.next()) {
                language = new Language();

                language.setName(languagesResult.getString(NAME));
                language.setPercentage(languagesResult.getDouble(PERCENTAGE));
                language.setSize(languagesResult.getDouble(SIZE));

                languages.add(language);
            }

            project.setLanguages(languages);

            System.out.println(project.getId() + " " + project.getName());
            projectDAO.saveGithub(project);
        }

    }

    public static List<Long> projectIDs(Connection connection, List<Long> migrated) throws SQLException {

        Statement statement = connection.createStatement();

        String queryProject = "SELECT p.id "
                + "FROM project p ORDER BY p.id";

        ResultSet result = statement.executeQuery(queryProject);

        List<Long> resultList = new ArrayList<>();

        while (result.next()) {
            Long id = result.getLong(ID);
            resultList.add(id);

        }

        return resultList;
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        Connection connection = null;

        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/krakenBackup", "postgres",
                "kraken");

        return connection;
    }

    public static void migrate() throws ClassNotFoundException, SQLException, Exception {

        Date begin = new Date();

        Connection connection = getConnection();

        ProjectDAO projectDAO = new ProjectDAO();
        List<Long> projectIDsMigrated = projectDAO.getIDs();

        int sizeMigrated = projectIDsMigrated.size();
        List<Long> projectIDsNonMigrated = projectIDs(connection, projectIDsMigrated);
        int sizeNonMigrated = projectIDsNonMigrated.size();

        System.out.println(sizeNonMigrated + " - " + sizeMigrated + " = " + (sizeNonMigrated - sizeMigrated));
        int i = 0, j = 0;

        while (i < projectIDsMigrated.size()) {

            boolean removed = false;

            Long migrated = projectIDsMigrated.get(i);

            while (j < projectIDsNonMigrated.size()) {
                Long nonMigrated = projectIDsNonMigrated.get(j);

                if (migrated > nonMigrated) {
                    j++;
                } else if (migrated < nonMigrated) {
                    break;
                } else {
                    projectIDsMigrated.remove(i);
                    projectIDsNonMigrated.remove(j);
                    removed = true;

                    break;
                }
            }
            if (!removed) {
                i++;
            }
        }

        Date end = new Date();

        System.out.println("----------------------------------------------------------");
        System.out.println(begin + " " + end);
        System.out.println("----------------------------------------------------------");

        int size = projectIDsNonMigrated.size();
        int currentLine = 0;

        for (Long project : projectIDsNonMigrated) {
            System.out.println(++currentLine + "/" + size);
            migrateProject(connection, project);
        }

    }

    public static void main(String[] args) {
        try {
            migrate();
        } catch (SQLException ex) {
            Logger.getLogger(TreatOldProject.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(TreatOldProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
