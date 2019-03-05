package sample;

import java.util.ArrayList;

public class Edge
{
    public GraphNode from;
    public GraphNode to;

    public GraphNode getFrom() {
        return from;
    }

    public GraphNode getTo() {
        return to;
    }


    public Edge(GraphNode from, GraphNode to) {
        this.from = from;
        this.to = to;
    }
}
