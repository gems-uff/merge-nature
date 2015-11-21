/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.LanguageConstruct;
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
public class KindConflictJDBCDAO {

    public static final String ID = "id";
    public static final String BEGIN_LINE = "beginline";
    public static final String END_LINE = "endline";
    public static final String SIDE = "side";

    public static final String CONFLICTING_CHUNK_ID = "conflictingchunk_id";

    private final String database;

    public KindConflictJDBCDAO(String database) {
        this.database = database;
    }

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

        return DefaultOperations.insert(insertSQL, database);

    }

    public Long insertAll(KindConflict kindConflict, Long conflictingChunkId, Side side) throws SQLException {

        LanguageConstructJDBCDAO languageConstructDAO = new LanguageConstructJDBCDAO(database);

        Long kindConflictId = this.insert(kindConflict, conflictingChunkId, side);

        //Adding language constructs
        for (LanguageConstruct languageConstruct : kindConflict.getLanguageConstructs()) {
            languageConstructDAO.insertAll(languageConstruct, kindConflictId);
        }

        return kindConflictId;

    }

    public List<KindConflict> selectByConflictingChunkId(Long conflictingChunkId) throws SQLException {
        List<KindConflict> kindConflicts = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.KIND_CONFLICT
                + " WHERE " + CONFLICTING_CHUNK_ID + " = " + conflictingChunkId;

        try (Connection connection = (new JDBCConnection()).getConnection(database);
                Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                KindConflict kindConflict = new KindConflict();

                kindConflict.setBeginLine(results.getInt(BEGIN_LINE));
                kindConflict.setEndLine(results.getInt(END_LINE));
                kindConflict.setId(results.getLong(ID));

                String string = results.getString(SIDE);

                if (Side.LEFT.toString().equals(string)) {
                    kindConflict.setSide(Side.LEFT);
                } else if (Side.RIGHT.toString().equals(string)) {
                    kindConflict.setSide(Side.RIGHT);
                } else {
                    throw new SQLException("There is no kind of conflict side.");
                }

                kindConflicts.add(kindConflict);

            }
        }

        return kindConflicts;
    }

    public List<KindConflict> selectAllByConflictingChunkId(Long conflictingChunkId) throws SQLException {
        LanguageConstructJDBCDAO languageConstructDAO = new LanguageConstructJDBCDAO(database);

        List<KindConflict> kindConflicts = this.selectByConflictingChunkId(conflictingChunkId);

        for (KindConflict kindConflict : kindConflicts) {
            kindConflict.setLanguageConstructs(languageConstructDAO.selectAllByKindConflictId(kindConflict.getId()));
        }

        return kindConflicts;
    }
}
