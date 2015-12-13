package br.uff.ic.github.github;

import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.github.parser.GithubAPI;
import java.sql.Connection;

/**
 *
 * @author Gleiph
 */
public class Main {
 
    public static void main(String[] args) {

        String database = "apagarLogo";
        
        try (Connection connection = (new JDBCConnection()).getConnection(database)) {

            GithubAPI.init();
            GithubAPI.projects(connection);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
