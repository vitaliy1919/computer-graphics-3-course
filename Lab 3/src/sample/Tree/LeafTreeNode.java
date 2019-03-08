package sample.Tree;

import sample.Edge;
import sample.GraphNode;

import java.util.ArrayList;
import java.util.TreeSet;

public class LeafTreeNode extends TreeNode {
    public double yMin, yMax;
    public ArrayList<Edge> edges;
    public TreeSet<GraphNode> nodes;

    public LeafTreeNode(ArrayList<Edge> edges, TreeSet<GraphNode> nodes, double yMin, double yMax) {
        this.yMin = yMin;
        this.yMax = yMax;
        this.edges = edges;
        this.nodes = nodes;
    }
}
