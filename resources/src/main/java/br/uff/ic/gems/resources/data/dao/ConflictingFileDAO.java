/*
 * To change this license header, choose License Headers in ConflictingFile Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.ConflictingFile;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class ConflictingFileDAO {

    public ConflictingFile save(ConflictingFile conflictingFile) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (conflictingFile.getId() == null) {
                manager.persist(conflictingFile);
            } else {

                if (!manager.contains(conflictingFile)) {
                    if (manager.find(ConflictingFile.class, conflictingFile.getId()) == null) {
                        throw new Exception("Error during ConflictingFile persistence!");
                    }
                }

                conflictingFile = manager.merge(conflictingFile);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        } 

        return conflictingFile;
    }

    public void remove(ConflictingFile conflictingFile) {
        EntityManager manager = DatabaseManager.getManager();

        if (conflictingFile.getId() != null) {
            ConflictingFile conflictingFileRemove = manager.find(ConflictingFile.class, conflictingFile.getId());

            try {
                manager.getTransaction().begin();
                manager.remove(conflictingFileRemove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            } 
        }
    }

    public ConflictingFile getById(Long id) {
        ConflictingFile conflictingFile = null;
        EntityManager manager = DatabaseManager.getManager();

        try {
            conflictingFile = manager.find(ConflictingFile.class, id);
        } catch (Exception e) {
            throw e;
        } 

        return conflictingFile;
    }
}
