/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class ConflictingChunkDAO {

    public ConflictingChunk save(ConflictingChunk conflictingChunk) throws Exception {

        EntityManager manager = DatabaseManager.getManager();
        try {
            manager.getTransaction().begin();
            if (conflictingChunk.getId() == null) {
                manager.persist(conflictingChunk);
            } else {

                if (!manager.contains(conflictingChunk)) {
                    if (manager.find(ConflictingChunk.class, conflictingChunk.getId()) == null) {
                        throw new Exception("Error during ConflictingChunk persistence!");
                    }
                }

                conflictingChunk = manager.merge(conflictingChunk);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        }
        return conflictingChunk;
    }

    public void remove(ConflictingChunk conflictingChunk) {
        EntityManager manager = DatabaseManager.getManager();

        if (conflictingChunk.getId() != null) {
            ConflictingChunk remove = manager.find(ConflictingChunk.class, conflictingChunk.getId());
            try {
                manager.getTransaction().begin();
                manager.remove(remove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            }
        }
    }
    
    public ConflictingChunk getById(Long id){
        EntityManager manager = DatabaseManager.getManager();
        ConflictingChunk conflictingChunk = null;
        
        try{
            conflictingChunk = manager.find(ConflictingChunk.class, id);
        }catch(Exception e){
            throw e;
        }
        
        return conflictingChunk;
    }
}
