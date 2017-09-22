/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.projectviewer.bean;

import br.uff.ic.kraken.projectviewer.pages.PagesName;

/**
 *
 * @author gleiph
 */
public class Navegation {
    
    public String projects(){
        
        System.out.println("teste");
        
        return PagesName.projects + "?faces-redirect=true";
    }
    
}
