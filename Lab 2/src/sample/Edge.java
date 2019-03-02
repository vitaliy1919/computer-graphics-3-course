package sample;

import java.util.ArrayList;

public class Edge
{
    public GraphNode from;
    public GraphNode to;
    public int weight;

    public GraphNode getFrom() {
        return from;
    }

    public GraphNode getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    public Edge(GraphNode from, GraphNode to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}
