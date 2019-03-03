package sample;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Graph {
    static class Helper implements Comparable<Helper>{
        int number;
        Point point;

        @Override
        public int compareTo(Helper helper) {
            if (Double.compare(point.y, helper.point.y) == 0)
                return Double.compare(point.x, helper.point.x);
            return Double.compare(point.y, helper.point.y);
        }

        public Helper(int number, Point point) {
            this.number = number;
            this.point = point;
        }
    }
    public ArrayList<GraphNode> points = new ArrayList<>();
    private ArrayList<Helper> pointMapping;
    public void addEdge(int a, int b) {

        for (int i = 0; i < max(a, b) - points.size() + 1; i++)
            points.add(new GraphNode());
        Edge edge = new Edge(points.get(a), points.get(b), 1);
        points.get(a).outEdges.add(edge);
        points.get(b).inEdges.add(edge);
    }
    public Point getPoint(int i) {
        if (pointMapping == null)
            return points.get(i).position;
        else
            return pointMapping.get(i).point;
    }

    public GraphNode getNode(int i) {
        if (pointMapping == null)
            return points.get(i);
        else
            return points.get(pointMapping.get(i).number);
    }

    public void sortPoints() {
        points.sort((a, b)->{
            int comp = Double.compare(a.position.y, b.position.y);
            if (comp != 0)
                return (comp > 0 ? -1: 1);
            else
                return Double.compare(a.position.x, b.position.x);
        });

        for (GraphNode node: points) {
            node.outEdges.sort((a, b) -> Double.compare(a.to.position.x, b.to.position.x));
            node.inEdges.sort((a, b) -> Double.compare(a.from.position.x, b.from.position.x));

        }
    }

    public void setWeights() {
        for (GraphNode node:points) {
            for (Edge edge:node.outEdges)
                edge.weight = 1;
        }
        for (int i = 1; i < points.size() - 1; i++) {
            GraphNode current = getNode(i);
            if (current.inEdges.size() > current.outEdges.size()) {
                int wInt = 0, wOut = 0;
                for (int j = 0; j < current.inEdges.size(); j++)
                    wInt += current.inEdges.get(j).weight;
                for (int j = 0; j < current.outEdges.size(); j++)
                    wOut += current.outEdges.get(j).weight;
                current.outEdges.get(0).weight = wInt - wOut + 1;
                //GraphNode reverce = points.get(current.outEdges.get(0).to);
            }
        }

        for (int i = points.size() - 2; i >= 1; i--) {
            GraphNode current = getNode(i);
            if (current.outEdges.size() > current.inEdges.size()) {
                int wInt = 0, wOut = 0;
                for (int j = 0; j < current.inEdges.size(); j++)
                    wInt += current.inEdges.get(j).weight;
                for (int j = 0; j < current.outEdges.size(); j++)
                    wOut += current.outEdges.get(j).weight;
                current.inEdges.get(0).weight = wOut - wInt + current.inEdges.get(0).weight;
                //GraphNode reverce = points.get(current.inEdges.get(0).to);
            }
        }
    }

    public  ArrayList<ArrayList<GraphNode>> buildChains() {
        ArrayList<ArrayList<GraphNode>> chains = new ArrayList<>();
        boolean flag= true;
        GraphNode current ;
        do {
            current = points.get(0);
            chains.add(new ArrayList<>());
            boolean nodeFound = false;
            do {
                nodeFound = false;
                for (Edge edge : current.outEdges) {
                    if (edge.weight > 0) {
                        edge.weight--;
                        chains.get(chains.size() - 1).add(current);
                        current = edge.to;
                        nodeFound = true;
                        break;
                    }
                }
                if (!nodeFound)
                    break;
                if (current == points.get(points.size() - 1)) {
                    chains.get(chains.size() - 1).add(current);

                    break;
                }
            } while (true);
            if (!nodeFound) {
                chains.remove(chains.size() - 1);
                flag = false;
            }
        } while (flag);
        return chains;
    }

    private int findPosInChain(ArrayList<GraphNode> chain, Point point) {
        int l = 0;
        int r = chain.size();
        while (r - l >= 2) {
            int m = l + (r - l) / 2 - 1;
            if (chain.get(m).position.y >= point.y && chain.get(m + 1).position.y <= point.y) {
                return m;
            } else if (chain.get(m).position.y < point.y) {
                r = m + 1;
            } else if (chain.get(m + 1).position.y > point.y) {
                l = m + 1;
            }
        }
        return -1;
    }

    public ArrayList<Pair<List<GraphNode>, Integer>> findChainsContainingPoint(ArrayList<ArrayList<GraphNode>> chains, Point point) {
        int l = 0;
        int r = chains.size();
        while (r - l >= 2) {
            int m = l + ( r - l) / 2 - 1;

            int j = 0;
            int firstJ = findPosInChain(chains.get(m), point), secondJ = findPosInChain(chains.get(m + 1), point);
            if (firstJ == -1 || secondJ == -1)
                return null;

            double a = Point.area(point, chains.get(m).get(firstJ).position, chains.get(m).get(firstJ+1).position);
            double b = Point.area(point, chains.get(m + 1).get(secondJ).position, chains.get(m + 1).get(secondJ+1).position);
            if ((a > 0 || abs(a) <= 1e-2) && (b < 0 || abs(b) <= 1e-2)) {
                ArrayList<Pair<List<GraphNode>, Integer>> res = new ArrayList<>();
                int iter1 = 0, iter2 = 0;
                ArrayList<GraphNode> i1 = chains.get(m), i2 = chains.get(m + 1);
                int start = -1, end = -1, start2 = -1, end2 = -1;
                while (iter1 < chains.get(m).size() && iter2 < chains.get(m + 1).size()) {
                    if (i1.get(iter1) == i2.get(iter2)) {
                        if (start == -1) {
                            start = iter1;
                            start2 = iter2;
                        } else {
                            end = iter1;
                            end2 = iter2;
                            if (start <= firstJ && end >= firstJ && start <= firstJ + 1 && end>= firstJ +1) {
                                break;
                            } else {
                                start = iter1;
                                start2 = iter2;
                            }
                        }
                    }
                    if (i1.get(iter1).position.y < i2.get(iter2).position.y)
                        iter2++;
                    else
                        iter1++;
                }
                res.add(new Pair<>(chains.get(m).subList(start, end + 1), firstJ));
                res.add(new Pair<>(chains.get(m + 1).subList(start2, end2 + 1), secondJ));
                return res;
            } else if ( a < 0) {
                r = m + 1;
            } else if (b > 0) {
                l = m + 1;
            }
        }
//        for (int i = 0; i < chains.size() - 1; i++) {
//            int j = 0;
//            int firstJ = -1, secondJ = -1;
//            for (; j < chains.get(i).size() - 1; j++) {
//                if (chains.get(i).get(j).position.y >= point.y && chains.get(i).get(j + 1).position.y <= point.y) {
//                    firstJ = j;
//                    break;
//                }
//            }j = 0;
//            for (; j < chains.get(i + 1).size() - 1; j++) {
//                if (chains.get(i+1).get(j).position.y >= point.y && chains.get(i+1).get(j+1).position.y <= point.y) {
//                    secondJ = j;
//                    break;
//                }
//            }
//            if (firstJ == -1 || secondJ == -1)
//                continue;
//
//            double a = Point.area(point, chains.get(i).get(firstJ).position, chains.get(i).get(firstJ+1).position);
//            double b = Point.area(point, chains.get(i + 1).get(secondJ).position, chains.get(i + 1).get(secondJ+1).position);
//            if ((a > 0 || abs(a) <= 1e-2) && (b < 0 || abs(b) <= 1e-2)) {
//                ArrayList<Pair<List<GraphNode>, Integer>> res = new ArrayList<>();
//                int iter1 = 0, iter2 = 0;
//                ArrayList<GraphNode> i1 = chains.get(i), i2 = chains.get(i + 1);
//                int start = -1, end = -1, start2 = -1, end2 = -1;
//                while (iter1 < chains.get(i).size() && iter2 < chains.get(i + 1).size()) {
//                    if (i1.get(iter1) == i2.get(iter2)) {
//                        if (start == -1) {
//                            start = iter1;
//                            start2 = iter2;
//                        } else {
//                            end = iter1;
//                            end2 = iter2;
//                            if (start <= firstJ && end >= firstJ && start <= firstJ + 1 && end>= firstJ +1) {
//                                break;
//                            } else {
//                                start = iter1;
//                                start2 = iter2;
//                            }
//                        }
//                    }
//                    if (i1.get(iter1).position.y < i2.get(iter2).position.y)
//                        iter2++;
//                    else
//                        iter1++;
//                }
//                res.add(new Pair<>(chains.get(i).subList(start, end + 1), firstJ));
//                res.add(new Pair<>(chains.get(i + 1).subList(start2, end2 + 1), secondJ));
//                return res;
//            }
//
//        }
        return null;
    }
}
