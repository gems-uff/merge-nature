/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.gems.resources.vcs.Git;
import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.datastructure.MergeDependency;
import br.uff.ic.mergeguider.dependency.graph.ShowDependencies;
import br.uff.ic.mergeguider.metric.Assistance;
import br.uff.ic.mergeguider.metric.Noise;
import br.uff.ic.mergeguider.strategy.NodeDependency;
import br.uff.ic.mergeguider.strategy.PrepareNodes;
import br.uff.ic.mergeguider.strategy.Strategies;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gleiph
 */
public class ProjectAnalysis {

    public static int merges = 0, mergesNoDependencies = 0, mergesWithDependencies = 0,
            sequentialDCW = 0, moreOneChunk = 0, cleanMerge = 0;

    public static void projectAnalysis(String repositoryPath, String sandbox, boolean verbose) throws Exception {
        Git git = new Git(repositoryPath);
        List<String> mergeRevisions = Git.getMergeRevisions(repositoryPath);

        for (String mergeRevision : mergeRevisions) {
            merges++;

            System.out.println("=================================" + mergeRevision + "(" + mergeRevisions.indexOf(mergeRevision) + "/" + mergeRevisions.size() + ")==================================");

            printData();
            mergeAnalysis(repositoryPath, mergeRevision, sandbox, verbose);
        }

        printData();
    }

    public static void printData() {
        System.out.println("Merges: " + merges + " \nNo dependencies:" + mergesNoDependencies
                + " \nWith dependencies: " + mergesWithDependencies + " \nSequential different of Context Aware: " + sequentialDCW
                + "\n More than one chunk: " + moreOneChunk + "\n Clean merge: " + moreOneChunk);
    }

    public static void mergeAnalysis(String repositoryPath, String mergeSHA, String sandbox, boolean verbose) throws Exception {
        ShowDependencies showDependencies = new ShowDependencies();

        Git git = new Git(repositoryPath);
        List<String> parents = git.getParents(repositoryPath, mergeSHA);

        if (parents.size() != 2) {
            throw new Exception("Merge has number of tips different of 2");
        }

        showDependencies.setProjectPath(repositoryPath);
        showDependencies.setSHALeft(parents.get(0));
        showDependencies.setSHARight(parents.get(1));
        showDependencies.setSandbox(sandbox);

        try {
            MergeDependency dependencies = MergeGuider.performMerge(showDependencies.getProjectPath(), showDependencies.getSHALeft(),
                    showDependencies.getSHARight(), showDependencies.getSandbox());

            if (dependencies == null) {
                mergesNoDependencies++;
                cleanMerge++;
                if (verbose) {
                    System.out.println("Merge has no dependencies...");
                }

                return;
            }
            if (dependencies.getConflictingChunksAmount() < 2) {
                mergesNoDependencies++;
                if (verbose) {
                    System.out.println("Merge has no dependencies...");
                }
                return;
            }

            mergesWithDependencies++;

            List<NodeDependency> prepare = PrepareNodes.prepare(dependencies);

            if (verbose) {
                for (ConflictingChunkInformation cci : dependencies.getCcis()) {
                    cci.setLabel("CC" + dependencies.getCcis().indexOf(cci));
                    System.out.println(cci.getLabel());
                    System.out.println(cci);
                }
            }

            //Random strategy 
            if (verbose) {
                System.out.println("Random");
            }
            List<ConflictingChunkInformation> random = Strategies.random(dependencies, prepare);

            if (verbose) {
                for (ConflictingChunkInformation chunk : random) {

                    System.out.println(chunk.getLabel());
                }
            }

            if (verbose) {
                System.out.println("Sequencial");
            }
            List<ConflictingChunkInformation> sequencial = Strategies.sequencial(dependencies, prepare);

            if (verbose) {
                for (ConflictingChunkInformation chunk : sequencial) {
                    System.out.println(chunk.getLabel());
                }
            }

            if (verbose) {
                System.out.println("Greedy");
            }

            List<ConflictingChunkInformation> greed = Strategies.greedy(dependencies, prepare);

            if (verbose) {
                for (ConflictingChunkInformation chunk : greed) {
                    System.out.println(chunk.getLabel());
                }
            }

            if (verbose) {
                System.out.println("Context aware");
            }

            List<ConflictingChunkInformation> contextAware = Strategies.contextAware(dependencies, prepare);

            if (verbose) {
                for (ConflictingChunkInformation chunk : contextAware) {
                    System.out.println(chunk.getLabel());
                }
            }

            if (verbose) {
                for (ConflictingChunkInformation chunk : contextAware) {
                    System.out.println(chunk.getLabel() + " " + Assistance.chunkAssistance(chunk, contextAware, dependencies));

                }
            }

            System.out.println("Sequencial");
            System.out.println("Assistance " + Assistance.assistance(sequencial, dependencies));
            System.out.println("Noise: " + Noise.noise(sequencial, dependencies));

            System.out.println("Context aware");
            System.out.println("Assistance " + Assistance.assistance(contextAware, dependencies));
            System.out.println("Noise: " + Noise.noise(contextAware, dependencies));

            if (contextAware.size() > 1) {
                moreOneChunk++;
            }

            if (!contextAware.equals(sequencial)) {
                sequentialDCW++;
            }
        } catch (IOException ex) {
            Logger.getLogger(Strategies.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

//        String projectRepository = "/Users/gleiph/repositories/ATK";
//        String projectRepository = "/Users/gleiph/repositories/netty";
//                String projectRepository = "/Users/gleiph/repositories/izpack";
        String projectRepository = "/Users/gleiph/repositories/spring-data-neo4j";

//                String projectRepository = "/Users/gleiph/repositories/twitter4j";
//                String projectRepository = "/Users/gleiph/repositories/antlr4";
//                String projectRepository = "/Users/gleiph/repositories/voldemort";
//        String projectRepository = "/Users/gleiph/repositories/wro4j";
        //Netty spring-data-neo4j
        String mergeSHA = "042b1d";

        //Nuxeo revision
//        String mergeSHA = "5b45fc";
        String sandbox = "/Users/gleiph/repositories/icse2";

        try {
//            ProjectAnalysis.projectAnalysis(projectRepository, sandbox);
            mergeAnalysis(projectRepository, mergeSHA, sandbox, true);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

}
