/*
 * To change this license header, choose License Headers in Revision Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.data.dao;

import br.uff.ic.gems.resources.data.Revision;
import br.uff.ic.gems.resources.jpa.DatabaseManager;
import javax.persistence.EntityManager;

/**
 *
 * @author gleiph
 */
public class RevisionDAO {

    public Revision save(Revision revision) throws Exception {
        EntityManager manager = DatabaseManager.getManager();

        try {
            manager.getTransaction().begin();

            if (revision.getId() == null) {
                manager.persist(revision);
            } else {

                if (!manager.contains(revision)) {
                    if (manager.find(Revision.class, revision.getId()) == null) {
                        throw new Exception("Error during Revision persistence!");
                    }
                }

                revision = manager.merge(revision);
            }

            manager.getTransaction().commit();
        } catch (Exception e) {
            throw e;
        } 

        return revision;
    }

    public void remove(Revision revision) {
        EntityManager manager = DatabaseManager.getManager();

        if (revision.getId() != null) {
            Revision revisionRemove = manager.find(Revision.class, revision.getId());

            try {
                manager.getTransaction().begin();
                manager.remove(revisionRemove);
                manager.getTransaction().commit();
            } catch (Exception e) {
                throw e;
            } 
        }
    }

    public Revision getById(Long id) {
        Revision revision = null;
        EntityManager manager = DatabaseManager.getManager();

        try {
            revision = manager.find(Revision.class, id);
        } catch (Exception e) {
            throw e;
        } 

        return revision;
    }
}
