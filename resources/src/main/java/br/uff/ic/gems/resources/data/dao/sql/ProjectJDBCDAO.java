/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.Language;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.Revision;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ProjectJDBCDAO {

    public static final String ID = "id";
    public static final String CREATED_AT = "createdat";
    public static final String DEVELOPERS = "developers";
    public static final String HTML_URL = "htmlurl";
    public static final String MESSAGE = "message";
    public static final String NAME = "name";
    public static final String CONFLICTING_MERGES = "numberconflictingmerges";
    public static final String MERGE_REVISIONS = "numbermergerevisions";
    public static final String REVISIONS = "numberrevisions";
    public static final String PRIVATE = "priva";
    public static final String REPOSITORY_PATH = "repositorypath";
    public static final String SEARCH_URL = "searchurl";
    public static final String UPDATED_AT = "updatedat";

    private final String database;

    public ProjectJDBCDAO(String database) {
        this.database = database;
    }

    public void insert(Project project) throws SQLException {

        if (project.getId() == null) {
            throw new SQLException("Invalid identifier during insertion.");
        }

        String insertSQL = "INSERT INTO " + Tables.PROJECT
                + "(" + ID + ", " + CREATED_AT + ", " + DEVELOPERS + ", " + HTML_URL
                + ", " + MESSAGE + ", " + NAME + ", " + CONFLICTING_MERGES + ", "
                + MERGE_REVISIONS + ", " + REVISIONS + ", " + PRIVATE + ", "
                + REPOSITORY_PATH + ", " + SEARCH_URL + ", " + UPDATED_AT + ") "
                + "VALUES (\'" + project.getId() + "\', \'" + project.getCreatedAt() + "\', \'" + project.getDevelopers()
                + "\', \'" + project.getHtmlUrl() + "\', \'" + project.getMessage() + "\', \'" + project.getName()
                + "\', \'" + project.getNumberConflictingMerges() + "\', \'" + project.getNumberMergeRevisions() + "\', \'"
                + project.getNumberRevisions() + "\', \'" + project.isPriva() + "\', \'" + project.getRepositoryPath()
                + "\', \'" + project.getSearchUrl() + "\', \'" + project.getUpdatedAt() + "\')";

        DefaultOperations.insert(insertSQL, database);

    }

    public void insertAll(Project project) throws SQLException {
        this.insert(project);

        //Adding languages
        LanguageJDBCDAO languageDAO = new LanguageJDBCDAO(database);

        for (Language language : project.getLanguages()) {
            languageDAO.insertAll(language, project.getId());
        }

        //Adding revisions
        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(database);

        for (Revision revision : project.getRevisions()) {
            revisionDAO.insertAll(revision, project.getId());
        }

    }

    public List<Project> select() throws SQLException {
        List<Project> projects = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.PROJECT;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                Project project = new Project();

                project.setCreatedAt(results.getString(CREATED_AT));
                project.setDevelopers(results.getInt(DEVELOPERS));
                project.setHtmlUrl(results.getString(HTML_URL));
                project.setId(results.getLong(ID));
                project.setMessage(results.getString(MESSAGE));
                project.setNumberConflictingMerges(results.getInt(CONFLICTING_MERGES));
                project.setNumberMergeRevisions(results.getInt(MERGE_REVISIONS));
                project.setNumberRevisions(results.getInt(REVISIONS));
                project.setPriva(results.getBoolean(PRIVATE));
                project.setRepositoryPath(results.getString(REPOSITORY_PATH));
                project.setSearchUrl(results.getString(SEARCH_URL));
                project.setUpdatedAt(results.getString(UPDATED_AT));

                projects.add(project);

            }
        }

        return projects;
    }

    public List<Project> selectAll() throws SQLException {
        List<Project> projects = this.select();

        LanguageJDBCDAO languageDAO = new LanguageJDBCDAO(database);
        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(database);

        for (Project project : projects) {

            project.setLanguages(languageDAO.selectAllByProjectId(project.getId()));
            project.setRevisions(revisionDAO.selectAllByProjectId(project.getId()));

        }

        return projects;
    }

    public Project selectByProjectId(Long projectId) throws SQLException {
        Project project = new Project();

        String query = "SELECT * FROM " + Tables.PROJECT
                + " WHERE " + ID + " = " + projectId;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            if (results.next()) {

                project.setCreatedAt(results.getString(CREATED_AT));
                project.setDevelopers(results.getInt(DEVELOPERS));
                project.setHtmlUrl(results.getString(HTML_URL));
                project.setId(results.getLong(ID));
                project.setMessage(results.getString(MESSAGE));
                project.setNumberConflictingMerges(results.getInt(CONFLICTING_MERGES));
                project.setNumberMergeRevisions(results.getInt(MERGE_REVISIONS));
                project.setNumberRevisions(results.getInt(REVISIONS));
                project.setPriva(results.getBoolean(PRIVATE));
                project.setRepositoryPath(results.getString(REPOSITORY_PATH));
                project.setSearchUrl(results.getString(SEARCH_URL));
                project.setUpdatedAt(results.getString(UPDATED_AT));

            }
        }

        return project;
    }

    public Project selectAllByProjectId(Long projectId) throws SQLException {
        Project project = this.selectByProjectId(projectId);

        LanguageJDBCDAO languageDAO = new LanguageJDBCDAO(database);
        project.setLanguages(languageDAO.selectAllByProjectId(project.getId()));

        RevisionJDBCDAO revisionDAO = new RevisionJDBCDAO(database);
        project.setRevisions(revisionDAO.selectAllByProjectId(project.getId()));

        return project;
    }
}
