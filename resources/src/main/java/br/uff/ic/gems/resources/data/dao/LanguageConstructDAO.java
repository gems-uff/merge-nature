/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.LanguageConstruct;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class LanguageConstructDAO {
    
    public LanguageConstruct save(LanguageConstruct languageConstruct) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (languageConstruct.getId() == null) {
                manager.persist(languageConstruct);
            } else {

                if (!manager.contains(languageConstruct)) {
                    if (manager.find(LanguageConstruct.class, languageConstruct.getId()) == null) {
                        throw new Exception("Error during LanguageConstruct persistence!");
                    }
                }
                
                languageConstruct = manager.merge(languageConstruct);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        }

        return languageConstruct;
    }

    public void remove(LanguageConstruct languageConstruct) {
        EntityManager manager = DatabaseManager.getManager();

        if (languageConstruct.getId() != null) {
            LanguageConstruct languageConstructRemove = manager.find(LanguageConstruct.class, languageConstruct.getId());

            try {
                manager.getTransaction().begin();
                manager.remove(languageConstructRemove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    public LanguageConstruct getById(Long id){
        LanguageConstruct languageConstruct = null;
        EntityManager manager = DatabaseManager.getManager();
        
        try{
            languageConstruct = manager.find(LanguageConstruct.class, id);
        } catch(Exception e){
            throw e;
        }
        
        return languageConstruct;
    }
}
