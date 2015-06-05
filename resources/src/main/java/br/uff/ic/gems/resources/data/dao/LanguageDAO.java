/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.Language;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class LanguageDAO {
    public Language save(Language language) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (language.getId() == null) {
                manager.persist(language);
            } else {

                if (!manager.contains(language)) {
                    if (manager.find(Language.class, language.getId()) == null) {
                        throw new Exception("Error during Language persistence!");
                    }
                }
                
                language = manager.merge(language);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        }

        return language;
    }

    public void remove(Language language) {
        EntityManager manager = DatabaseManager.getManager();

        if (language.getId() != null) {
            Language languageRemove = manager.find(Language.class, language.getId());

            try {
                manager.getTransaction().begin();
                manager.remove(languageRemove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    public Language getById(Long id){
        Language language = null;
        EntityManager manager = DatabaseManager.getManager();
        
        try{
            language = manager.find(Language.class, id);
        } catch(Exception e){
            throw e;
        }
        
        return language;
    }
}
