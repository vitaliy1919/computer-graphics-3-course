package sample;

import java.util.ArrayList;

public class GraphNode {
    public ArrayList<Edge> outEdges = new ArrayList<>();
    public ArrayList<Edge> inEdges = new ArrayList<>();

    public Point position;

    public GraphNode(Point position) {
        this.position = position;
    }
    public GraphNode(double x, double y) {
        this.position = new Point(x, y);
    }

    public GraphNode() {
    }
}
