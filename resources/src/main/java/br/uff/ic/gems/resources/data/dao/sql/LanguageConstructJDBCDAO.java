/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.LanguageConstruct;
import java.sql.SQLException;

/**
 *
 * @author gleiph
 */
public class LanguageConstructJDBCDAO {

    public static final String ID = "id";
    public static final String BEGIN_COLUMN = "begincolumn";
    public static final String BEGIN_COLUMN_BLOCK = "begincolumnblock";
    public static final String BEGIN_LINE = "beginline";
    public static final String BEGIN_LINE_BLOCK = "beginlineblock";
    public static final String END_COLUMN = "endcolumn";
    public static final String END_COLUMN_BLOCK = "endcolumnblock";
    public static final String END_LINE = "endline";
    public static final String END_LINE_BLOCK = "endlineblock";
    public static final String HAS_BLOCK = "hasblock";
    public static final String NAME = "name";

    public static final String KIND_CONFLICT_ID = "kindconflict_id";

    public Long insert(LanguageConstruct languageConstruct, Long kindConflict_id) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.LANGUAGE_CONSTRUCT + "("
                + BEGIN_COLUMN + ", "
                + BEGIN_COLUMN_BLOCK + ", "
                + BEGIN_LINE + ", "
                + BEGIN_LINE_BLOCK + ", "
                + END_COLUMN + ", "
                + END_COLUMN_BLOCK + ", "
                + END_LINE + ", "
                + END_LINE_BLOCK + ", "
                + HAS_BLOCK + ", "
                + NAME + ", "
                + KIND_CONFLICT_ID
                + ") "
                + "VALUES(\'"
                + languageConstruct.getBeginColumn() + "\', \'"
                + languageConstruct.getBeginColumnBlock() + "\', \'"
                + languageConstruct.getBeginLine() + "\', \'"
                + languageConstruct.getBeginLineBlock() + "\', \'"
                + languageConstruct.getEndColumn() + "\', \'"
                + languageConstruct.getEndColumnBlock() + "\', \'"
                + languageConstruct.getEndLine() + "\', \'"
                + languageConstruct.getEndLineBlock() + "\', \'"
                + languageConstruct.isHasBlock() + "\', \'"
                + languageConstruct.getName() + "\', \'"
                + kindConflict_id
                + "\')";

        return DefaultOperations.insert(insertSQL);

    }

    public Long insertAll(LanguageConstruct languageConstruct, Long kindConflict_id) throws SQLException {
        return this.insert(languageConstruct, kindConflict_id);
    }

}
