package sample;

import javafx.util.Pair;
import sample.Tree.EdgeTreeNode;
import sample.Tree.LeafTreeNode;
import sample.Tree.TreeNode;
import sample.Tree.VerticalTreeNode;
import sun.reflect.generics.tree.Tree;

import java.util.*;

import static java.lang.Math.*;

public class Graph {

    public ArrayList<GraphNode> points = new ArrayList<>();
    private TreeSet<GraphNode> sPoints;
    Comparator<GraphNode> compY = (a,b)->{
        int yComp = Double.compare(a.position.y, b.position.y);
        if (yComp != 0)
            return yComp;
        return Double.compare(a.position.x, b.position.x);
    };
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
                return comp;
            else
                return Double.compare(a.position.x, b.position.x);
        });
    }
    public void sort() {
       //sort(points);

//        for (GraphNode node: points) {
//            node.outEdges.sort((a, b) -> Double.compare(a.to.position.x, b.to.position.x));
//        }
        sPoints = new TreeSet<>(compY);
        sPoints.addAll(points);
        edges.sort((a,b) -> {
            double minA = Math.min(a.to.position.x, a.from.position.x);
            double maxA = Math.min(a.to.position.y, a.from.position.y);
            double minB = Math.min(b.to.position.x, b.from.position.x);
            double maxB = Math.min(b.to.position.y, b.from.position.y);

            int comp =  Double.compare(minA, minB);
            if (comp != 0)
                return comp;
            else
                return Double.compare(maxA, maxB);
        });

    }

    private TreeNode buildTree(ArrayList<ArrayList<Pair<TreeNode, Edge>>> treeNodes, int i,  int l, int r) {
        if (r - l == 1) {
            EdgeTreeNode treeNode = new EdgeTreeNode(treeNodes.get(i).get(l).getValue());
            treeNode.left = treeNodes.get(i).get(l).getKey();
            if (r < treeNodes.get(i).size())
                treeNode.right = treeNodes.get(i).get(r).getKey();
            return treeNode;
        } else if (l >= r)
            return null;
        int m = l + (r - l) / 2;
        EdgeTreeNode root = new EdgeTreeNode(treeNodes.get(i).get(m).getValue());
        root.left = buildTree(treeNodes, i, l, m);
        root.right = buildTree(treeNodes, i, m, r);
        return root;


    }

    public TreeNode split() {
        sort();
        Edge left = edges.get(0), right = edges.get(edges.size() - 1);
        return split(sPoints, edges, left, right);
    }

    public LeafTreeNode findInGraph(TreeNode node, Point point) {

        do {
            if (node instanceof VerticalTreeNode) {
                VerticalTreeNode verticalTreeNode = (VerticalTreeNode)node;
                if (point.y < verticalTreeNode.y)
                    node = node.left;
                else
                    node = node.right;
            } else if (node instanceof EdgeTreeNode) {
                EdgeTreeNode edgeTreeNode = (EdgeTreeNode) node;
                GraphNode up, down;
                if (edgeTreeNode.edge.to.position.y > edgeTreeNode.edge.from.position.y) {
                    up = edgeTreeNode.edge.to;
                    down = edgeTreeNode.edge.from;
                } else {
                    down = edgeTreeNode.edge.to;
                    up = edgeTreeNode.edge.from;
                }
                double area = Point.area(point, down.position, up.position);
                if (area > 0) {
                    node = node.left;
                } else {
                    node = node.right;
                }
            } else if (node instanceof LeafTreeNode) {
                return (LeafTreeNode)node;
            }

        } while (node != null);
        return null;
    }
    public boolean checkIfInside(double yMin, double yMax, GraphNode node) {
        return (node.position.y > yMin || equal(node.position.y, yMin)) &&
                (node.position.y < yMax || equal(node.position.y, yMax));
    }

    public boolean checkIfInsideStrictly(double yMin, double yMax, GraphNode node) {
        return (node.position.y > yMin && node.position.y < yMax );
    }
    private boolean equal(double a, double b) {
        return abs(a - b) < 1e-2;
    }

    private TreeNode split(TreeSet<GraphNode> tNodes, ArrayList<Edge> tEdges, double yDown, double yUp) {
        if (tNodes.isEmpty() || tEdges.size() <= 2 ) {
            return new LeafTreeNode(tEdges, tNodes, yDown, yUp);
        }
        Iterator<GraphNode> iter = tNodes.iterator();
        for (int i = 0; i < tNodes.size() / 2 - 1 ; i++) {
            iter.next();
        }
        double yMedian = iter.next().position.y;


        ArrayList<TreeSet<GraphNode>> nodes = new ArrayList<>(2);
        double[] yMax = {yMedian, tNodes.last().position.y};
        double[] yMin = {tNodes.first().position.y, yMedian};
        nodes.add(new TreeSet<>(compY));
        nodes.add(new TreeSet<>(compY));

        ArrayList<ArrayList<Edge>> edges = new ArrayList<>(2);
        edges.add(new ArrayList<>());
        edges.add(new ArrayList<>());

        ArrayList<Edge> currentLeftEdge = new ArrayList<>(2);
        currentLeftEdge.add(null);
        currentLeftEdge.add(null);

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
                if (checkIfInsideStrictly(yMin[i], yMax[i], curYMin.getValue())  ||
                        checkIfInsideStrictly(yMin[i], yMax[i], curYMax.getValue()) ) {
                    edges.get(i).add(edge);
                    if (checkIfInside(yMin[i], yMax[i], edge.from))
                        nodes.get(i).add(edge.from);

                    if (checkIfInside(yMin[i], yMax[i], edge.to))
                        nodes.get(i).add(edge.to);
                } else if ((curYMin.getKey() < yMin[i] || equal(curYMin.getKey(), yMin[i])) &&
                           (curYMax.getKey() >= yMax[i] || equal(curYMax.getKey(), yMax[i]))){

                    nodes.get(i).add(edge.from);
                    nodes.get(i).add(edge.to);
                    TreeNode leftNode;
                    if (leftNode)
                    leftNode = split(nodes.get(i), edges.get(i), currentLeftEdge.get(i), edge);
                    treeNodes.get(i).add(new Pair<>(leftNode, edge));
                    currentLeftEdge.set(i, edge);

                    nodes.get(i).clear();
                    edges.get(i).clear();
                    nodes.get(i).add(edge.from);
                    nodes.get(i).add(edge.to);
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

//    private TreeNode split(TreeSet<GraphNode> tNodes, ArrayList<Edge> tEdges, Edge left, Edge right) {
//        if (tNodes.isEmpty() || tEdges.size() <= 1 ) {
//            return new LeafTreeNode(left, right, tEdges, tNodes);
//        }
//        Iterator<GraphNode> iter = tNodes.iterator();
//        for (int i = 0; i < tNodes.size() / 2 ; i++) {
//            iter.next();
//        }
//        double yMedian = iter.next().position.y;
//
//
//        ArrayList<TreeSet<GraphNode>> nodes = new ArrayList<>(2);
//        double yMax[] = {yMedian, tNodes.last().position.y };
//        double yMin[] = {tNodes.first().position.y, yMedian};
//        nodes.add(new TreeSet<>(compY));
//        nodes.add(new TreeSet<>(compY));
//        for (int i = 0; i < 2; i++) {
//            nodes.get(i).add(left.from);
//            nodes.get(i).add(left.to);
//
//        }
//        ArrayList<ArrayList<Edge>> edges = new ArrayList<>(2);
//        edges.add(new ArrayList<>());
//        edges.add(new ArrayList<>());
//
//        ArrayList<Edge> currentLeftEdge = new ArrayList<>(2);
//        currentLeftEdge.add(left);
//        currentLeftEdge.add(left);
//
//        ArrayList<ArrayList<Pair<TreeNode, Edge>>> treeNodes = new ArrayList<>(2);
//        treeNodes.add(new ArrayList<>());
//        treeNodes.add(new ArrayList<>());
//
//
//        for (Edge edge: tEdges) {
//            if (edge == left)
//                continue;
//            Pair<Double, GraphNode> curYMax;
//            Pair<Double, GraphNode> curYMin;
//
//            if (edge.to.position.y > edge.from.position.y) {
//                curYMax = new Pair<>(edge.to.position.y, edge.to);
//                curYMin = new Pair<>(edge.from.position.y, edge.from);
//            } else {
//                curYMin = new Pair<>(edge.to.position.y, edge.to);
//                curYMax = new Pair<>(edge.from.position.y, edge.from);
//            }
//            for (int i = 0; i < 2; i++) {
//                if (checkIfInsideStrictly(yMin[i], yMax[i], curYMin.getValue())  ||
//                        checkIfInsideStrictly(yMin[i], yMax[i], curYMax.getValue()) ) {
//                    edges.get(i).add(edge);
//                    if (checkIfInside(yMin[i], yMax[i], edge.from))
//                        nodes.get(i).add(edge.from);
//
//                    if (checkIfInside(yMin[i], yMax[i], edge.to))
//                        nodes.get(i).add(edge.to);
//                } else if ((curYMin.getKey() < yMin[i] || equal(curYMin.getKey(), yMin[i])) && (curYMax.getKey() >= yMax[i] || equal(curYMax.getKey(), yMax[i]))){
////                    if (edge == right && currentLeftEdge.get(i) == left)
////                        continue;
//                    nodes.get(i).add(edge.from);
//                    nodes.get(i).add(edge.to);
//                    TreeNode leftNode = split(nodes.get(i), edges.get(i), currentLeftEdge.get(i), edge);
//                    treeNodes.get(i).add(new Pair<>(leftNode, edge));
//                    currentLeftEdge.set(i, edge);
//
//                    nodes.get(i).clear();
//                    edges.get(i).clear();
//                    nodes.get(i).add(edge.from);
//                    nodes.get(i).add(edge.to);
//                }
//            }
//        }
//        if (treeNodes.get(0).isEmpty() && treeNodes.get(1).isEmpty()) {
//                return new LeafTreeNode(left, right, tEdges, tNodes);
//        }
//
//        TreeNode root = new VerticalTreeNode(yMedian);
//        ArrayList<TreeNode> sideNodes = new ArrayList<>(2);
//        sideNodes.add(null); sideNodes.add(null);
//        for (int i = 0; i < 2; i++) {
//            if (treeNodes.get(i).isEmpty())
//                sideNodes.set(i, new LeafTreeNode(left, right, edges.get(i), nodes.get(i)));
//            else
//                sideNodes.set(i, buildTree(treeNodes, i, 0, treeNodes.get(i).size()));
//        }
//        root.left = sideNodes.get(0);
//        root.right = sideNodes.get(1);
//        return root;
//    }
}
