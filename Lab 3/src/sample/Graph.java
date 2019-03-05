package sample;

import javafx.util.Pair;
import sample.Tree.EdgeTreeNode;
import sample.Tree.LeafTreeNode;
import sample.Tree.TreeNode;
import sample.Tree.VerticalTreeNode;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Graph {

    public ArrayList<GraphNode> points = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();
    public void addEdge(int a, int b) {

        for (int i = 0; i < max(a, b) - points.size() + 1; i++)
            points.add(new GraphNode());
        Edge edge = new Edge(points.get(a), points.get(b));
        points.get(a).outEdges.add(edge);
        edges.add(new Edge(points.get(a), points.get(b)));
    }


    private void sort(ArrayList<GraphNode> nodes) {
        nodes.sort((a, b)->{
            int comp = Double.compare(a.position.y, b.position.y);
            if (comp != 0)
                return (comp > 0 ? -1: 1);
            else
                return Double.compare(a.position.x, b.position.x);
        });
    }
    public void sort() {
       sort(points);

        for (GraphNode node: points) {
            node.outEdges.sort((a, b) -> Double.compare(a.to.position.x, b.to.position.x));
        }

        edges.sort((a,b) -> {
            double minA = Math.min(a.to.position.x, a.from.position.x);
            double minB = Math.min(b.to.position.x, b.from.position.x);
            return Double.compare(minA, minB);
        });

    }

    private TreeNode buildTree(ArrayList<ArrayList<Pair<TreeNode, Edge>>> treeNodes, int i,  int l, int r) {
        if (l == r) {
            return treeNodes.get(i).get(l).getKey();
        }
        int m = l + (r - l) / 2;
        EdgeTreeNode root = new EdgeTreeNode(treeNodes.get(i).get(m).getValue());
        root.left = buildTree(treeNodes, i, l, m);
        root.right = buildTree(treeNodes, i, m + 1, r);
        return root;


    }

    public TreeNode split() {
        sort();
        Edge left = edges.get(0), right = edges.get(edges.size() - 1);
        return split(points, edges, left, right);
    }
    private TreeNode split(ArrayList<GraphNode> tNodes, ArrayList<Edge> tEdges, Edge left, Edge right) {
        double yMedian = tNodes.get(tNodes.size() / 2 ).position.y;


        ArrayList<ArrayList<GraphNode>> nodes = new ArrayList<>(2);
        double yMax[] = {yMedian, tNodes.get(tNodes.size() -1).position.y };
        double yMin[] = {tNodes.get(0).position.y, yMedian};
        nodes.add(new ArrayList<>());
        nodes.add(new ArrayList<>());
        ArrayList<ArrayList<Edge>> edges = new ArrayList<>(2);
        edges.add(new ArrayList<>());
        edges.add(new ArrayList<>());

        ArrayList<Edge> currentLeftEdge = new ArrayList<>(2);
        currentLeftEdge.add(left);
        currentLeftEdge.add(left);

        ArrayList<ArrayList<Pair<TreeNode, Edge>>> treeNodes = new ArrayList<>(2);
        treeNodes.add(new ArrayList<>());
        treeNodes.add(new ArrayList<>());


        for (Edge edge: tEdges) {
            Pair<Double, GraphNode> curYMax;
            Pair<Double, GraphNode> curYMin;

            if (edge.to.position.y > edge.from.position.y) {
                curYMax = new Pair<>(edge.to.position.y, edge.to);
                curYMin = new Pair<>(edge.from.position.y, edge.from);
            } else {
                curYMin = new Pair<>(edge.to.position.y, edge.to);
                curYMax = new Pair<>(edge.from.position.y, edge.from);
            }
            for (int i = 0; i < 2; i++) {
                if (curYMin.getKey() <= yMax[i] && curYMin.getKey() >= yMin[i] ||
                    curYMax.getKey() <= yMax[i] && curYMax.getKey() >= yMin[i]) {
                    edges.get(i).add(edge);
                    nodes.get(i).add(curYMin.getValue());
                } else if (curYMin.getKey() <= yMin[i] && curYMax.getKey() >= yMax[i]) {
                    sort(nodes.get(i));
                    TreeNode leftNode = split(nodes.get(i), edges.get(i), currentLeftEdge.get(i), edge);
                    treeNodes.get(i).add(new Pair<>(leftNode, edge));
                    currentLeftEdge.set(i, edge);

                    nodes.get(i).clear();
                    edges.get(i).clear();
                }
            }
        }
        if (treeNodes.get(0).isEmpty() && treeNodes.get(1).isEmpty()) {
                return new LeafTreeNode(left, right, tEdges, tNodes);
        }

        TreeNode root = new VerticalTreeNode(yMedian);
        ArrayList<TreeNode> sideNodes = new ArrayList<>(2);
        sideNodes.add(null); sideNodes.add(null);
        for (int i = 0; i < 2; i++) {
            if (treeNodes.get(i).isEmpty())
                sideNodes.set(i, new LeafTreeNode(left, right, edges.get(i), nodes.get(i)));
            else
                sideNodes.set(i, buildTree(treeNodes, i, 0, treeNodes.get(i).size()));
        }
        root.left = sideNodes.get(0);
        root.right = sideNodes.get(1);
        return root;
    }
}
