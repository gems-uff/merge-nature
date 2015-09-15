/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.Language;
import java.sql.SQLException;

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
}
