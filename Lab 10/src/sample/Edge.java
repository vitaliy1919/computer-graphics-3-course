package sample;

public class Edge {
    public Point start;
    public Point end;

    @Override
    public String toString() {
        return "Edge{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    public Edge(Point start, Point end) {
        this.start = start;
        this.end = end;
    }
}
