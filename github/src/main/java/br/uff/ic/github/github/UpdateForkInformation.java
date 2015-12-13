/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github;

import br.uff.ic.gems.resources.data.Fork;
import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.sql.ForkJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.github.parser.GithubAPI;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class UpdateForkInformation {

    public static void main(String[] args) {

        String database = "automaticAnalysis";
        GithubAPI.init();

        try (Connection connection = (new JDBCConnection()).getConnection(database)) {

            ProjectJDBCDAO projectJDBCDAO = new ProjectJDBCDAO(connection);
            ForkJDBCDAO forkJDBCDAO = new ForkJDBCDAO(connection);
            
            List<Project> projects = projectJDBCDAO.selectWithoutForkInformation();

            for (Project project : projects) {
                String searchUrl = project.getSearchUrl();
                System.out.println("searchUrl = " + searchUrl);
                Project projectUpdated = GithubAPI.project(searchUrl, false, false, true);


                projectJDBCDAO.updateForkInformation(projectUpdated);
                
                
                for (Fork fork : projectUpdated.getForks()) {
                    forkJDBCDAO.insertAll(fork);
                }
                

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
