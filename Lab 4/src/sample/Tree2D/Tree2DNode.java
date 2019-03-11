package sample.Tree2D;

import sample.Point;
import sample.Rectangle;

import java.util.ArrayList;

public class Tree2DNode {
    public double x = -1;
    public double y = -1;
    public Tree2DNode left;
    public Tree2DNode right;
    public Point point;

    public Tree2DNode(double x, double y, Point point) {
        this.x = x;
        this.y = y;
        this.point = point;
    }

}
