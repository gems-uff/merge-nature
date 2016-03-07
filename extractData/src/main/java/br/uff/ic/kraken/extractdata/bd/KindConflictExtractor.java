/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.kraken.extractdata.bd;

import br.uff.ic.gems.resources.data.ConflictingChunk;
import br.uff.ic.gems.resources.data.KindConflict;
import br.uff.ic.gems.resources.data.dao.sql.ConflictingChunkJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.JDBCConnection;
import br.uff.ic.gems.resources.data.dao.sql.KindConflictJDBCDAO;
import br.uff.ic.gems.resources.data.dao.sql.Side;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class KindConflictExtractor {

    public static void main(String[] args) {

        String bdName = "automaticAnalysis";

        try (Connection connection = (new JDBCConnection()).getConnection(bdName)) {

            ConflictingChunkJDBCDAO conflictingChunkDAO = new ConflictingChunkJDBCDAO(connection);
            KindConflictJDBCDAO kindConflictDAO = new KindConflictJDBCDAO(connection);

            System.out.println(new Date());
            List<ConflictingChunk> conflictingChunks = conflictingChunkDAO.selectNullGeneralKindConflictOutmost();
            System.out.println("Size = " + conflictingChunks.size());
            System.out.println(new Date());

            String generalKindConflictOutmost;
            for (ConflictingChunk conflictingChunk : conflictingChunks) {

                generalKindConflictOutmost = "";

        List<KindConflict> kindConflicts = kindConflictDAO.selectAllByConflictingChunkId(conflictingChunk.getId());

        for (KindConflict kindConflict : kindConflicts) {
            if (kindConflict.getSide().equals(Side.LEFT)) {
                conflictingChunk.setLeftKindConflict(kindConflict);
            } else if (kindConflict.getSide().equals(Side.RIGHT)) {
                conflictingChunk.setRightKindConflict(kindConflict);
            } else {
                System.out.println("------------------------------------------------------");
                System.out.println("                      Problem!!!!!!!!");
                System.out.println("------------------------------------------------------");

            }
        }

        List<String> generalKindConflict = conflictingChunk.generalKindConflict();

        for (int i = 0; i < generalKindConflict.size() - 1; i++) {
//                    System.out.print(generalKindConflict.get(i) + ", ");
            generalKindConflictOutmost += generalKindConflict.get(i) + ", ";
        }

//                System.out.println(generalKindConflict.get(generalKindConflict.size() - 1));
        generalKindConflictOutmost += generalKindConflict.get(generalKindConflict.size() - 1);

        conflictingChunkDAO.updateGeneralKindConflict(conflictingChunk.getId(), generalKindConflictOutmost);
    }

        } catch (SQLException ex) {
            Logger.getLogger(KindConflictExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
