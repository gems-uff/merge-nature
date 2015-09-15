/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.LanguageConstruct;
import java.sql.SQLException;

/**
 *
 * @author gleiph
 */
public class KindConflictJDBCDAO {

    public static final String ID = "id";
    public static final String BEGIN_LINE = "beginline";
    public static final String END_LINE = "endline";
    public static final String SIDE = "side";

    public static final String CONFLICTING_CHUNK_ID = "conflictingchunk_id";

    public Long insert(KindConflict kindConflict, Long conflictingChunkId, Side side) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.KIND_CONFLICT
                + "("
                + BEGIN_LINE + ", "
                + END_LINE + ", "
                + SIDE + ", "
                + CONFLICTING_CHUNK_ID + ") "
                + "VALUES(\'"
                + kindConflict.getBeginLine() + "\', \'"
                + kindConflict.getEndLine() + "\', \'"
                + side.toString() + "\', \'"
                + conflictingChunkId + "\')";

        return DefaultOperations.insert(insertSQL);

    }

    public Long insertAll(KindConflict kindConflict, Long conflictingChunkId, Side side) throws SQLException {

        LanguageConstructJDBCDAO languageConstructDAO = new LanguageConstructJDBCDAO();

        Long kindConflictId = this.insert(kindConflict, conflictingChunkId, side);

        //Adding language constructs
        for (LanguageConstruct languageConstruct : kindConflict.getLanguageConstructs()) {
            languageConstructDAO.insertAll(languageConstruct, kindConflictId);
        }

        return kindConflictId;

    }
}
