/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.strategy;

import br.uff.ic.mergeguider.MergeGuider;
import br.uff.ic.mergeguider.datastructure.ConflictingChunkInformation;
import br.uff.ic.mergeguider.datastructure.ConflictingChunksDependency;
import br.uff.ic.mergeguider.datastructure.MergeDependency;
import br.uff.ic.mergeguider.dependency.graph.ShowDependencies;
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
//        String projectPath = "/Users/gleiph/repositories/voldemort";
//        String shaLeft = "aee112d9ef0ed960c7bc9955d7e85e6ed6ac91a0";
//        String shaRight = "fd5dbeb5113ffed51cf1836ac78b129a4bea4cb6";
//        String sanbox = "/Users/gleiph/repositories/icse/";

        //First case
//        String projectPath = "/Users/gleiph/repositories/dependencies/android_libcore";
//        String shaLeft = "a7249ef";
//        String shaRight = "217766d";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
//        
        //Second case 
//        String projectPath = "/Users/gleiph/repositories/dependencies/wro4j";
//        String shaLeft = "6de49bc";
//        String shaRight = "e8b80e4";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Third case 
//        String projectPath = "/Users/gleiph/repositories/dependencies/xmlcalabash1";
//        String shaLeft = "2df89cc";
//        String shaRight = "9976a12";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Fourth case 
//        String projectPath = "/Users/gleiph/repositories/dependencies/mifosx";
//        String shaLeft = "b13156b";
//        String shaRight = "3531e95";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Fifth case
//        String projectPath = "/Users/gleiph/repositories/dependencies/structr";
//        String shaLeft = "82c2183";
//        String shaRight = "fd5e7ce";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Sixth case
//        String projectPath = "/Users/gleiph/repositories/dependencies/wro4j";
//        String shaLeft = "12e7c35";
//        String shaRight = "d72611f";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Seventh case
//        String projectPath = "/Users/gleiph/repositories/dependencies/platform_packages_apps_camera";
//        String shaLeft = "1c01f0a";
//        String shaRight = "85b0f1e";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Eighth case
//        String projectPath = "/Users/gleiph/repositories/dependencies/jedis";
//        String shaLeft = "3da2f2d ";
//        String shaRight = "dd88c51";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
//        
        //Nineth case
//        String projectPath = "/Users/gleiph/repositories/dependencies/android-sdk";
//        String shaLeft = "90d46ad";
//        String shaRight = "1028078";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Tenth case
//        String projectPath = "/Users/gleiph/repositories/dependencies/jira-plugin";
//        String shaLeft = "824817d";
//        String shaRight = "b509c1e";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //Eleventh case
//        String projectPath = "/Users/gleiph/repositories/dependencies/atlas-feeds";
//        String shaLeft = "fa86e3a";
//        String shaRight = "ad7cf55";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //12th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/Phenex";
//        String shaLeft = "d0cb4ef";
//        String shaRight = "e763873";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
         //13th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/pojobuilder";
//        String shaLeft = "74ce4a4";
//        String shaRight = "5464dac";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        //14th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/RSBot";
//        String shaLeft = "7b65f04";
//        String shaRight = "a75ff5d";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
       
        //15th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/OpenMEAP";
//        String shaLeft = "4c349da";
//        String shaRight = "ba95c08";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //16th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/fongo";
//        String shaLeft = "82b0ecd";
//        String shaRight = "2651ca0";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //17th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/atlas";
//        String shaLeft = "28fdffa";
//        String shaRight = "e3d7e32";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //18th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/h-store";
//        String shaLeft = "923e155";
//        String shaRight = "cb3481c";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //19th case
//        String projectPath = "/Users/gleiph/repositories/dependencies/hector";
//        String shaLeft = "b5719dc";
//        String shaRight = "218c401";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

//        //20th case: ne dependencies
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/eucalyptus";
//        String shaLeft = "882da8f";
//        String shaRight = "4cf8803";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//        //21th case
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/MinecraftForge";
//        String shaLeft = "7179edf";
//        String shaRight = "ca67d6e";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

        
        //21st case
        
//        String projectPath = "/Users/gleiph/repositories/dependencies/xp";
//        String shaLeft = "4187dc4";
//        String shaRight = "833e80c";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//        //22nd case
//        
        String projectPath = "/Users/gleiph/repositories/dependencies/spring-data-neo4j";
        String shaLeft = "3ba54fd";
        String shaRight = "4a8f404";
        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//         //23rd case
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/rhostudio";
//        String shaLeft = "af6f46d";
//        String shaRight = "7e006ca";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //24th case
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/SAIM";
//        String shaLeft = "5650a81";
//        String shaRight = "bbb3f6b";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//        //25th case (error)
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/IRIS";
//        String shaLeft = "d299199";
//        String shaRight = "b3b0c2a";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

        
//        //25th case (Error)
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/Custom-Salem";
//        String shaLeft = "34f8ae8";
//        String shaRight = "d674785";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

        //25th case 
        
//        String projectPath = "/Users/gleiph/repositories/dependencies/modeler";
//        String shaLeft = "907163e";
//        String shaRight = "390ede6";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //26th case (Error)
        
//        String projectPath = "/Users/gleiph/repositories/dependencies/qcadoo";
//        String shaLeft = "9b4a717";
//        String shaRight = "04039a5";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//        //26th case 
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/jackson-databind";
//        String shaLeft = "54b2a5f";
//        String shaRight = "7d5c50b";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //27th case  (ERror)
        
//        String projectPath = "/Users/gleiph/repositories/dependencies/iNaturalistAndroid";
//        String shaLeft = "16adb20 ";
//        String shaRight = "9971469";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//        //27th case (Error)
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/NoTube-Beancounter-2.0";
//        String shaLeft = "9cfc636";
//        String shaRight = "6f2d3a1";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
//        
//        //27th case 
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/wro4j";
//        String shaLeft = "4fc63ec";
//        String shaRight = "40fa11f";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

//        //28th case : 05d907e92af053437242c8fbb006d82772d73881
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/lorsource";
//        String shaLeft = "3ff364c";
//        String shaRight = "9170a35";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

//        //29th case : 
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/Tamagotchi";
//        String shaLeft = "98bd75d";
//        String shaRight = "c03a1b3";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

////        //30th case 
////        
//        String projectPath = "/Users/gleiph/repositories/dependencies/android_packages_apps_Bluetooth";
//        String shaLeft = "48bd916";
//        String shaRight = "4f491cb";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        

////        //30th case : 6b05dfc4b8595c35d83d34796949dd5d68bba23f
////        
//        String projectPath = "/Users/gleiph/repositories/dependencies/groovy-git";
//        String shaLeft = "0e7864d";
//        String shaRight = "543f54f";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

//                //30th case : 6b05dfc4b8595c35d83d34796949dd5d68bba23f
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/errai";
//        String shaLeft = "47115b3";
//        String shaRight = "883f0b4";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

         //30th case : 
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/imglib2";
//        String shaLeft = "825d7cd";
//        String shaRight = "f56ea5e";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //30th case : 61cde865dc19626fae8d310a6b335fb95794faef
        
//        String projectPath = "/Users/gleiph/repositories/dependencies/nuxeo";
//        String shaLeft = "261757a";
//        String shaRight = "076453d";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        
//        //31th case : 
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/izpack";
//        String shaLeft = "9945dec";
//        String shaRight = "2c53e67";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//        //32th case : error
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/spring-integration";
//        String shaLeft = "965b51d";
//        String shaRight = "9d22d8a";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        //32th case : 3ede547b823a265dd2dddf2e218663bf3e1f4d40 : error
        
//        String projectPath = "/Users/gleiph/repositories/dependencies/TomP2P";
//        String shaLeft = "f9e8dcc";
//        String shaRight = "ba4bae4";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
//32th case : 
//        
//        String projectPath = "/Users/gleiph/repositories/dependencies/Web-Karma";
//        String shaLeft = "8be2f0e";
//        String shaRight = "955d682";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
         
         
//        String projectPath = "/Users/gleiph/repositories/voldemort";
//        String shaLeft = "604acf9";
//        String shaRight = "f1e5ec7";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";

//        String projectPath = "/Users/gleiph/repositories/dependencies/voldemort";
//        String shaLeft = "4d3a194";
//        String shaRight = "3d1c22f";
//        String sanbox = "/Users/gleiph/repositories/dependencies/sandbox";
        
        
        ShowDependencies showDependencies = new ShowDependencies();
        showDependencies.setProjectPath(projectPath);
        showDependencies.setSHALeft(shaLeft);
        showDependencies.setSHARight(shaRight);
        showDependencies.setSandbox(sanbox);

        try {
            showDependencies.show();
        } catch (IOException ex) {
            Logger.getLogger(Strategies.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            MergeDependency dependencies = MergeGuider.performMerge(projectPath, shaLeft, shaRight, sanbox);

            List<NodeDependency> prepare = PrepareNodes.prepare(dependencies);

            for (ConflictingChunkInformation cci : dependencies.getCcis()) {
                System.out.println("CC" + dependencies.getCcis().indexOf(cci));
                System.out.println(cci);
            }

            //Random strategy 
            System.out.println("Random");
            List<ConflictingChunkInformation> resolutionOrder = Strategies.random(dependencies, prepare);

            for (ConflictingChunkInformation chunk : resolutionOrder) {
                int indexOf = dependencies.getCcis().indexOf(chunk);
                System.out.println("CC" + indexOf);
            }

            System.out.println("Sequencial");
            List<ConflictingChunkInformation> sequencial = Strategies.sequencial(dependencies, prepare);

            for (ConflictingChunkInformation chunk : sequencial) {
                System.out.println("CC" + dependencies.getCcis().indexOf(chunk));
            }

            System.out.println("Greedy");
            List<ConflictingChunkInformation> greed = Strategies.greedy(dependencies, prepare);

            for (ConflictingChunkInformation chunk : greed) {
                System.out.println("CC" + dependencies.getCcis().indexOf(chunk));
            }

            System.out.println("Context aware");
            List<ConflictingChunkInformation> contextAware = Strategies.contextAware(dependencies, prepare);

            for (ConflictingChunkInformation chunk : contextAware) {
                System.out.println("CC" + dependencies.getCcis().indexOf(chunk));
            }
        } catch (IOException ex) {
            Logger.getLogger(Strategies.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<ConflictingChunkInformation> random(MergeDependency mergeDependency, List<NodeDependency> nodesDependency) {

        Random rand = new Random();
        List<ConflictingChunkInformation> ccis = new ArrayList<>(mergeDependency.getCcis());
        List<ConflictingChunkInformation> result = new ArrayList<>();

        while (!ccis.isEmpty()) {
            int randomNumber = rand.nextInt(ccis.size());
//            System.out.println("randomNumber = " + randomNumber);

            ConflictingChunkInformation remove = ccis.remove(randomNumber);
            result.add(remove);
        }

        return result;
    }

    public static List<ConflictingChunkInformation> sequencial(MergeDependency mergeDependency, List<NodeDependency> nodesDependency) {

        return mergeDependency.getCcis();
    }

    public static NodeDependency lowestDependencies(List<NodeDependency> nodes) {

        if (nodes.isEmpty()) {
            return null;
        }

        int i = 0;

        NodeDependency result = null;
        NodeDependency currentNode = null;

        //Take the first unvisited node
        for (; i < nodes.size(); i++) {
            currentNode = nodes.get(i);

            if (!currentNode.isVisited()) {
                result = currentNode;
                break;
            }
        }

        //Get the node with lowest number of dependencies 
        for (; i < nodes.size(); i++) {

            currentNode = nodes.get(i);

            if (!currentNode.isVisited()
                    && currentNode.getDependencies() < result.getDependencies()) {
                result = currentNode;
            }

        }

        return result;
    }

    public static List<ConflictingChunkInformation> greedy(MergeDependency mergeDependency, List<NodeDependency> nodesDependency) {

        List<ConflictingChunkInformation> ccis = new ArrayList<>();

        for (ConflictingChunkInformation cci : mergeDependency.getCcis()) {
            ccis.add(new ConflictingChunkInformation(cci));
        }

        ArrayList<NodeDependency> nodes = new ArrayList<>();
        for (NodeDependency node : nodesDependency) {
            nodes.add(new NodeDependency(node));
        }

        List<ConflictingChunkInformation> result = new ArrayList<>();

        for (NodeDependency node : nodes) {
            node.setVisited(false);
        }

        NodeDependency lowestDependencies = lowestDependencies(nodes);
        int indexOf = nodes.indexOf(lowestDependencies);

        while (lowestDependencies != null) {

            ConflictingChunkInformation cci = ccis.get(indexOf);
            result.add(cci);

            nodes.get(indexOf).setVisited(true);

            //Updating dependencies number 
            for (ConflictingChunksDependency dependency : mergeDependency.getConflictingChunksDependencies()) {

                if (dependency.getDependsOn().equals(cci)) {

                    ConflictingChunkInformation reference = dependency.getReference();
                    ConflictingChunkInformation dependsOn = dependency.getDependsOn();
                    int dependencyIndex = ccis.indexOf(reference);
                    int dependentIndex = ccis.indexOf(dependsOn);

                    //updating dependencies
                    int dependencies = nodes.get(dependencyIndex).getDependencies();
                    nodes.get(dependencyIndex).setDependencies(dependencies - 1);

                    //updating dependent
                    int dependent = nodes.get(dependentIndex).getDependent();
                    nodes.get(dependentIndex).setDependent(dependent - 1);

                }

            }

            lowestDependencies = lowestDependencies(nodes);
            indexOf = nodes.indexOf(lowestDependencies);
        }

        return result;
    }

    public static NodeDependency lowestDependenciesWithContext(List<NodeDependency> nodes) {

        if (nodes.isEmpty()) {
            return null;
        }

        int i = 0;

        NodeDependency result = null;
        NodeDependency currentNode = null;

        //Take the first unvisited node
        for (; i < nodes.size(); i++) {
            currentNode = nodes.get(i);

            if (!currentNode.isVisited()) {
                result = currentNode;
                break;
            }
        }

        //Get the node with lowest number of dependencies 
        for (; i < nodes.size(); i++) {

            currentNode = nodes.get(i);

            if (!currentNode.isVisited()
                    && currentNode.getDependencies() < result.getDependencies()) {
                result = currentNode;
            } else if (!currentNode.isVisited()
                    && currentNode.getDependencies() == result.getDependencies()
                    && currentNode.getContextAware() > result.getContextAware()) {
                result = currentNode;
            }

        }

        return result;
    }

    public static List<ConflictingChunkInformation> contextAware(MergeDependency mergeDependency, List<NodeDependency> nodesDependency) {

        List<ConflictingChunkInformation> ccis = new ArrayList<>();

        for (ConflictingChunkInformation cci : mergeDependency.getCcis()) {
            ccis.add(new ConflictingChunkInformation(cci));
        }

        ArrayList<NodeDependency> nodes = new ArrayList<>();
        for (NodeDependency node : nodesDependency) {
            nodes.add(new NodeDependency(node));
        }

        List<ConflictingChunkInformation> result = new ArrayList<>();

        for (NodeDependency node : nodes) {
            node.setVisited(false);
        }

        NodeDependency lowestDependencies = lowestDependenciesWithContext(nodes);
        int indexOf = nodes.indexOf(lowestDependencies);

        while (lowestDependencies != null) {

            ConflictingChunkInformation cci = ccis.get(indexOf);
            result.add(cci);

            nodes.get(indexOf).setVisited(true);

            //Updating dependencies number 
            for (ConflictingChunksDependency dependency : mergeDependency.getConflictingChunksDependencies()) {

                if (dependency.getDependsOn().equals(cci)) {

                    ConflictingChunkInformation reference = dependency.getReference();
                    ConflictingChunkInformation dependsOn = dependency.getDependsOn();
                    int dependencyIndex = ccis.indexOf(reference);
                    int dependentIndex = ccis.indexOf(dependsOn);

                    //updating dependencies
                    int dependencies = nodes.get(dependencyIndex).getDependencies();
                    nodes.get(dependencyIndex).setDependencies(dependencies - 1);

                    int contextAware = nodes.get(dependencyIndex).getContextAware();
                    nodes.get(dependencyIndex).setContextAware(contextAware + 1);

                    //updating dependent
                    int dependent = nodes.get(dependentIndex).getDependent();
                    nodes.get(dependentIndex).setDependent(dependent - 1);

                }

            }

            lowestDependencies = lowestDependenciesWithContext(nodes);
            indexOf = nodes.indexOf(lowestDependencies);
        }

        return result;
    }

}
