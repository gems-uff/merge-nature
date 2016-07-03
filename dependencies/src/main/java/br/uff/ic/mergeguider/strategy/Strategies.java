/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.strategy;

import br.uff.ic.mergeguider.MergeGuider;
import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.datastructure.MergeDependency;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class Strategies {

    public static void main(String[] args) {
        String projectPath = "/Users/gleiph/repositories/voldemort";
        String shaLeft = "aee112d9ef0ed960c7bc9955d7e85e6ed6ac91a0";
        String shaRight = "fd5dbeb5113ffed51cf1836ac78b129a4bea4cb6";
        String sanbox = "/Users/gleiph/repositories/icse/";

        try {
            MergeDependency dependencies = MergeGuider.performMerge(projectPath, shaLeft, shaRight, sanbox);

            List<NodeDependency> prepare = PrepareNodes.prepare(dependencies);

            List<ConflictingChunkInformation> resolutionOrder = Strategies.random(dependencies, prepare);

            for (ConflictingChunkInformation chunk : resolutionOrder) {
                System.out.println(chunk);
            }

        } catch (IOException ex) {
            Logger.getLogger(Strategies.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<ConflictingChunkInformation> random(MergeDependency mergeDependency, List<NodeDependency> nodesDependency) {

        Random rand = new Random();
        List<ConflictingChunkInformation> ccis = mergeDependency.getCcis();
        List<ConflictingChunkInformation> result = new ArrayList<>();

        while (!ccis.isEmpty()) {
            int randomNumber = rand.nextInt(ccis.size());
            System.out.println("randomNumber = " + randomNumber);

            ConflictingChunkInformation remove = ccis.remove(randomNumber);
            result.add(remove);
        }

        return result;
    }

}
