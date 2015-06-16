/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.gems.resources.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author gleiph
 */
public class DatabaseManager {

    private static EntityManagerFactory instance = null;

    public DatabaseManager() {
    }

    public static EntityManager getManager() {
        if (instance == null) {
//            instance = Persistence.createEntityManagerFactory("Local");
            instance = Persistence.createEntityManagerFactory("Test");
        }

        return instance.createEntityManager();
    }
}
