/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider;

import br.uff.ic.gems.resources.vcs.Git;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gleiph
 */
public class ThesisExperiments {

    static int project = 1;
    
    public static void main(String[] args) {

        
        String repositoriesPath = "/Users/gleiph/repositories";
        String sandbox = "/Users/gleiph/repositories/icse2";

        List<ExperimentData> cases = new ArrayList<>();
        
        ExperimentData casse = new ExperimentData("https://github.com/RedditAndroidDev/Tamagotchi",
                "21f5e0");

        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/nuxeo/nuxeo",
                "61cde8");
        cases.add(casse);
        
        
        casse = new ExperimentData("https://github.com/usc-isi-i2/Web-Karma",
                "29d83b");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/FasterXML/jackson-databind",
                "059d39");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/alexo/wro4j",
                "05d025");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/atlasapi/atlas",
                "003639");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/AKSW/SAIM",
                "044a3c");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/maxcom/lorsource",
                "159d31");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/enonic/xp",
                "04176d");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/rhomobile/rhostudio",
                "043aa3");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/pentaho/modeler",
                "0587bc");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/apavlo/h-store",
                "00448a");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/spring-projects/spring-data-neo4j",
                "042b1d");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/jponge/izpack",
                "5b45fc");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/MinecraftForge/MinecraftForge",
                "039d92");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/android/platform_packages_apps_camera",
                "04c12f");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/fakemongo/fongo",
                "0033c8");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/mozilla-b2g/android-sdk",
                "058ab2");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/Ineedajob/RSBot",
                "09d43f");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/mkarneim/pojobuilder",
                "09b977");
        cases.add(casse);
        
        
        casse = new ExperimentData("https://github.com/hector-client/hector",
                "010bf7");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/jenkinsci/jira-plugin",
                "063259");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/openMF/mifosx",
                "01368e");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/eclipse/objectteams",
                "08e2a6");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/ndw/xmlcalabash1",
                "00b04c");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/structr/structr",
                "01c205");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/alexo/wro4j",
                "00961b");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/xetorthio/jedis",
                "055032");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/phenoscape/Phenex",
                "0985bf");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/alexo/wro4j",
                "01851e");
        cases.add(casse);
        
        casse = new ExperimentData("https://github.com/OpenMEAP/OpenMEAP",
                "0af9d5");
        cases.add(casse);
        for (ExperimentData aCase : cases) {
            
            try {
                analyze(aCase, repositoriesPath, sandbox);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void analyze(ExperimentData data, String repositoriesPath, String sandbox) throws Exception{
        
        Git git = new Git(repositoriesPath);
        
        String[] split = data.url.split("/");
        String projectName = split[split.length-1];
        
        System.out.println("=================================="+projectName+" "+ project++ +"=============================================");
        File file = new File(repositoriesPath, projectName);
        
        if(!file.isDirectory()){
            System.out.println("Cloning repository " + projectName);
            git.clone(data.url);
        }

        System.out.println("Performing analysis...");
        ProjectAnalysis.mergeAnalysis(file.getAbsolutePath(), data.shaMerge, sandbox, false);
        
    }
    
}

class ExperimentData {

    String url;
    String shaMerge;

    public ExperimentData(String url, String shaMerge) {
        this.url = url;
        this.shaMerge = shaMerge;
    }

}
