/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class KindConflictDAO {

    public KindConflict save(KindConflict kindConflict) throws Exception {

        EntityManager manager = DatabaseManager.getManager();
        try {
            manager.getTransaction().begin();
            if (kindConflict.getId() == null) {
                manager.persist(kindConflict);
            } else {

                if (!manager.contains(kindConflict)) {
                    if (manager.find(KindConflict.class, kindConflict.getId()) == null) {
                        throw new Exception("Error during KindConflict persistence!");
                    }
                }

                kindConflict = manager.merge(kindConflict);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        } finally {
            manager.close();
        }
        return kindConflict;
    }

    public void remove(KindConflict kindConflict) {
        EntityManager manager = DatabaseManager.getManager();

        if (kindConflict.getId() != null) {
            KindConflict remove = manager.find(KindConflict.class, kindConflict.getId());
            try {
                manager.getTransaction().begin();
                manager.remove(remove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            } finally {
                manager.close();
            }
        }
    }

    public KindConflict getById(Long id) {
        EntityManager manager = DatabaseManager.getManager();
        KindConflict kindConflict = null;

        try {
            kindConflict = manager.find(KindConflict.class, id);
        } catch (Exception e) {
            throw e;
        } finally {
            manager.close();
        }

        return kindConflict;
    }

}
