/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.ic.mergeguider.dependency.graph;

import br.uff.ic.mergeguider.MergeGuider;
import br.uff.ic.mergeguider.datastructure.ConflictingChunksDependency;
import br.uff.ic.mergeguider.datastructure.MergeDependency;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author gleiph
 */
public class ShowDependencies {

    private String projectPath;
    private String SHALeft;
    private String SHARight;
    private String sandbox;

    public Graph<ConflictingChunkNode, DependencyLink> createGraph(MergeDependency mergeDependency) {
        Graph<ConflictingChunkNode, DependencyLink> graph = new DirectedSparseMultigraph<>();

        if (mergeDependency != null) {

            List<ConflictingChunkNode> nodes = new ArrayList<>();

            for (int i = 1; i <= mergeDependency.getConflictingChunksAmount(); i++) {
                ConflictingChunkNode conflictingChunkNode = new ConflictingChunkNode(i);
                graph.addVertex(conflictingChunkNode);
                nodes.add(conflictingChunkNode);
            }

            for (ConflictingChunksDependency conflictingChunksDependency : mergeDependency.getConflictingChunksDependencies()) {
                int reference = conflictingChunksDependency.getReference();
                int dependsOn = conflictingChunksDependency.getDependsOn();
                String depedencyType = conflictingChunksDependency.getType().toString();

                System.out.println("("+reference+ ", " + dependsOn + ", " + depedencyType+ ")");
                
                graph.addEdge(new DependencyLink(depedencyType), nodes.get(reference), nodes.get(dependsOn), EdgeType.DIRECTED);
            }
        }

        return graph;
    }

    public void show() throws IOException {

        VisualizationViewer<ConflictingChunkNode, DependencyLink> vv;
        Layout<ConflictingChunkNode, DependencyLink> layout;

        MergeDependency performMerge = MergeGuider.performMerge(projectPath, SHALeft, SHARight, sandbox);

        Graph<ConflictingChunkNode, DependencyLink> graph = createGraph(performMerge);

        layout = new CircleLayout<>(graph);
        layout.setSize(new Dimension(800, 800));

        vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(850, 850));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<ConflictingChunkNode>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<DependencyLink>());

        JFrame frame = new JFrame("Dependency graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @return the projectPath
     */
    public String getProjectPath() {
        return projectPath;
    }

    /**
     * @param projectPath the projectPath to set
     */
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    /**
     * @return the SHALeft
     */
    public String getSHALeft() {
        return SHALeft;
    }

    /**
     * @param SHALeft the SHALeft to set
     */
    public void setSHALeft(String SHALeft) {
        this.SHALeft = SHALeft;
    }

    /**
     * @return the SHARight
     */
    public String getSHARight() {
        return SHARight;
    }

    /**
     * @param SHARight the SHARight to set
     */
    public void setSHARight(String SHARight) {
        this.SHARight = SHARight;
    }

    /**
     * @return the sandbox
     */
    public String getSandbox() {
        return sandbox;
    }

    /**
     * @param sandbox the sandbox to set
     */
    public void setSandbox(String sandbox) {
        this.sandbox = sandbox;
    }
}
