/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.github.github.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Gleiph
 */
@Entity
public class Project implements Serializable {

    @Id
    private Long id;
    
    private String name;
    private String createdAt;
    private String updatedAt;
    private boolean priva;
    private String htmlUrl;
    private String searchUrl;
    private int developers;
    
    @OneToMany
    private List<Language> languages;

    public Project() {
        this.id = 0l;
        this.name = null;
        this.createdAt = null;
        this.updatedAt = null;
        this.priva = false;
        this.htmlUrl = null;
        this.searchUrl = null;
        this.developers = 0;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the priva
     */
    public boolean isPriva() {
        return priva;
    }

    /**
     * @param priva the priva to set
     */
    public void setPriva(boolean priva) {
        this.priva = priva;
    }

    /**
     * @return the htmlUrl
     */
    public String getHtmlUrl() {
        return htmlUrl;
    }

    /**
     * @param htmlUrl the htmlUrl to set
     */
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    

    @Override
    public String toString() {
        String result = "";

        result = this.name + "\n";
        result += "\t" + this.createdAt + "\n";
        result += "\t" + this.updatedAt + "\n";
        result += "\t" + this.getSearchUrl() + "\n";
        result += "\t" + this.htmlUrl + "\n";
        result += "\t" + this.developers + "\n";
        result += "\tprivate: " + this.priva;

        return result;
    }

    /**
     * @return the searchUrl
     */
    public String getSearchUrl() {
        return searchUrl;
    }

    /**
     * @param searchUrl the searchUrl to set
     */
    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    /**
     * @return the developers
     */
    public int getDevelopers() {
        return developers;
    }

    /**
     * @param developers the developers to set
     */
    public void setDevelopers(int developers) {
        this.developers = developers;
    }

    /**
     * @return the languages
     */
    public List<Language> getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
}
