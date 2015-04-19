package br.uff.ic.github.github.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
    private String message;

    @OneToMany(cascade = CascadeType.PERSIST)
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
        this.message = null;
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

        result = this.getName() + "\n";
        result += "\t" + this.getCreatedAt() + "\n";
        result += "\t" + this.getUpdatedAt() + "\n";
        result += "\t" + this.getSearchUrl() + "\n";
        result += "\t" + this.getHtmlUrl() + "\n";
        result += "\t" + this.getDevelopers() + "\n";
        result += "\tprivate: " + this.isPriva();

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
    
    public static Project getProject(Long id, EntityManager manager){
        return manager.find(Project.class, id);
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
