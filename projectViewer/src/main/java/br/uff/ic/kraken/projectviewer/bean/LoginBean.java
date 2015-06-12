/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.kraken.projectviewer.pages.PagesName;
import java.io.Serializable;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author gleiph
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean implements Serializable{

    private String username;
    private String password;
    private boolean isUsernameValid;
    private boolean isPasswordValid;
    private boolean validationComplete = false;

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the isPasswordValid
     */
    public boolean isIsPasswordValid() {
        return isPasswordValid;
    }

    /**
     * @param isPasswordValid the isPasswordValid to set
     */
    public void setIsPasswordValid(boolean isPasswordValid) {
        this.isPasswordValid = isPasswordValid;
    }

    /**
     * @return the validationComplete
     */
    public boolean isValidationComplete() {
        return validationComplete;
    }

    /**
     * @param validationComplete the validationComplete to set
     */
    public void setValidationComplete(boolean validationComplete) {
        this.validationComplete = validationComplete;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the isUsernameValid
     */
    public boolean isIsUsernameValid() {
        return isUsernameValid;
    }

    /**
     * @param isUsernameValid the isUsernameValid to set
     */
    public void setIsUsernameValid(boolean isUsernameValid) {
        this.isUsernameValid = isUsernameValid;
    }

    public String checkValidity() {
        if (this.username == null || this.username.equals("")) {
            isUsernameValid = false;
        } else if (this.username.equals("gleiph")) {
            isUsernameValid = true;
        } else {
            isUsernameValid = false;
        }

        if (this.password == null || this.password.equals("")) {
            isPasswordValid = false;
        } else if (this.password.equals("ghiotto")) {
            isPasswordValid = true;
        } else {
            isPasswordValid = false;
        }

        validationComplete = true;

        if (isUsernameValid && isPasswordValid) {
            return PagesName.projects;
        }else
            return PagesName.index;
    }

}
