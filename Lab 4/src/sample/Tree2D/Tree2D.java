package sample.Tree2D;

import sample.Point;
import sample.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tree2D {
    private Tree2DNode root;
    private static final short VERTICAL = 0, HORIZONTAL = 1;
    public void buildTree(ArrayList<Point> points) {
        ArrayList<Point> pointsCopy = new ArrayList<>(points);
        root = buildTree(pointsCopy, VERTICAL);


    }
    private int findMedian(List<Point> points, short splitType) {
        if (splitType == VERTICAL) {
            points.sort(Comparator.comparing(Point::getX));
        } else
            points.sort(Comparator.comparing(Point::getY));
        return points.size() / 2;
    }
    private Tree2DNode buildTree(List<Point> points, short splitType) {
        if ( points.size() == 1) {
            return new Tree2DNode(-1, -1, points.get(0));
        } else if (points.size() == 0)
            return null;

        int median = findMedian(points, splitType);
        Point point = points.get(median);
        double x, y;
        short nextSplit;
        if (splitType == VERTICAL) {
            x = point.x;
            y = -1;
            nextSplit = HORIZONTAL;
        } else {
            x = -1;
            y = point.y;
            nextSplit = VERTICAL;
        }
        Tree2DNode node = new Tree2DNode(x, y, point);
        node.left = buildTree(points.subList(0, median), nextSplit);
        node.right = buildTree(points.subList(median + 1, points.size()), nextSplit);
        return node;
    }

    private void findPointsInRectangle(Tree2DNode iter, ArrayList<Point> curPoints, Rectangle rectangle) {
        if (rectangle == null || iter == null)
            return;
        if (rectangle.isPointInside(iter.point))
            curPoints.add(iter.point);

        if (Double.compare(iter.x, -1) == 0 && Double.compare(iter.y, -1) == 0) {
            return;
        }
        Rectangle leftRect = null, rightRect = null;
        if (iter.x == -1) {
            if (rectangle.p2.y < iter.y || Double.compare(rectangle.p2.y, iter.y) == 0) {
                leftRect = rectangle;
            } else if (rectangle.p1.y < iter.y) {
                leftRect = new Rectangle(rectangle.p1, new Point(rectangle.p2.x, iter.y));
                rightRect = new Rectangle(new Point(rectangle.p1.x, iter.y), rectangle.p2);

            } else
                rightRect = rectangle;
        } else {
            if (rectangle.p2.x < iter.x || Double.compare(rectangle.p2.x, iter.x) == 0)
                leftRect = rectangle;
            else if (rectangle.p1.x < iter.x) {
                leftRect = new Rectangle(rectangle.p1, new Point(iter.x, rectangle.p2.y));
                rightRect = new Rectangle(new Point(iter.x, rectangle.p1.y), rectangle.p2);
            } else
                rightRect = rectangle;
        }
        if (leftRect != null)
            findPointsInRectangle(iter.left, curPoints, leftRect);
        if (rightRect != null)
            findPointsInRectangle(iter.right, curPoints, rightRect);
    }
    public ArrayList<Point> findPointsInRectangle(Rectangle rectangle) {
        rectangle.normalize();
        ArrayList<Point> points = new ArrayList<>();
        findPointsInRectangle(root, points, rectangle);
        return points;
    }
}

