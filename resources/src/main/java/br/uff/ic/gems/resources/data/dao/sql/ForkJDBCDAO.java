/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao.sql;

import br.uff.ic.gems.resources.data.Fork;
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
public class ForkJDBCDAO {

    private static final String FORK_ID = "FORK_ID";
    private static final String FORK_URL = "FORK_URL";

    private static final String PROJECT_ID = "PROJECT_ID";

    private final Connection connection;

    public ForkJDBCDAO(Connection connection) {
        this.connection = connection;
    }

    public Long insert(Fork fork) throws SQLException {
        String insertSQL = "INSERT INTO " + Tables.REVISION
                + "("
                + FORK_ID + ", "
                + FORK_URL+ ", "
                + PROJECT_ID
                + ")"
                + " VALUES(\'"
                + fork.getForkId() + "\', \'"
                + fork.getForkURL()+ "\', \'"
                + fork.getProjectId()
                + "\')";

        return DefaultOperations.insert(insertSQL, connection);

    }

    public Long insertAll(Fork fork) throws SQLException {
        return insert(fork);
    }

    public List<Fork> selectByProjectId(Long projectId) throws SQLException {
        List<Fork> forks = new ArrayList<>();

        String query = "SELECT * FROM " + Tables.FORK
                + " WHERE " + PROJECT_ID + " = " + projectId;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);

            ResultSet results = statement.getResultSet();

            while (results.next()) {
                Fork fork = new Fork();

                fork.setForkId(results.getLong(FORK_ID));
                fork.setForkURL(results.getString(FORK_URL));
                fork.setProjectId(results.getLong(PROJECT_ID));

                forks.add(fork);

            }
        }

        return forks;
    }

    public List<Fork> selectAllByProjectId(Long projectId) throws SQLException {
        return this.selectByProjectId(projectId);
    }
}
