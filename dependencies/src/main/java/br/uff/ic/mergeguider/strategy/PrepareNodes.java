/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.strategy;

import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.datastructure.ConflictingChunksDependency;
import br.uff.ic.mergeguider.datastructure.MergeDependency;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class PrepareNodes {

//    public static void main(String[] args) {
//
//        String projectPath = "/Users/gleiph/repositories/voldemort";
//        String shaLeft = "aee112d9ef0ed960c7bc9955d7e85e6ed6ac91a0";
//        String shaRight = "fd5dbeb5113ffed51cf1836ac78b129a4bea4cb6";
//        String sanbox = "/Users/gleiph/repositories/icse/";
//
//        try {
//            MergeDependency dependencies = MergeGuider.performMerge(projectPath, shaLeft, shaRight, sanbox);
//
//            List<NodeDependency> prepare = prepare(dependencies);
//
//            for (NodeDependency p : prepare) {
//                System.out.println(p.toString());
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(PrepareNodes.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

    public static List<NodeDependency> prepare(MergeDependency dependency) {

        List<NodeDependency> nodes = new ArrayList<>();

        List<ConflictingChunkInformation> ccis = dependency.getCcis();
        List<ConflictingChunksDependency> conflictingChunksDependencies = dependency.getConflictingChunksDependencies();

        for (ConflictingChunksDependency conflictingChunksDependency : conflictingChunksDependencies) {
            System.out.println("CC" + ccis.indexOf(conflictingChunksDependency.getReference()) + " depends on "
                    + "CC" + ccis.indexOf(conflictingChunksDependency.getDependsOn()));
        }

        int dependencies = 0;//How many nodes the current one depends on 
        int dependent = 0;//How many nodes depends of the current one 

        for (ConflictingChunkInformation cci : ccis) {
            NodeDependency node = new NodeDependency();

            System.out.println("CC"+ccis.indexOf(cci));
            
            node.setBegin(cci.getBegin());
            node.setSeparator(cci.getSeparator());
            node.setEnd(cci.getEnd());
            node.setFile(new File(cci.getFilePath()));
            dependencies = 0;
            dependent = 0;

            for (ConflictingChunksDependency conflictingChunksDependency : conflictingChunksDependencies) {

                ConflictingChunkInformation reference = conflictingChunksDependency.getReference();
                ConflictingChunkInformation dependsOn = conflictingChunksDependency.getDependsOn();

                if (cci.equals(reference)) {
                    dependencies++;
                }

                if (cci.equals(dependsOn)) {
                    dependent++;
                }
            }

            node.setDependencies(dependencies);
            node.setDependent(dependent);
            System.out.println("dependencies = " + dependencies);
            System.out.println("dependent = " + dependent);
            nodes.add(node);

        }

        return nodes;
    }
}
