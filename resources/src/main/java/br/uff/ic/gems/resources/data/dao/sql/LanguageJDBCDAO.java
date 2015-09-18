/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.Language;
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
public class LanguageJDBCDAO {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PERCENTAGE = "percentage";
    public static final String SIZE = "size";

    public static final String PROJECT_ID = "project_id";

    public Long insert(Language language, Long projectId) throws SQLException {

        String insertSQL = "INSERT INTO " + Tables.LANGUAGE
                + "("
                + NAME + ", "
                + PERCENTAGE + ", "
                + SIZE + ", "
                + PROJECT_ID
                + ") "
                + "VALUES(\'"
                + language.getName() + "\', \'"
                + language.getPercentage() + "\', \'"
                + language.getSize() + "\', \'"
                + projectId
                + "\')";

        return DefaultOperations.insert(insertSQL);

    }

    public Long insertAll(Language language, Long projectId) throws SQLException {
        return this.insert(language, projectId);
    }
    
    public List<Language> selectByProjectId(Long projectId) throws SQLException {
        List<Language> languages = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.LANGUAGE
                + " WHERE " + PROJECT_ID + " = " + projectId;

        try (Connection connection = (new JDBCConnection()).getConnection(Tables.DATABASE);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                Language language = new Language();
                
                language.setId(results.getLong(ID));
                language.setName(results.getString(NAME));
                language.setPercentage(results.getDouble(PERCENTAGE));
                language.setSize(results.getDouble(SIZE));

                languages.add(language);

            }
        }

        return languages;
    }
    
        public List<Language> selectAllByProjectId(Long projectId) throws SQLException {
            return this.selectByProjectId(projectId);
        }

}
