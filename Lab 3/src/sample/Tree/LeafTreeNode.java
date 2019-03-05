package sample.Tree;

import sample.Edge;
import sample.GraphNode;

import java.util.ArrayList;

public class LeafTreeNode extends TreeNode {
    public Edge leftEdge;
    public Edge rightEdge;
    public ArrayList<Edge> edges = new ArrayList<>();
    public ArrayList<GraphNode> nodes = new ArrayList<>();

    public LeafTreeNode(Edge leftEdge, Edge rightEdge, ArrayList<Edge> edges, ArrayList<GraphNode> nodes) {
        this.leftEdge = leftEdge;
        this.rightEdge = rightEdge;
        this.edges = edges;
        this.nodes = nodes;
    }
}
