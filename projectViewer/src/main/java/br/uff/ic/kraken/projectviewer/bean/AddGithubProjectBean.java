/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.gems.resources.data.Project;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.ProjectJDBCDAO;
import br.uff.ic.gems.resources.github.parser.GithubAPI;
import br.uff.ic.kraken.projectviewer.pages.PagesName;
import br.uff.ic.kraken.projectviewer.utils.DatabaseConfiguration;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */
@Named(value = "addGithubProjectBean")
@RequestScoped
public class AddGithubProjectBean implements Serializable {

    private String htmlURL;

    /**
     * @return the htmlURL
     */
    public String getHtmlURL() {
        return htmlURL;
    }

    /**
     * @param htmlURL the htmlURL to set
     */
    public void setHtmlURL(String htmlURL) {
        this.htmlURL = htmlURL;
    }

    public String saveProject() {//https://api.github.com/repos/yusuke/twitter4j
        String githubURL = htmlURL.replace("https://github.com/", "https://api.github.com/repos/");

        if (githubURL.endsWith("/")) {
            githubURL = githubURL.substring(0, githubURL.length() - 1);
        }

        try (Connection connection = (new JDBCConnection()).getConnection(DatabaseConfiguration.database)) {
            ProjectJDBCDAO projectDAO = new ProjectJDBCDAO(connection);

            Project project;
            GithubAPI.init();

            project = GithubAPI.project(githubURL);

            try {
                projectDAO.insertAll(project);
            } catch (Exception ex) {
                Logger.getLogger(AddGithubProjectBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            return null;
        }
        return PagesName.projects;
    }
}
